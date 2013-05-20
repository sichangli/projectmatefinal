package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.Task;
import projectmate.backend.models.User;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ListProjectTasks extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3987848845869773833L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		long projid = Integer.valueOf(req.getParameter("projid"));
		Datastore ds = new Datastore();
		JSONObject result = new JSONObject();
		JSONArray arr = new JSONArray();
		
		ArrayList<Task> tasks = ds.getAllTasks(projid);
		for (Task t : tasks) {
			JSONObject r = new JSONObject();
			try {
				r.put("tid", t.getTaskId());
				r.put("owner", t.getOwner());
				r.put("descr", t.getDesc());
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
				r.put("deadline", formatter.format(t.getDeadline()));
				r.put("status", t.getStatus());
				r.put("parentProj", t.getParentProj());
				r.put("title", t.getTitle());
				
//				ArrayList<User> userlist = t.getUserList();
//				JSONArray members = new JSONArray();
//				for (User user : userlist) {
//					JSONObject u = new JSONObject();
//					u.put("userid", user.getUserId());
//					u.put("lastName", user.getLastName());
//					u.put("firstName", user.getFirstName());
//					u.put("sex", user.getSex());
//					members.put(u);
//				}
//				r.put("members", members);
				
				arr.put(r);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			result.put("tasks", arr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(result);
		out.flush();
		out.close();
	}
}
