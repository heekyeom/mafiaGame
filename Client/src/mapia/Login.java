package mapia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Login {
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public Login(ObjectInputStream in, ObjectOutputStream out) {
		this.in=in;
		this.out=out;
	}

	public String idCheck(String id) { // id 확인 끝
		HashMap<String, String> result;
		HashMap<String, String> send = new HashMap<>();
		send.put("request", "idCheck");
		send.put("id", id);
		result = sendData(send);

		return result.get("result");
	}

	public String signup(String id, String pw) { // 회원가입 확인 끝
		

		HashMap<String, String> result;
		HashMap<String, String> send = new HashMap<>();
		send.put("request", "signup");
		send.put("id", id);
		send.put("pw", pw);
		result = sendData(send);

		return result.get("result");
	}

	public String signin(String id, String pw) { // 로그인 끝.
		HashMap<String, String> result;
		HashMap<String, String> send = new HashMap<>();
		send.put("request", "signin");
		send.put("id", id);
		send.put("pw", pw);
		result = sendData(send);
		return result.get("result");
	}
	public HashMap<String, String> sendData(HashMap<String, String> send) { // 데이터를 보내고 결과를 기다림.
		HashMap<String, String> result = null;

		try {
			out.writeObject(send);
			Object tmp = in.readObject();
			if (tmp instanceof HashMap<?, ?>)
				result = (HashMap<String, String>) tmp;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}
