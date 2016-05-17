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
 * Servlet implementation class ChatServlet
 */
@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChatServlet() {
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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		String ChatRoomID=request.getParameter("chatroomid");
		String MsgRoom="MSG_"+ChatRoomID;
		String MSG=request.getParameter("MSG");
//		String MSG = new String((request.getParameter("MSG")).getBytes(),"UTF-8");
		String[] msgArr=MSG.split(StaticVar.Split_Str);
		int msgtype=Integer.parseInt(msgArr[0]);
		
		String result="";

		DBManager dbmg = new DBManager();
		try {
			String insertsql;
			if(msgtype==0||msgtype==2){
				insertsql="insert into "+MsgRoom+"(MsgType,Msg) values("+msgArr[0]+",\'"+msgArr[1]+"\')";
				
			}else{
				insertsql="insert into "+MsgRoom+"(MsgType,Msg,UserName,Head,Bubble) values("
																				+msgArr[0]
																				+",\'"
																				+msgArr[1]
																				+"\',\'"
																				+msgArr[2]
																				+"\',"
																				+msgArr[3]
																				+","
																				+msgArr[4]
																				+")";
			}
			System.out.println(insertsql);
			int rs=dbmg.myUpdate(insertsql);
			if(rs<1){
				result=StaticVar.FAIL;
			}else{
				result=StaticVar.OK;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(result);
		out.close();
		dbmg.closeConnection();
	}

}
