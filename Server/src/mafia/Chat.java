package mafia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import comm.UserVO;

public class Chat extends Thread {

	Game game;
	Map<String, UserVO> users;
	UserVO user;
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;

	public Chat(UserVO user, Game game) {
		System.out.println("[Chat] 생성자  : " + user + ", " + game); // Debug
		this.user = user;
		this.game = game;
		socket = user.getSocket();
		in = user.getOis();
		out = user.getOos();
	}

	void sendToAll(HashMap<String, String> sendData) { // 전체
		send(game.getAll(), sendData);
	}

	void sendToDead(HashMap<String, String> sendData) { // 죽음
		send(game.getDead(), sendData);
	}

	void sendToMafia(HashMap<String, String> sendData) { // 마피아
		send(game.getMafia(), sendData);
	}

	private void send(HashMap<String, UserVO> users, HashMap<String, String> sendData) {
		Set<String> keys = users.keySet();
		Iterator<String> it = keys.iterator();

		Collections.synchronizedMap(sendData);

		while (it.hasNext()) {
			String key = it.next();
			UserVO user = users.get(key);
			ObjectOutputStream oos = user.getOos();
			try {
				oos.writeObject(sendData);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, String> toMsg(String msg) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("request", "msg");
		map.put("msg", msg);
		return map;
	}

	public HashMap<String, String> toMap1(String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("request", value);
		return map;
	}

	public HashMap<String, String> toMap2(String key, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("request", key);
		map.put(key, value);
		return map;
	}

	public HashMap<String, String> toMap3(String method, String key, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("request", method);
		map.put(key, value);
		return map;
	}

	public void run() {
		String id = user.getId();
		game.addUser(user);

		sendToAll(toMap3("addUser", "id", id));
		try {
			if (game.getAll().size() == 1) {
				user.setHost(true);
			}

			// 방에 들어온 사람은 자신의 호스트 정보와 현재 방에 누가 있는지 모르기 떄문에 호스트 정보, 현재 방에 있는 사람들이 누구인지
			// 클라이언트에게 보내줌.
			HashMap<String, String> initInfo = new HashMap<>();
			HashMap<String, UserVO> all = game.getAll();
			initInfo.put("request", "init");
			initInfo.put("host", String.valueOf(user.isHost()));
			Set<String> keys = all.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				initInfo.put(key, all.get(key).getId());
			}
			out.writeObject(initInfo);

			while (true) { // 게임 방에 진입

				HashMap<String, String> data = new HashMap<String, String>();

				// 게임 대기 상태
				while (!game.game) {
					System.out.println("[Chat] 대기를 위한 while문"); // Debug
					Object obj = in.readObject();
					data = (HashMap<String, String>) obj;
					System.out.println("[Chat] 대기 상태에서 받은 data : " + data);
					if (data.get("request").equals("start")) { // 클라이언트한테 받은 정보가 Start일 때
						game.game = true;
						break;
					} else {
						if (!game.game) {
							sendToAll(data);
						}
						data.clear();
					}
				}

				// 게임 시작 상태
				while (game.game) {
					System.out.println("[Chat] 게임를 위한 while문 "); // Debug
					Object obj = in.readObject();
					data = (HashMap<String, String>) obj;
					System.out.println("[Chat] 게임 상태에서 받은 data : " + data);

					if (game.day) { // 낮일 때

						if (data.get("request").equals("msg")) { // 클라이언트한테 받은 정보가 메세지일 때

							// if (data.get("status").equals("citizen")) {
							// sendToAll(data);
							// }
							//
							// if (data.get("status").equals("mafia")) {
							// sendToAll(data);
							// }
							//
							// if (data.get("status").equals("dead")) {
							// sendToAll(data);
							// }
							if (game.getDead().containsKey(data.get("id")))
								sendToDead(data);
							else
								sendToAll(data);
						}

						if (data.get("request").equals("vote")) { // 클라이언트한테 받은 정보가 투표일 때
							if (game.getLives().containsKey(data.get("vote"))) {
								game.getLives().get(data.get("vote")).countVote();
							} else {
								System.out.println("[Chat] 투표실패");
							}
						}

					} // 낮

					if (!game.day) { // 밤일 때

						if (data.get("request").equals("msg")) { // 클라이언트한테 받은 정보가 메세지일 때

							if (game.getCitizen().containsKey(data.get("id"))) {
							}

							if (game.getMafia().containsKey(data.get("id"))) {
								sendToMafia(data);
							}

							if (game.getDead().containsKey(data.get("id"))) {
								sendToDead(data);
							}
						}

						if (data.get("request").equals("vote")) { // 클라이언트한테 받은 정보가 투표일 때
							if (game.getLives().containsKey(data.get("vote"))) {
								game.getLives().get(data.get("vote")).countVote();
							} else {
								System.out.println("[Chat] 투표실패");
							}
						}

					} // 밤
				} // while 게임
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sendToAll(toMap3("removeUser", "id", id)); // 퇴장한 사람 정보 전송
			game.removeUser(user); // 방에서 퇴장 시킴

			if (user.isHost()) { // 방장이 나갈 시 임의의 인원에게 방장 권한 부여
				Iterator<String> it = game.getAll().keySet().iterator();
				HashMap<String, String> sendData = new HashMap<String, String>();

				int host = (int) (Math.random() * game.getAll().size());
				int count = 0;

				while (it.hasNext()) {
					String key = (String) it.next();
					if (host == count++) {
						sendData.put("request", "host");
						sendData.put("id", game.getAll().get(key).getId());
						game.getAll().get(key).setHost(true);
						sendToAll(sendData);
					}
				}
			}

			System.out.println("[ " + socket.getInetAddress() + " : " + socket.getPort() + " ] 에서 접속을 종료하였습니다.");

			System.out.println("현재 서버접속자 수는 " + game.getAll().size() + "입니다.");

		}
	}

}
