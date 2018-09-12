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
			System.out.println("[server] ������ ���۵Ǿ����ϴ�.");   //Debug
			
			game.start();

			while (true) {
				System.out.println("[server] Ŭ���̾�Ʈ ���� �����");   //Debug
				socket = serversocket.accept();
				System.out.println("[server] : [ " + socket.getInetAddress() + " : " + socket.getPort() + " ] ���� �����Ͽ����ϴ�.");    //Debug
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
			// �α���, ���̵� Ȯ��, ȸ������ ����.
			HashMap<String, String> input = null;
			
			try {
			while (true) {
				System.out.println("[server] ���̵� Ȯ��, ȸ������, �α����� ����  while�� �����߽��ϴ�.");   //Debug
				
				Object tmp = in.readObject();
				if (tmp instanceof HashMap) {
					input = (HashMap<String, String>) tmp;
				}
				System.out.println("[server] Ŭ���̾�Ʈ�κ��� ���� ��û : "+input.get("request"));  //Debug
				System.out.println("[server] Ŭ���̾�Ʈ�κ��� ���� ���̵� : "+input.get("id"));  //Debug
				
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
			// �α��� ����
			System.out.println("[server] �α��ο� �����߽��ϴ�. : "+input.get("id"));  //Debug
			System.out.println("[server] UserVO user : "+user.getId());  //Debug
			user.setSocket(socket);
			user.setOis(in);
			user.setOos(out);
			
			users.put(user.getId(), user);
			System.out.println("[server] Users : "+users);

			
			System.out.println("[server] Chat �����带 �����մϴ�. ");  //Debug
			Chat chat = new Chat(user, game);
			System.out.println("[server] Chat �����带 �����մϴ�. ");  //Debug
			chat.start();
			
			//TODO ��� ���� �� users������ ������ �κ� ���� �ʿ�
		}
		
		public int idCheck(HashMap<String, String> input) {
			System.out.println("[server] ���̵� Ȯ�� ��û : "+input);   //Debug
			int result = service.checkUser(input.get("id"));
			System.out.println("[server] ���̵� Ȯ�� ��� : "+result);   //Debug
			return result;
		}

		public int signup(HashMap<String, String> input) {
			System.out.println("[server] ȸ������ ��û : "+input);   //Debug
			UserVO user = new UserVO(input.get("id"), input.get("pw"));
			int result = service.userregister(user);
			System.out.println("[server] ȸ������ ��� : "+result);   //Debug
			return result;
		}

		public int signin(HashMap<String, String> input) {
			System.out.println("[server] �α��� ��û : "+input);   //Debug
			
			if(users.containsKey(input.get("id"))){ // �α��� �õ� �� �̹� ������ ����� ���� ���
				return 0;
			}
			
			user = service.login(input.get("id"), input.get("pw"));
			
			System.out.println("[server] �α��� ��� : "+user);   //Debug
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
