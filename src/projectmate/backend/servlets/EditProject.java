package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONObject;





public class EditProject extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException
	{
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject json = new JSONObject();
		
		String bitStr = req.getParameter("bitmap");
		
		
	}

}
