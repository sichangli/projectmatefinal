package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import projectmate.backend.datastore.Datastore;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class CheckUser extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2927300990712370508L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		String userId = req.getParameter("userId");
		Datastore ds = new Datastore();
		JSONObject result = new JSONObject();
		
		Entity user = ds.findUserEntity(userId);
		if (user != null) {
			try {
				result.put("result", "yes");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				result.put("result", "no");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		out.print(result);
		out.flush();
		out.close();
	}
}
