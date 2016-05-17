package ChatRoomInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBPk.DBManager;
import StaticClass.StaticVar;

/**
 * Servlet implementation class ChatRegServlet
 */
@WebServlet("/ChatRegServlet")
public class ChatRegServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChatRegServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		String MAC = request.getParameter("MAC");
		String ChatRoomID = request.getParameter("chatroomid");
		String Pwd = request.getParameter("password");
//		System.out.println(Pwd);
		String result="";
		
		String roomname;
		DBManager dbmg = new DBManager();
		try {

			String query_chatroom = "select *from ChatRooms where ChatID=" + ChatRoomID;
//			System.out.println(query_chatroom);
			dbmg.myQuery(query_chatroom);
			if (dbmg.myRsCount() < 1) {
				result = StaticVar.FAIL + StaticVar.Split_Str + "Invalid room id";
			} else {
				roomname=dbmg.getRs().getString(2);
				String truePwd = dbmg.getRs().getString(3);
//				System.out.println(truePwd);
				dbmg.closeRs();
				if (!Pwd.equals(truePwd)) {
					result = StaticVar.FAIL + StaticVar.Split_Str + "Wrong password";
				} else {
					String query_chat_info = "select *from ChatInfo where ChatID=" + ChatRoomID + " and MAC=" + MAC;
//					System.out.println(query_chat_info);
					dbmg.myQuery(query_chat_info);
					if (dbmg.myRsCount() > 0) {
						result = StaticVar.FAIL + StaticVar.Split_Str + "Already join the room";
					} else {
						dbmg.closeRs();
						String insertsql = "insert into ChatInfo values(" + "\'" + MAC + "\'," + "\'" + ChatRoomID
								+ "\'" + ")";
//						System.out.println(insertsql);
						int status=dbmg.myUpdate(insertsql);
						if(status<1){
							result=StaticVar.FAIL + StaticVar.Split_Str + "insert failed";
						}else{
							result=StaticVar.OK+StaticVar.Split_Str+roomname;
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(result);
		out.close();
		dbmg.closeConnection();
	}

}
