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

public class GetProjectInfo extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7926100005393454115L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		String userId = req.getParameter("userId");
		Datastore ds = new Datastore();
		JSONObject result = new JSONObject();
		
		try {
			result.put("upcoming", ds.getUpcomingProjects(userId).size());
			result.put("ongoing", ds.getOngoingProjects(userId).size());
			result.put("completed", ds.getCompletedProjects(userId).size());
			result.put("favorite", ds.getFavoriteProjects(userId).size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//get 4 recent projects
		ArrayList<Project> recents = ds.getRecentProjects(userId);
		JSONArray arr = new JSONArray();
		for (Project p : recents) {
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
			result.put("recents", arr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(result);
		out.flush();
		out.close();
	}
}
