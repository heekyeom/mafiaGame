package mafia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import comm.UserVO;
import service.UserService;
import service.UserServiceImpl;

public class Server extends Thread {
	
	UserService service = new UserServiceImpl();
	UserVO user;
	Map<String, UserVO> users;
	Game game;
	
	public void run() {
		ServerSocket serversocket = null;
		Socket socket = null;
		game = new Game();
		users = new HashMap<String, UserVO>();
		
		Collections.synchronizedMap(users);
		
		try {
			serversocket = new ServerSocket(7777);
			System.out.println("[server] 서버가 시작되었습니다.");   //Debug
			
			game.start();

			while (true) {
				System.out.println("[server] 클라이언트 접속 대기중");   //Debug
				socket = serversocket.accept();
				System.out.println("[server] : [ " + socket.getInetAddress() + " : " + socket.getPort() + " ] 에서 접속하였습니다.");    //Debug
				ServerReceiver serverReceiver = new ServerReceiver(socket);
				serverReceiver.start();

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}
	
	public class ServerReceiver extends Thread{
		
		Socket socket;
		ObjectOutputStream out;
		ObjectInputStream in;

		public ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		public void run(){
			// 로그인, 아이디 확인, 회원가입 시작.
			HashMap<String, String> input = null;
			
			try {
			while (true) {
				System.out.println("[server] 아이디 확인, 회원가입, 로그인을 위한  while문 진입했습니다.");   //Debug
				
				Object tmp = in.readObject();
				if (tmp instanceof HashMap) {
					input = (HashMap<String, String>) tmp;
				}
				System.out.println("[server] 클라이언트로부터 받은 요청 : "+input.get("request"));  //Debug
				System.out.println("[server] 클라이언트로부터 받은 아이디 : "+input.get("id"));  //Debug
				
				HashMap<String, String> result = request(input);
				if (input.get("request").equals("signin") && result.get("result").equals("1")) {
					out.writeObject(result); 
					break;
				} else {
					out.writeObject(result);
				}
			}
			} catch(Exception e) {
				e.printStackTrace();
			}
			// 로그인 성공
			System.out.println("[server] 로그인에 성공했습니다. : "+input.get("id"));  //Debug
			System.out.println("[server] UserVO user : "+user.getId());  //Debug
			user.setSocket(socket);
			user.setOis(in);
			user.setOos(out);
			
			users.put(user.getId(), user);
			System.out.println("[server] Users : "+users);

			
			System.out.println("[server] Chat 쓰레드를 생성합니다. ");  //Debug
			Chat chat = new Chat(user, game);
			System.out.println("[server] Chat 스레드를 시작합니다. ");  //Debug
			chat.start();
			
			//TODO 사람 퇴장 시 users에서도 빠지는 부분 구현 필요
		}
		
		public int idCheck(HashMap<String, String> input) {
			System.out.println("[server] 아이디 확인 요청 : "+input);   //Debug
			int result = service.checkUser(input.get("id"));
			System.out.println("[server] 아이디 확인 결과 : "+result);   //Debug
			return result;
		}

		public int signup(HashMap<String, String> input) {
			System.out.println("[server] 회원가입 요청 : "+input);   //Debug
			UserVO user = new UserVO(input.get("id"), input.get("pw"));
			int result = service.userregister(user);
			System.out.println("[server] 회원가입 결과 : "+result);   //Debug
			return result;
		}

		public int signin(HashMap<String, String> input) {
			System.out.println("[server] 로그인 요청 : "+input);   //Debug
			
			if(users.containsKey(input.get("id"))){ // 로그인 시도 시 이미 접속한 사람이 있을 경우
				return 0;
			}
			
			user = service.login(input.get("id"), input.get("pw"));
			
			System.out.println("[server] 로그인 결과 : "+user);   //Debug
			if (user == null)
				return 0;
			return 1;
		}

		public HashMap<String, String> request(HashMap<String, String> input) {
			String requestMethod = input.get("request");
			int result = 0;

			if (requestMethod.equals("idCheck"))
				result = idCheck(input);
			else if (requestMethod.equals("signup"))
				result = signup(input);
			else if (requestMethod.equals("signin"))
				result = signin(input);

			HashMap<String, String> output = new HashMap<String, String>();
			output.put("result", String.valueOf(result));

			return output;
		}

	}
}
