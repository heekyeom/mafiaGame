package service;

import comm.UserDAO;
import comm.UserVO;

public class UserServiceImpl implements UserService{
	UserDAO userDAO;
	public UserServiceImpl() {
		userDAO = new UserDAO();
	}
	
	@Override
	public int searchWin(String id) {
		return userDAO.searchWin(id);
	}

	@Override
	public int updateWin(String id, int wins) {
		return userDAO.updateWin(id, wins);
	}

	@Override
	public int searchLose(String id) {
		return userDAO.searchLose(id);
	}

	@Override
	public int updateLose(String id, int loses) {
		return userDAO.updateLose(id, loses);
	}

	@Override
	public int searchRate(String id) {
		return userDAO.searchRate(id);
	}

	@Override
	public int updateRate(String id) {
		return userDAO.updateRate(id);
	}

	@Override
	public UserVO login(String id, String pw) {
		return userDAO.login(id, pw);
	}

	@Override
	public int userregister(UserVO user) {
		return userDAO.userregister(user);
	}

	@Override
	public int checkUser(String id) {
		return userDAO.checkUser(id);
	}
	
}
