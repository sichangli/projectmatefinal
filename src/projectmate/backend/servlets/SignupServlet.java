package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.User;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class SignupServlet extends HttpServlet {

	private static final long serialVersionUID = -4551518993306347962L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject result = new JSONObject();

		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");
		String lastName = req.getParameter("lastName");
		String firstName = req.getParameter("firstName");
		String sex = req.getParameter("sex");
		
		User user = new User();
		user.setUserId(userId);
		user.setPassword(userPwd);
		user.setLastName(lastName);
		user.setFirstName(firstName);
		user.setSex(sex);
		
		Datastore ds = new Datastore();
		if (ds.signup(user)) {
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
