package comm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserVO implements Comparable<UserVO> {

	private String id;
	private String pw;
	private boolean host = false;
	private boolean life = true;
	private int vote = 0;
	
	private int win;
	private int lose;
	private int rate;
	
	private Socket socket;
	ObjectOutputStream Oos = null;
	ObjectInputStream Ois = null;
	
	public UserVO(String id) {
		super();
		this.id = id;
	}
	
	public UserVO(String id, String pw) {
		super();
		this.id = id;
		this.pw=pw;
	}
	
	public UserVO() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	
	
	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLose() {
		return lose;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public boolean isHost() {
		return host;
	}
	public void setHost(boolean host) {
		this.host = host;
	}
	public boolean isLife() {
		return life;
	}

	public void setLife(boolean life) {
		this.life = life;
	}
	
	public int getVote() {
		return vote;
	}

	public void countVote() {
		++this.vote;
	}
	
	public void resetVote() {
		this.vote = 0;
	} 


	public void setOos(ObjectOutputStream oos) {
		Oos = oos;
	}

	public void setOis(ObjectInputStream ois) {
		Ois = ois;
	}

	public ObjectInputStream getOis(){
		return Ois;
	}
	public ObjectOutputStream getOos(){
		return Oos;
	}
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserVO other = (UserVO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(UserVO o) {
		return o.vote - vote;
	}
}
