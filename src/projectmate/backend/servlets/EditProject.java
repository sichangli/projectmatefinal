package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.Project;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class EditProject extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6750851244383992686L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException
	{
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject result = new JSONObject();
		Datastore ds = new Datastore();
		
		int projid = Integer.valueOf(req.getParameter("projid"));
		String title = req.getParameter("title");
		//String owner = req.getParameter("owner");
		String desc = req.getParameter("descr");
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
		Date deadline = null;
		try {
			deadline = formatter.parse(req.getParameter("deadline"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int status = Integer.valueOf(req.getParameter("status"));
		
		Project proj = new Project();
		proj.setProid(projid);
		proj.setDeadline(deadline);
		proj.setDescr(desc);
		proj.setStatus(status);
		proj.setTitle(title);
		//proj.setOwner(owner);
		
		ds.editProject(proj);
		
		try {
			result.put("result", "yes");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(result);
		out.flush();
		out.close();
	}
}
