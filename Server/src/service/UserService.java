package service;

import comm.UserVO;

public interface UserService {
	public int searchWin(String id);
	public int updateWin(String id, int wins);
	public int searchLose(String id);
	public int updateLose(String id, int loses);
	public int searchRate(String id);
	public int updateRate(String id);
	public UserVO login(String id, String pw);
	public int userregister(UserVO user);
	public int checkUser(String id);
	
}
