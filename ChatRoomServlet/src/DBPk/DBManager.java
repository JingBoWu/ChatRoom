package DBPk;
import java.sql.*;

public class DBManager {
	static final String DBURL="jdbc:mysql://localhost:3306/ChatRoom";
	static final String DBUSER="root";
	static final String DBPWD="123123";
	Connection con=null;
	Statement stmt=null;
	ResultSet rs=null;
	public DBManager(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			con=java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
			stmt=con.createStatement();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
//	public void getConnection(){
//		try{
//			Class.forName("com.mysql.jdbc.Driver");
//			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
//			con=java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
//			stmt=con.createStatement();
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
	public ResultSet getRs(){
		return rs;
	}
	public void closeRs(){
		try{
			if(rs!=null){
				rs.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void closeSt(){
		try{
			if(stmt!=null){
				stmt.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void closeConnection(){
		try{
			if(rs!=null){
				rs.close();
			}
			if(stmt!=null){
				stmt.close();
			}
			if(con!=null){
				con.close();
			}	
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void myQuery(String str){
		try{
			rs=stmt.executeQuery(str);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public int myUpdate(String str){
		int nRecord=0;
		try{
			nRecord=stmt.executeUpdate(str);
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		return nRecord;
	}
	public int myRsCount(){
		if(rs==null){
			return -1;
		}
		int count=0;
		try{
			if(rs.next()){
				rs.last();
				count=rs.getRow();
				rs.first();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
}
