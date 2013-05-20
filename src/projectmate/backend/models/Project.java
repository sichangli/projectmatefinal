package projectmate.backend.models;

import java.util.ArrayList;
import java.util.Date;

public class Project {
	private String title;
	private String descr;
	private long proid;
	private ArrayList<String> tasks;
	private ArrayList<String> users;
	private Date deadline;
	private String owner;
	private long status;
	private ArrayList<Task> Tasklist;
	private ArrayList<User> Userlist;
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public long getStatus() {
		return status;
	}
	public void setStatus(long status) {
		this.status = status;
	}
	public ArrayList<String> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<String> users) {
		this.users = users;
	}
	public ArrayList<String> getTasks() {
		return tasks;
	}
	public void setTasks(ArrayList<String> tasks) {
		this.tasks = tasks;
	}
	public long getProid() {
		return proid;
	}
	public void setProid(long proid) {
		this.proid = proid;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public ArrayList<Task> getTasklist() {
		return Tasklist;
	}
	public void setTasklist(ArrayList<Task> tasklist) {
		this.Tasklist = tasklist;
	}
	public ArrayList<User> getUserlist() {
		return Userlist;
	}
	public void setUserlist(ArrayList<User> userlist) {
		Userlist = userlist;
	} 
}
