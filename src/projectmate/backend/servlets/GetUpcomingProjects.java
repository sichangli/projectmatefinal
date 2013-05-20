package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.Project;
import projectmate.backend.models.User;

public class GetUpcomingProjects extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1332280499181412419L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		String userId = req.getParameter("userId");
		Datastore ds = new Datastore();
		JSONObject result = new JSONObject();
		
		JSONArray arr = new JSONArray();
		ArrayList<Project> upcomings = ds.getUpcomingProjects(userId);
		for (Project p : upcomings) {
			JSONObject r = new JSONObject();
			try {
				r.put("proid", p.getProid());
				r.put("title", p.getTitle());
				r.put("status", p.getStatus());
				r.put("deadline", p.getDeadline());
				r.put("descr", p.getDescr());
				r.put("owner", p.getOwner());
				
				JSONArray members = new JSONArray();
				ArrayList<User> userlist = p.getUserlist();
				for (User user : userlist) {
					JSONObject u = new JSONObject();
					u.put("userid", user.getUserId());
					u.put("lastName", user.getLastName());
					u.put("firstName", user.getFirstName());
					u.put("sex", user.getSex());
					members.put(u);
				}
				r.put("members", members);
				
				arr.put(r);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			result.put("upcomings", arr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(result);
		out.flush();
		out.close();
	}
}
