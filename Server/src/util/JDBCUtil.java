package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtil {
	public static Connection getConnection() {
		//properties 揶쏆빘猿쒏에占� 占쎌뇚�겫占쏙옙�솁占쎌뵬 占쎌뿯占쎌젾 獄쏆룇�벉
		Connection con = null;
		String driver ="oracle.jdbc.driver.OracleDriver";
		String url ="jdbc:oracle:thin:@127.0.0.1:1521:xe";
		String user = "system";
		String pw = "1234";
		
		try {
			//Properties p = new Properties();
			//p.load(new FileInputStream("C:\\lib\\db_info.txt"));
			
			//String driver = p.getProperty("driver");
//			String url = p.getProperty("url");
//			String user = p.getProperty("user");
//			String pw = p.getProperty("pw");
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static void close(Connection con, Statement st, ResultSet rs) {
		try {
			if(rs != null)rs.close();
			if(st != null)st.close();
			if(con != null)con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
