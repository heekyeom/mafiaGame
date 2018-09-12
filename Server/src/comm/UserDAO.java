package comm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import util.JDBCUtil;


public class UserDAO {
	
	public int searchWin(String id)
	{
		int win =0;
		String sql1 = "select win from userinfo where id=?";
		
		Connection con1 = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
	
		try {
			con1 = JDBCUtil.getConnection();
			ps1 = con1.prepareStatement(sql1);
			// ?, value binding
			ps1.setString(1,id);
			// ps execution
			rs1 = ps1.executeQuery();
			// result value handling
			while(rs1.next()) {
				win = rs1.getInt("win");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con1, ps1, rs1);
		}
		
		return win;
	}
	public int updateWin(String id, int wins)
	{
		
		int win = searchWin(id);
		int result=0;
		String sql2 = "update userinfo set win=? where id=?";
		
		Connection con2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 =null;
		try {
			con2 = JDBCUtil.getConnection();
			ps2 = con2.prepareStatement(sql2);
			// ?, value binding
			ps2.setInt(1,win+wins);
			ps2.setString(2,id);
			// ps execution
			result = ps2.executeUpdate();
			// result value handling
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con2, ps2, rs2);
		}
		return result;

	}
	public int searchLose(String id)
	{
		int lose =0;
		String sql1 = "select lose from userinfo where id=?";
		
		Connection con1 = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
	
		try {
			con1 = JDBCUtil.getConnection();
			ps1 = con1.prepareStatement(sql1);
			// ?, value binding
			ps1.setString(1,id);
			// ps execution
			rs1 = ps1.executeQuery();
			// result value handling
			while(rs1.next()) {
				lose = rs1.getInt("lose");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con1, ps1, rs1);
		}
		return lose;
	}

	public int updateLose(String id, int loses)
	{
		int lose =searchLose(id);
		int result=0;
		String sql2 = "update userinfo set lose=? where id=?";
		
		Connection con2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 =null;
		try {
			con2 = JDBCUtil.getConnection();
			ps2 = con2.prepareStatement(sql2);
			// ?, value binding
			ps2.setInt(1,lose+loses);
			ps2.setString(2,id);
			// ps execution
			result = ps2.executeUpdate();
			// result value handling
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con2, ps2, rs2);
		}
		return result;
	}
	public int searchRate(String id)
	{
		int rate =0;
		String sql1 = "select rate from userinfo where id=?";
		
		Connection con1 = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
	
		try {
			con1 = JDBCUtil.getConnection();
			ps1 = con1.prepareStatement(sql1);
			// ?, value binding
			ps1.setString(1,id);
			// ps execution
			rs1 = ps1.executeQuery();
			// result value handling
			while(rs1.next()) {
				rate = rs1.getInt("rate");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con1, ps1, rs1);
		}
		
		return rate;
	}
	
	public int updateRate(String id)
	{
		int result=0;
		int win=searchWin(id);
		int lose=searchLose(id);
		
		String sql2 = "update userinfo set rate=? where id=?";
		
		Connection con2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 =null;
		try {
			con2 = JDBCUtil.getConnection();
			ps2 = con2.prepareStatement(sql2);
			// ?, value binding
			//rate+ Math.floor(win/(win+lose))
			ps2.setInt(1,(int)(((float)win/((float)win+(float)lose)*100)+0.5));
			ps2.setString(2,id);
			// ps execution
			result = ps2.executeUpdate();
			// result value handling
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con2, ps2, rs2);
		}
		return result;
	}
	
	/*
	public int insertRole(String id, String role)
	{
		int result=0;
		String sql = "update userinfo set role=? where id=?";
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs =null;
		try {
			con = JDBCUtil.getConnection();
			ps = con.prepareStatement(sql);
			// ?, value binding
			ps.setString(1,role);
			ps.setString(2,id);
			// ps execution
			result = ps.executeUpdate();
			// result value handling
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con, ps, rs);
		}
		
		return result;
	}*/
	
	public UserVO login(String id, String pw)
	{
		UserVO user = null;
		String sql = "select * from userinfo where id=? and pw=?";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
	
		try {
			con = JDBCUtil.getConnection();
			ps = con.prepareStatement(sql);
			// ?, value binding
			ps.setString(1,id);
			ps.setString(2,pw);
			
			// ps execution
			rs = ps.executeQuery();
			// result value handling
			while(rs.next()) {
				user = new UserVO(rs.getString("id"), rs.getString("pw"));
			}
			System.out.println("[UserDAO] 로그인 결과 : "+user);   //Debug
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con, ps, rs);
		}
		
		return user;
	}
	
	public int userregister(UserVO user)      //회원가입 완료
	{
		String sql = "insert into userinfo(id,pw) values(?,?)";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result =0;
		try {
			con = JDBCUtil.getConnection();
			ps = con.prepareStatement(sql);
			// ?, value binding
			ps.setString(1,user.getId());
			ps.setString(2,user.getPw());
			// ps execution
			if(checkUser(user.getId())!=0) return 0;
			result = ps.executeUpdate();
			System.out.println("[UserDAO] 회원가입 결과 : "+result);   //Debug
			// result value handling
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con, ps, rs);
		}
		return result;
	}

	public int checkUser(String id) {
		// TODO Auto-generated method stub
		
		String sql1 = "select count(id) from userinfo where id=?";
		
		Connection con1 = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		
		try {
			con1 = JDBCUtil.getConnection();
			ps1 = con1.prepareStatement(sql1);
			// ?, value binding
			ps1.setString(1,id);
			// ps execution
			rs1 = ps1.executeQuery();
			
			// result value handling
			
			while(rs1.next()) {
				if(rs1.getInt("count(id)") != 0)
				{
					System.out.println("[UserDAO] 아이디 확인 결과 : 있습니다. "+1);   //Debug
					return 1;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(con1, ps1, rs1);
		}
		System.out.println("[UserDAO] 회원가입 결과 : 없습니다. "+0);   //Debug
		return 0;
	}

	
}
