package ChatRoomInfo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBPk.DBManager;
import StaticClass.StaticVar;

/**
 * Servlet implementation class ChatQuitServlet
 */
@WebServlet("/ChatQuitServlet")
public class ChatQuitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChatQuitServlet() {
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
		
		String result = "";

		DBManager dbmg = new DBManager();
		try {
			String query="select *from ChatInfo where ChatID="+ChatRoomID+" and MAC="+MAC;
			dbmg.myQuery(query);
			if(dbmg.myRsCount()<1){
				result=StaticVar.FAIL+StaticVar.Split_Str+"Not in the room";
			}else{
				String delete="delete from ChatInfo where ChatID="+ChatRoomID+" and MAC="+MAC;
				int rs=dbmg.myUpdate(delete);
				if(rs<1){
					result=StaticVar.FAIL+StaticVar.Split_Str+"Delete failed";
				}else{
					result=StaticVar.OK;
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
