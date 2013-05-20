package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.User;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = -8426799082605598691L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");
		Datastore ds = new Datastore();
		
		JSONObject result = new JSONObject();
		User user = ds.logIn(userId, userPwd);
		if (user != null) {
			try {
				result.put("result", "yes");
				result.put("lastName", user.getLastName());
				result.put("firstName", user.getFirstName());
				result.put("sex", user.getSex());
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
		out.println(result);
		out.flush();
		out.close();
	}
}
