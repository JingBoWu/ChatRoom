package ChatPk;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBPk.DBManager;
import StaticClass.StaticVar;

/**
 * Servlet implementation class ChatGetServlet
 */
@WebServlet("/ChatGetServlet")
public class ChatGetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChatGetServlet() {
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
		int newestID=Integer.parseInt(request.getParameter("newestid"));
		int nextID= newestID+1;
		
		String result="";

		DBManager dbmg = new DBManager();
		try {
			String query ="select *from "+msgRoom+" where ID="+nextID;
			dbmg.myQuery(query);
			if(dbmg.getRs().next()){
				int type=dbmg.getRs().getInt(2);
				String msg=dbmg.getRs().getString(3);
				result+=type+StaticVar.Split_Str+msg+StaticVar.Split_Str;
				if(type==1){
					String username=dbmg.getRs().getString(4);
					int head=dbmg.getRs().getInt(5);
					int bubble=dbmg.getRs().getInt(6);
					result+=username+StaticVar.Split_Str+head+StaticVar.Split_Str+bubble;
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
