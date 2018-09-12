package mapia;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Game {
	private ObjectOutputStream out;
	private int gametime = 0;
	private String id;
	private boolean host=false;
	private String status;

	public Game(ObjectOutputStream out, String id) {
		this.out = out;
		this.id=id;
	}
	
	public boolean getHost() {
		return this.host;
	}
	
	public String getId() {
		return this.id;
	}

	public void start() {
		System.out.println("[game] 방에 접속하셨습니다.");   //debug
	}


	public Set<String> init(HashMap<String,String> initInfo) {
		Set<String> keys=initInfo.keySet();
		Iterator<String> it=keys.iterator();
		
		Set<String> userList = new HashSet<>(); 
		
		String key;
		System.out.println("[game] init() keySet : "+keys);   //debug
		while(it.hasNext()) {
			key=it.next();
			if(key.equals("request"));
			else if(key.equals("host")) this.host=Boolean.valueOf(initInfo.get(key)).booleanValue();
			else {
				userList.add(initInfo.get(key));
			}
		}
		
		
		System.out.println("[Game] 나의 정보 : "+id+", "+this.host+", "+status);
		return userList;
	}

	public String sethost(HashMap<String, String> host) {
		if(host.get("id").equals(id)) {
			this.host=true;
		}
		String msg = "[Game] [GM] "+host.get("id")+"님이 방장이 되셨습니다.";
		System.out.println(msg);
		System.out.println("[Game] 나의 host 정보 "+this.host);
		return msg;
	}
	public void sendChat(String msg) { // 채팅 메세지 보내기
		HashMap<String, String> send = new HashMap<>();
		send.put("request","msg");
		send.put("id", id);
		send.put("msg", msg);
		System.out.println("[game] sendChat() msg : "+send);   //debug
		sendMsg(send);
	}

	public void sendStart() { // 게임 시작(호스트가 게임방에 게임시작을 요청)
		HashMap<String, String> send = new HashMap<>();
		send.put("request", "start");
		send.put("id", id);
		System.out.println("[game] 게임시작 호출 sendStart() : "+send);   //debug
		sendMsg(send);
	}

	public void vote(String user) { // 투표
		HashMap<String, String> send = new HashMap<>();
		send.put("request","vote");
		send.put("id", id);
		send.put("vote", user);
		System.out.println("[game] 투표 vote() : "+send);   //debug
		sendMsg(send);
	}

//	public void agree(String agreement) { // 찬반
//		HashMap<String, String> result;
//		HashMap<String, String> send = new HashMap<>();
//		send.put("request","agree");
//		send.put("id", id);
//		send.put("agree", agreement);
//		System.out.println("찬반투표를 하였습니다.");
//		sendMsg(send);
//	}

	public String receiveChat(HashMap<String, String> msgData) { // 채팅 받기
		String id=null;
		String msg=null;
		if(msgData.get("id")!=null) id=msgData.get("id");
		msg=msgData.get("msg");
		System.out.println("[game] 받은 메세지 : ["+id+"] "+msg);  //debug
		
		return new String("[game] 받은 메세지 : ["+id+"] "+msg);
	}

	public void gameStart(String msg) {
		System.out.println("[game] 게임 시작");   //debug
		day();
	}

	public String setStatus(String status) { // 상태 설정
		StringBuilder str=new StringBuilder();
		this.status=status;
		str.append("[GM] ");
		str.append(this.id);
		str.append("은 ");
		str.append(status);
		
		str.append("입니다.");
		System.out.println(str);
		
		return str.toString();
	}


	public synchronized String addUser(String id) { // 유저 들어옴
		StringBuilder str=new StringBuilder();
		str.append("[GM] ");
		str.append(id);
		str.append("님이 들어왔습니다.");
		System.out.println(str);
		
		return str.toString();
		
	}

	public synchronized String removeUser(String id) { // 유저 나감
		StringBuilder str=new StringBuilder();
		str.append("[GM] ");
		str.append(id);
		str.append("님이 나갔습니다.");
		System.out.println(str);
		return str.toString();
	}

	public void dieUser(String id) { // 유저 죽음
		StringBuilder str=new StringBuilder();
		str.append("[GM] ");
		str.append(id);
		str.append("님이 죽었습니다.");
		System.out.println(str);
	}

	public void requestVote() { // 투표 시작 요청
		StringBuilder str=new StringBuilder();
		str.append("[GM] ");
		str.append("투표가 시작되었습니다.");
		str.append("죽일 사람을 선택하세요.");
		System.out.println(str);
	}

	public void night() {            //밤시작
		StringBuilder str=new StringBuilder();
		str.append("[GM] ");
		str.append("밤이 시작되었습니다.");
		System.out.println(str);
	}
	public void day() {           //낮 시작
		StringBuilder str=new StringBuilder();
		str.append("[GM] ");
		str.append("낮이 시작되었습니다.");
		System.out.println(str);
	}

	public void gameEnd() {
		StringBuilder str=new StringBuilder();
		str.append("[GM] ");
		str.append("게임이 종료되었습니다.");
		System.out.println(str);
	}
//	public void requestAgree(String msg) { // 동의 시작 요청
//
//	}

	public void sendMsg(HashMap<String, String> send) { // 데이터를 보내고 결과를 기다리지 않음.
		try {
			out.writeObject(send);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
