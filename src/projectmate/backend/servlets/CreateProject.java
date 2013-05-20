package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.Project;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class CreateProject extends HttpServlet {

	private static final long serialVersionUID = -2309515937540315917L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException
	{
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject json = new JSONObject();
		
		String owner = req.getParameter("owner");
		String desc = req.getParameter("descr");
		String title = req.getParameter("title");
		@SuppressWarnings("deprecation")
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
		Date deadline = null;
		try {
			deadline = formatter.parse(req.getParameter("deadline"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(deadline == null){
			try {
				json.put("result", "no");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.print(json);
			out.flush();
			out.close();
			return;
		}
		
		int status = Integer.valueOf(req.getParameter("status"));
		String members = req.getParameter("members");
		
		String[] ms = members.split(",");
		ArrayList<String> users = new ArrayList<String>(Arrays.asList(ms));
		
		Project proj = new Project();
		proj.setDeadline(deadline);
		proj.setDescr(desc);
		proj.setStatus(status);
		proj.setTitle(title);
		proj.setUsers(users);
		proj.setOwner(owner);
		
		Datastore datastore = new Datastore();
		datastore.addProjectCaller(proj);
		
		try {
			json.put("result", "yes");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(json);
		out.flush();
		out.close();
	}
}
