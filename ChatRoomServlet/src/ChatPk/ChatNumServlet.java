package ChatPk;

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
 * Servlet implementation class ChatNumServlet
 */
@WebServlet("/ChatNumServlet")
public class ChatNumServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChatNumServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
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

		String ChatRoomID = request.getParameter("chatroomid");
		String msgRoom="MSG_"+ChatRoomID;
		
		String result="";

		DBManager dbmg = new DBManager();
		try {
			String query ="select *from "+msgRoom;
			dbmg.myQuery(query);
			int count=dbmg.myRsCount();
			result=count+"";
		} catch (Exception e) {
			e.printStackTrace();
			result=StaticVar.FAIL;
		}
		out.print(result);
		out.close();
		dbmg.closeConnection();
	}

}
