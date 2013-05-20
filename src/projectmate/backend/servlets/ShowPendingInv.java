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
import projectmate.backend.models.Project;
import projectmate.backend.models.User;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ShowPendingInv extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 312487455862396939L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		String userId = req.getParameter("userId");
		Datastore ds = new Datastore();
		JSONObject result = new JSONObject();
		
		JSONArray arr = new JSONArray();
		ArrayList<Project> pendings = ds.showPendingInvitation(userId);
		for (Project p : pendings) {
			JSONObject r = new JSONObject();
			try {
				r.put("proid", p.getProid());
				r.put("title", p.getTitle());
				r.put("status", p.getStatus());
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
				r.put("deadline", formatter.format(p.getDeadline()));
				r.put("descr", p.getDescr());
				r.put("owner", p.getOwner());
				
				User ut = ds.getUserFromId(p.getOwner());
				r.put("lastName", ut.getLastName());
				r.put("firstName", ut.getFirstName());
				r.put("sex", ut.getSex());
				
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
			result.put("pendings", arr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(result);
		out.flush();
		out.close();
	}
}
