package projectmate.backend.models;

import java.util.ArrayList;
import java.util.Date;

public class Task {
	private String title;
	private long status;
	private ArrayList<String> users;
	private long parentProj;
	private String owner;
	private String desc;
	private String taskId;
	private Date deadline;
	private ArrayList<User> userList;
	
	
	public ArrayList<User> getUserList() {
		return userList;
	}
	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}
	public String getTitle() {
		/*title for project*/
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public long getParentProj() {
		return parentProj;
	} 
	public void setParentProj(long parentProj) {
		this.parentProj = parentProj;
	}
	
	public ArrayList<String> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<String> users) {
		this.users = users;
	}
	
	public long getStatus() {
		return status;
	}
	public void setStatus(long status) {
		this.status = status;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	
}
