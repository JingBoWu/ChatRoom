package UserPK;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
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
 * Servlet implementation class RegServlet
 */
@WebServlet("/RegServlet")
public class RegServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		PrintWriter  out =response.getWriter();
		
		
		String MAC=request.getParameter("MAC");
		String UserName=request.getParameter("username");

		int Head=Integer.parseInt(request.getParameter("head"));
		int Bubble=Integer.parseInt(request.getParameter("bubble"));
		
		String result="";
//		System.out.println(query);
//		System.out.println(update);
		DBManager dbmg=new DBManager();
		try {
			String update="insert into Users values("+"\'"+UserName+"\',"+"\'"+MAC+"\',"+Head+","+Bubble+")";
			String query="select *from Users where MAC="+MAC;
			dbmg.myQuery(query);
			if(dbmg.getRs().next()){
				dbmg.closeRs();
				String delete="delete from Users where MAC="+MAC;
//				System.out.println(delete);
				dbmg.myUpdate(delete);
			}
			int rs=dbmg.myUpdate(update);
			if(rs==1){
				result=StaticVar.OK;
			}
			else{
				result=StaticVar.FAIL;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(result);
		out.close();
		dbmg.closeConnection();
	}

}
