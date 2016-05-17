package UserPK;

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
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
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

		String result = "";
		
		DBManager dbmg = new DBManager();
		try {
			String query = "select *from Users where MAC=" + MAC;
			dbmg.myQuery(query);
			ResultSet rs = dbmg.getRs();
			if (rs.next()) {
				String username = rs.getString(1);
				String mac = rs.getString(2);
				int head = rs.getInt(3);
				int bubble = rs.getInt(4);
				String userInfo = username + StaticVar.Split_Str + mac + StaticVar.Split_Str + head
						+ StaticVar.Split_Str + bubble;
				result=userInfo;
			} else {
				result=StaticVar.FAIL+StaticVar.Split_Str+"Have not registered";
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
