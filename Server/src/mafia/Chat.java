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
		System.out.println("[Chat] ������  : " + user + ", " + game); // Debug
		this.user = user;
		this.game = game;
		socket = user.getSocket();
		in = user.getOis();
		out = user.getOos();
	}

	void sendToAll(HashMap<String, String> sendData) { // ��ü
		send(game.getAll(), sendData);
	}

	void sendToDead(HashMap<String, String> sendData) { // ����
		send(game.getDead(), sendData);
	}

	void sendToMafia(HashMap<String, String> sendData) { // ���Ǿ�
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

			// �濡 ���� ����� �ڽ��� ȣ��Ʈ ������ ���� �濡 ���� �ִ��� �𸣱� ������ ȣ��Ʈ ����, ���� �濡 �ִ� ������� ��������
			// Ŭ���̾�Ʈ���� ������.
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

			while (true) { // ���� �濡 ����

				HashMap<String, String> data = new HashMap<String, String>();

				// ���� ��� ����
				while (!game.game) {
					System.out.println("[Chat] ��⸦ ���� while��"); // Debug
					Object obj = in.readObject();
					data = (HashMap<String, String>) obj;
					System.out.println("[Chat] ��� ���¿��� ���� data : " + data);
					if (data.get("request").equals("start")) { // Ŭ���̾�Ʈ���� ���� ������ Start�� ��
						game.game = true;
						break;
					} else {
						if (!game.game) {
							sendToAll(data);
						}
						data.clear();
					}
				}

				// ���� ���� ����
				while (game.game) {
					System.out.println("[Chat] ���Ӹ� ���� while�� "); // Debug
					Object obj = in.readObject();
					data = (HashMap<String, String>) obj;
					System.out.println("[Chat] ���� ���¿��� ���� data : " + data);

					if (game.day) { // ���� ��

						if (data.get("request").equals("msg")) { // Ŭ���̾�Ʈ���� ���� ������ �޼����� ��

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

						if (data.get("request").equals("vote")) { // Ŭ���̾�Ʈ���� ���� ������ ��ǥ�� ��
							if (game.getLives().containsKey(data.get("vote"))) {
								game.getLives().get(data.get("vote")).countVote();
							} else {
								System.out.println("[Chat] ��ǥ����");
							}
						}

					} // ��

					if (!game.day) { // ���� ��

						if (data.get("request").equals("msg")) { // Ŭ���̾�Ʈ���� ���� ������ �޼����� ��

							if (game.getCitizen().containsKey(data.get("id"))) {
							}

							if (game.getMafia().containsKey(data.get("id"))) {
								sendToMafia(data);
							}

							if (game.getDead().containsKey(data.get("id"))) {
								sendToDead(data);
							}
						}

						if (data.get("request").equals("vote")) { // Ŭ���̾�Ʈ���� ���� ������ ��ǥ�� ��
							if (game.getLives().containsKey(data.get("vote"))) {
								game.getLives().get(data.get("vote")).countVote();
							} else {
								System.out.println("[Chat] ��ǥ����");
							}
						}

					} // ��
				} // while ����
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sendToAll(toMap3("removeUser", "id", id)); // ������ ��� ���� ����
			game.removeUser(user); // �濡�� ���� ��Ŵ

			if (user.isHost()) { // ������ ���� �� ������ �ο����� ���� ���� �ο�
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

			System.out.println("[ " + socket.getInetAddress() + " : " + socket.getPort() + " ] ���� ������ �����Ͽ����ϴ�.");

			System.out.println("���� ���������� ���� " + game.getAll().size() + "�Դϴ�.");

		}
	}

}
