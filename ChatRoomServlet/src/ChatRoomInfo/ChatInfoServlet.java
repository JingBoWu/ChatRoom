package ChatRoomInfo;

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
 * Servlet implementation class ChatInfoServlet
 */
@WebServlet("/ChatInfoServlet")
public class ChatInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChatInfoServlet() {
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

		String MAC = request.getParameter("MAC");
		
		String result="";

		DBManager dbmg = new DBManager();
		try {
			String query = "select *from ChatInfoWhole where MAC=" + MAC;
			dbmg.myQuery(query);
			ResultSet rs = dbmg.getRs();
			while(rs.next()){
				//String mac = rs.getString(1);
				String chatroomid = rs.getString(2);
				String chatroomname=rs.getString(3);
				String chatinfo=chatroomid+StaticVar.Split_Str+chatroomname;
				result+=chatinfo+StaticVar.Split_Str;
			}
		   
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(result);
		out.close();
		dbmg.closeConnection();
	}
	
}
