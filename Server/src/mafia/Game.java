package mafia;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import comm.UserVO;

public class Game extends Thread {
	private HashMap<String, UserVO> all;
	private HashMap<String, UserVO> lives;
	private HashMap<String, UserVO> citizen;
	private HashMap<String, UserVO> mafia;
	private HashMap<String, UserVO> dead;
	private String[] jobs;
	public boolean day = true;
	public boolean game = false;
	private int time;
	Server server;
	GameSetting gs = null;

	static int count = 0;

	Game() {
		all = new HashMap<String, UserVO>();
		lives = new HashMap<String, UserVO>();
		citizen = new HashMap<String, UserVO>();
		mafia = new HashMap<String, UserVO>();
		dead = new HashMap<String, UserVO>();
		gs = new GameSetting();
	}

	public void run() {
		try {
			while (true) {
				gameWait();
				gameStart();
				while (game) {
					day();
					vote();
					// argument();
					// agree();
					gamecheck();
					if(game == false) {
						break;
					}
					night();
					vote();
//					if (gs.getPolice() != null) {
//						vote();
//					}
//					if (gs.getDoctor() != null) {
//						vote();
//					}
					gamecheck();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}

	public void addUser(UserVO user) {
		all.put(user.getId(), user);
		lives.put(user.getId(), user);
	}

	public void removeUser(UserVO user) {
		all.remove(user.getId());
		lives.remove(user.getId());
	}

	public HashMap<String, UserVO> getAll() {
		return all;
	}

	public HashMap<String, UserVO> getCitizen() {
		return citizen;
	}

	public HashMap<String, UserVO> getMafia() {
		return mafia;
	}

	public HashMap<String, UserVO> getDead() {
		return dead;
	}

	public HashMap<String, UserVO> getLives() {
		return lives;
	}

	void sendToAll(HashMap<String, String> sendData) { // 전체
		send(all, sendData);
	}

	void sendToDead(HashMap<String, String> sendData) { // 죽음
		send(dead, sendData);
	}

	void sendToMafia(HashMap<String, String> sendData) { // 마피아
		send(mafia, sendData);
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

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void showAliveName() {
		int count = 1;
		
		HashMap<String, String> sendData = new HashMap<String, String>();
		Iterator<String> itr = lives.keySet().iterator();

		while (itr.hasNext()) {
			String key = (String) itr.next();
			sendData.put("request", "msg");
			sendData.put("id", "GM");
			sendData.put("msg", count++ + ". " + key);
			if (day) {
				sendToAll(sendData);
			}
			if (!day) {
				sendToMafia(sendData);
			}
			sendData.clear();
			sendData = new HashMap<String, String>();
		}
		sendData.clear();
	}

	public void showAll() {
		HashMap<String, String> sendData = new HashMap<String, String>();
		
		for(int i = 0 ; i < jobs.length ; i++) {
			sendData.put("request", "msg");
			sendData.put("msg", jobs[i]);
			sendData.put("id", "GM");
			sendToAll(sendData);
			sendData.clear();
			sendData = new HashMap<String, String>();
		}
		sendData.clear();
		
	}

	private void gameEnd() {
		System.out.println(" === Game End === "); // Debug
		
		day = true;
		game = false;
		
		System.out.println("gameEnd() : " + game); // Debug
		
		HashMap<String, String> sendData = new HashMap<String, String>();
		sendData.put("request", "gameEnd");
		sendToAll(sendData);
		sendData.clear();
		showAll();
		
		mafia.clear();
		citizen.clear();
		dead.clear();
		lives.clear();
		
		mafia = new HashMap<String, UserVO>();
		citizen = new HashMap<String, UserVO>();
		dead = new HashMap<String, UserVO>();
		lives = new HashMap<String, UserVO>();
		
		Iterator<String> itr = all.keySet().iterator(); // 전체 플레이어에서 
		System.out.println("[Game] gameEnd() Iterator setting"); // Debug
		while (itr.hasNext()) {
			String key = (String) itr.next();
			try {
				System.out.println("[Game] gameEnd() while 진입"); // while
				
				lives.put(key, all.get(key));

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sendData.clear();
			}

		}
		
		
		
	}

	private void gamecheck() {
		if (mafia.size() >= citizen.size() || mafia.size() == 0)
			gameEnd();
	}

	private void night() {
		day = false;
		HashMap<String, String> sendData = new HashMap<String, String>();
		
		sendData.put("request", "night");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();
		
		sendData.put("request", "msg");
		sendData.put("id", "GM");
		sendData.put("msg", "밤이되었습니다.");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();
		
		sendData.put("request", "msg");
		sendData.put("id", "GM");
		sendData.put("msg", "마피아들은 90초간 죽일 사람을 상의해주세요.");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();

//		try {
//			Thread.sleep(45000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		sendData.put("request", "msg");
		sendData.put("id", "GM");
		sendData.put("msg", "15초 남았습니다..");
		sendToAll(sendData);
		sendData.clear();

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// private void agree() {
	// HashMap<String, String> sendData= new HashMap<String,String>();
	// sendData.put("request", "msg");
	// sendData.put("id", "GM");
	// sendData.put("msg", "찬반투표를 시작하겠습니다.");
	// sendToAll(sendData);
	// sendData.clear();
	//
	// sendData.put("request", "agree");
	// sendToAll(sendData);
	// sendData.clear();
	//
	// }

	// private void argument() {
	// HashMap<String, String> sendData= new HashMap<String,String>();
	// sendData.put("request", "msg");
	// sendData.put("id", "GM");
	// sendData.put("msg", "투표가 종료되었습니다.");
	// sendToAll(sendData);
	// sendData.clear();
	//
	// String result = "";
	// sendData.put("request", "msg");
	// sendData.put("id", "GM");
	// sendData.put("msg", result);
	// sendToAll(sendData);
	// sendData.clear();
	//
	// sendData.put("request", "msg");
	// sendData.put("id", "GM");
	// sendData.put("msg", "반론을 시작하세요");
	// sendToAll(sendData);
	// sendData.clear();
	//
	// }

	private void vote() {
		HashMap<String, String> sendData = new HashMap<String, String>();
		sendData.put("request", "vote");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();

		showAliveName();

		sendData.put("request", "msg");
		sendData.put("id", "GM");
		sendData.put("msg", "15초 이내로 투표를 진행해주세요");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();

		try { // 15초간 대기
			Thread.sleep(15000);
		} catch (InterruptedException e) {
		}

		// 살아있는 사람들을 ArrayList에 넣은 뒤 투표수를 기준으로 내림차순 정렬
		List<UserVO> list = new ArrayList<UserVO>();

		Iterator<String> itr = lives.keySet().iterator();

		while (itr.hasNext()) {
			String key = (String) itr.next();
			list.add(lives.get(key));
		}

		Collections.sort(list, new VoteDescCompare());

		// 득표율 1위와 2위를 비교 시 1위가 클 경우
		if (list.get(0).getVote() > list.get(1).getVote()) {
			
			lives.remove(list.get(0).getId());
			
			if(citizen.containsKey(list.get(0).getId())) {
				citizen.remove(list.get(0).getId());
			} else {
				mafia.remove(list.get(0).getId());
			}
			
			dead.put(list.get(0).getId(), list.get(0));

			sendData.put("request", "msg");
			sendData.put("id", "GM");
			sendData.put("msg", "투표가 종료되었습니다.\r\n" + list.get(0).getId() + "님이 사망하셨습니다.");
			sendToAll(sendData);
			sendData.clear();
			sendData = new HashMap<String, String>();
			
			
			try {
				sendData.put("request", "setStatus");
				sendData.put("status", "사망");
				list.get(0).getOos().writeObject(sendData);
				sendData.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}

		// 득표율 1위와 2위를 비교 시 같을경우
		if (list.get(0).getVote() == list.get(1).getVote()) {
			sendData.put("request", "msg");
			sendData.put("id", "GM");
			sendData.put("msg", "투표가 종료되었습니다.\r\n투표율이 같아 아무도 사망하지 않았습니다.");
			sendToAll(sendData);
			sendData.clear();
		}

		itr = lives.keySet().iterator();

		while (itr.hasNext()) {
			String key = (String) itr.next();
			lives.get(key).resetVote();
		}
		list.clear();
	}

	private void day() {
		day = true;
		HashMap<String, String> sendData = new HashMap<String, String>();
		
		sendData.put("request", "day");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();
		
		sendData.put("request", "msg");
		sendData.put("id", "GM");
		sendData.put("msg", "낮이 되었습니다.");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();
		
		sendData.put("request", "msg");
		sendData.put("id", "GM");
		sendData.put("msg", "대화를 90초간 시작해주세요.");
		sendToAll(sendData);
		sendData.clear();
		sendData = new HashMap<String, String>();

//		try {
//			Thread.sleep(45000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		sendData.put("request", "msg");
		sendData.put("id", "GM");
		sendData.put("msg", "15초 남았습니다..");
		sendToAll(sendData);
		sendData.clear();

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void gameStart() { // 게임 시작.
		System.out.println("[Game] gameStart() 돌입"); // Debug
		HashMap<String, String> sendData = new HashMap<String, String>();
		sendData.put("request", "start");
		sendToAll(sendData); // Client의 gameWait() 에서 탈출 시킴
		sendData.clear();
		int count  =  0;
		jobs = new String[all.size()];

		gs.roleSet(all.size(), all);
		
		sendData = new HashMap<String, String>();
		
		Iterator<String> itr = all.keySet().iterator(); // 전체 플레이어에서 
		System.out.println("[Game] gameStart() Iterator setting"); // Debug
		while (itr.hasNext()) {
			String key = (String) itr.next();
			try {
				System.out.println("[Game] gameStart() while 진입"); // while
				
				if (mafia.containsKey(key)) {
					sendData.put("request", "status");
					sendData.put("status", "마피아");
					mafia.get(key).getOos().writeObject(sendData);
					System.out.println("[Game] gameStart() " + sendData.get("status") + " : " + mafia.get(key).getId());
					
					jobs[count++] = count + ". " + key + " : 마피아";
					
				} else {
					sendData.put("request", "status");
					sendData.put("status", "시민");
					citizen.get(key).getOos().writeObject(sendData);
					System.out.println("[Game] gameStart() " + sendData.get("status") +  " : " + citizen.get(key).getId());
					
					jobs[count++] = count + ". " + key + " : 시민";
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sendData.clear();
			}

		}

	}

	private void gameWait() {
		System.out.println("[Game] gameWait() 돌입"); // Debug
		while (!game) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[Game] gameWait() 탈출");
	}

	class TimeCount extends Thread {
		@Override
		public void run() {
			while (time-- == 0) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public class GameSetting {

		private int day_time = 120;
		private int vote__time = 10;
		private int argument_time = 30;
		private int agree_time = 10;
		private int night_time = 30;

		public GameSetting() {
		}
		
		public HashMap<String,UserVO> getAll() {
			return all;
		}

		public HashMap<String,UserVO> getMafia() {
			return mafia;
		}

		public HashMap<String,UserVO> getCitizen() {
			return citizen;
		}
		
		public HashMap<String,UserVO> getDead() {
			return dead;
		}
		
		public HashMap<String,UserVO> getLives() {
			return lives;
		}

//		public UserVO getPolice() {
//			return police;
//		}
//
//		public UserVO getDoctor() {
//			return doctor;
//		}

		public int getDay_time() {
			return day_time;
		}

		public void setDay_time(int day_time) {
			this.day_time = day_time;
		}

		public int getVote__time() {
			return vote__time;
		}

		public void setVote__time(int vote__time) {
			this.vote__time = vote__time;
		}

		public int getArgument_time() {
			return argument_time;
		}

		public void setArgument_time(int argument_time) {
			this.argument_time = argument_time;
		}

		public int getAgree_time() {
			return agree_time;
		}

		public void setAgree_time(int agree_time) {
			this.agree_time = agree_time;
		}

		public int getNight_time() {
			return night_time;
		}

		public void setNight_time(int night_time) {
			this.night_time = night_time;
		}

		public void roleSet(int size, HashMap<String, UserVO> all) {

			int count = 0;
			int mafia = 0;
			int police = 0;
			int doctor = 0;
			int[] arr = new int[size];

			mafia = (int) (size * 0.3); // 마피아 수

			int i = 0;
			for (; i < mafia; i++) {
				arr[i] = -1;
			}

//			if (size >= 6) { // 경찰과 의사
//				arr[i] = 2;
//				i++;
//				arr[i] = 3;
//				i++;
//			}

			for (; i < size; i++) {
				arr[i] = 1;
			}

			i = 0;

			for (; i < size; i++) {
				int random = (int) (Math.random() * size);
				int temp = arr[i];
				arr[i] = arr[random];
				arr[random] = temp;
			}

			Iterator<String> it = all.keySet().iterator();

			int index = 0;
			while (it.hasNext()) {
				String key = it.next();
				all.put(key, all.get(key));
				if (arr[index] == -1) {
					getMafia().put(key,all.get(key));
				} else if (arr[index] == 1) {
					getCitizen().put(key,all.get(key));
				}
				index++;
			}
		}
	}
	
}

class VoteDescCompare implements Comparator<UserVO> {
	/**
	 * 내림차순(DESC)
	 */
	@Override
	public int compare(UserVO user1, UserVO user2) {
		// TODO Auto-generated method stub
		return user1.getVote() > user2.getVote() ? -1 : user1.getVote() < user2.getVote() ? 1 : 0;
	}

}