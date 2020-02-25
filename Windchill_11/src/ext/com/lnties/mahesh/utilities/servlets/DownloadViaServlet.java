package ext.com.lnties.mahesh.utilities.servlets;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



//@WebServlet("/servlet/DownloadViaServlet")
public class DownloadViaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DownloadViaServlet() {
		
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		
		System.out.println("**********************inside custom servlet******************");
		System.out.println("**********************inside custom servlet******************");
		System.out.println("**********************inside custom servlet******************");
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();  
		out.print("<html><body>");  
		out.print("<b>hello simple servlet</b>");  
		out.print("</body></html>");
		
		/*String name=request.getParameter("name");//will return value  
		out.println("Welcome "+name);  */
		
		
		
		/*RequestDispatcher requestDispatcher = request.getRequestDispatcher("D:\\PTC\\Windchill_11.0\\Windchill\\codebase\\netmarkets\\jsp\\custom\\utilities\\MyStateValidator.jsp");
    	requestDispatcher.forward(request, response);*/
		
	}
	
	/*@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();  
		out.print("<html><body>");  
		out.print("<b>hello simple servlet</b>");  
		out.print("</body></html>");  
		
		String name=request.getParameter("name");//will return value  
		out.println("Welcome "+name);
		
		System.out.println("**********************inside custom servlet******************");
		System.out.println("**********************inside custom servlet******************");
		System.out.println("**********************inside custom servlet******************");
		response.setContentType("text/html");
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("D:\\PTC\\Windchill_11.0\\Windchill\\codebase\\netmarkets\\jsp\\custom\\utilities\\MyStateValidator.jsp");
    	requestDispatcher.forward(request, response);
	}*/

	
	
}
