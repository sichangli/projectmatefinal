package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.Task;

public class CreateTask extends HttpServlet {

	private static final long serialVersionUID = 5535174392868668465L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		Datastore ds = new Datastore();
		JSONObject result = new JSONObject();
		
		String title = req.getParameter("title");
		String owner = req.getParameter("owner");
		String desc = req.getParameter("descr");
		DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Date deadline = null;
		try {
			deadline = formatter.parse(req.getParameter("deadline"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(deadline == null){
			try {
				result.put("result", "no");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.print(result);
			out.flush();
			out.close();
			return;
		}
		int status = Integer.valueOf(req.getParameter("status"));
		int parentProj = Integer.valueOf(req.getParameter("parentProj"));
		
		ArrayList<String> users = new ArrayList<String> ();
		users.add(owner);
		
		Task task = new Task();
		task.setDeadline(deadline);
		task.setDesc(desc);
		task.setStatus(status);
		task.setOwner(owner);
		task.setTitle(title);
		task.setParentProj(parentProj);
		task.setUsers(users);
		
		ds.createTask(task);
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
