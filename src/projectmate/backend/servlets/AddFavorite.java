package projectmate.backend.servlets;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import projectmate.backend.datastore.Datastore;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


public class AddFavorite extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2502489585710404636L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		String userId = req.getParameter("userId");
		long projid = Integer.valueOf(req.getParameter("projid"));
		Datastore ds = new Datastore();
		JSONObject result = new JSONObject();
		
		ds.addFavoriteProject(userId, projid);
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
