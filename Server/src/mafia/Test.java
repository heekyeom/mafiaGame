package mafia;

import java.sql.Connection;

import util.JDBCUtil;

public class Test {
	public static void main(String[] args) {
		
		Server server = new Server();
		
		server.start();
	}
} 

