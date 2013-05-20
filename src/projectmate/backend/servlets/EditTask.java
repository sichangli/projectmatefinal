package projectmate.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import projectmate.backend.datastore.Datastore;
import projectmate.backend.models.Task;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class EditTask extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9114241565840121486L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject result = new JSONObject();
		Datastore ds = new Datastore();
		
		String taskid = req.getParameter("taskid");
		String title = req.getParameter("title");
//		String owner = req.getParameter("owner");
		String desc = req.getParameter("descr");
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
		Date deadline = null;
		try {
			deadline = formatter.parse(req.getParameter("deadline"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int status = Integer.valueOf(req.getParameter("status"));
		
		Task task = new Task();
		task.setTaskId(taskid);
		task.setDeadline(deadline);
		task.setDesc(desc);
		task.setStatus(status);
		task.setTitle(title);
//		task.setOwner(owner);
		
		ds.editTask(task);
		
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
