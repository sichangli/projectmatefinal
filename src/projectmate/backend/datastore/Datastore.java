package projectmate.backend.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mortbay.log.Log;

import projectmate.backend.models.Project;
import projectmate.backend.models.Task;
import projectmate.backend.models.User;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class Datastore {

	private DatastoreService datastore;

	public Datastore() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public User logIn(String userId, String password) {
		Entity user = findUserEntity(userId);
		if (user != null) {
			String tempPassword = (String) user.getProperty("password");
			if (tempPassword.equals(password))
				return getUserFromEntity(user);
			else
				return null;
		} else
			return null;
	}

	private User getUserFromEntity(Entity userEntity) {
		User user = new User();
		user.setUserId((String) userEntity.getProperty("userId"));
		user.setPassword((String) userEntity.getProperty("password"));
		user.setLastName((String) userEntity.getProperty("lastName"));
		user.setFirstName((String) userEntity.getProperty("firstName"));
		user.setSex((String) userEntity.getProperty("sex"));
		return user;
	}

	public boolean signup(User user) {
		Entity userEntity = findUserEntity(user.getUserId());
		if (userEntity != null)
			return false;
		else {
			addUser(user);
			return true;
		}
	}

	private void addUser(User user) {
		Key key = KeyFactory.createKey("user", "default");
		Entity userE = new Entity("user", key);
		userE.setProperty("userId", user.getUserId());
		userE.setProperty("password", user.getPassword());
		userE.setProperty("lastName", user.getLastName());
		userE.setProperty("firstName", user.getFirstName());
		userE.setProperty("sex", user.getSex());
		datastore.put(userE);
	}

	public Entity findUserEntity(String userId) {
		Entity user = null;
		Key key = KeyFactory.createKey("user", "default");
		Query query = new Query("user", key);
		List<Entity> users = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		for (Entity u : users) {
			String tempUserId = (String) u.getProperty("userId");
			if (userId.equals(tempUserId))
				user = u;
		}
		return user;
	}

	public long addProjectCaller(Project proj) {
		long pid = addProject(proj);
		return pid;
	}

	private Long addPid() {
		Key key = KeyFactory.createKey("proj", "default");
		Query q = new Query("proj", key);
		List<Entity> list = datastore.prepare(q).asList(
				FetchOptions.Builder.withLimit(1000));
		if (list == null)
			return new Long(1);
		int res = list.size();
		res++;
		return new Long(res);
	}

	private long addProject(Project proj) {

		Key keyforProj = KeyFactory.createKey("proj", "default");

		ArrayList<String> users = proj.getUsers();

		/* Should be no need to build task at this moment */
		// List tasks = proj.getTasks();
		String owner = proj.getOwner();
		Date deadline = proj.getDeadline();
		String title = proj.getTitle();
		String desc = proj.getDescr();
		long status = proj.getStatus();

		/* make proj entity */
		Entity project = new Entity("proj", keyforProj);
		project.setProperty("title", title);
		project.setProperty("owner", owner);
		project.setProperty("desc", desc);
		project.setProperty("status", status);
		project.setProperty("deadline", deadline);
		long pid = addPid();
		project.setProperty("pid", pid);
		datastore.put(project);

		// add owner to pair
		addProjectPair(pid, owner);
		// no need to invite owner
		users.remove(owner);
		// add to invitation list
		addInvitation(pid, users);

		return pid;
	}

	public void addInvitation(long projid, ArrayList<String> users) {
		String status = "pending";
		Key invitationKey = KeyFactory.createKey("invitation", "default");
		for (String user : users) {
			Entity invitation = new Entity("invitation", invitationKey);
			invitation.setProperty("projid", projid);
			invitation.setProperty("userid", user);
			invitation.setProperty("status", status);

			datastore.put(invitation);
		}
	}

	public ArrayList<Project> showPendingInvitation(String userId) {
		Key invitationKey = KeyFactory.createKey("invitation", "default");
		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userId);
		Filter statusFilter = new FilterPredicate("status", FilterOperator.EQUAL,
				"pending");
		CompositeFilter comFilter = CompositeFilterOperator.and(userFilter,
				statusFilter);
		Query query = new Query("invitation", invitationKey)
				.setFilter(comFilter);
		List<Entity> invitations = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		ArrayList<Long> pids = new ArrayList<Long>();
		for (Entity inv : invitations) {
			pids.add((Long) inv.getProperty("projid"));
		}
		
		ArrayList<Project> projs = getProjects(pids);
		return projs;
	}

	public void confirmInvitation(long projid, String userId) {
		// update the invitation
		String status = "confirmed";
		Key invitationKey = KeyFactory.createKey("invitation", "default");
		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userId);
		Filter projFilter = new FilterPredicate("projid", FilterOperator.EQUAL,
				projid);
		CompositeFilter comFilter = CompositeFilterOperator.and(userFilter,
				projFilter);
		Query query = new Query("invitation", invitationKey)
				.setFilter(comFilter);
		Entity invitation = datastore.prepare(query).asSingleEntity();
		invitation.setProperty("status", status);
		datastore.put(invitation);

		addProjectPair(projid, userId);

	}

	private void addProjectPair(long projid, String userid) {
		// add to (user, project) pair
		Key keyforPair = KeyFactory.createKey("pair", "default");
		Entity userpropair = new Entity("pair", keyforPair);
		userpropair.setProperty("userid", userid);
		userpropair.setProperty("projid", projid);
		userpropair.setProperty("fav", 0);
		Date currdate = new Date();
		// long visitTime = currdate.getTime();
		userpropair.setProperty("visittime", currdate);
		datastore.put(userpropair);
	}

	public void declineInvitation(long projid, String userId) {
		// update the invitation
		String status = "declined";
		Key invitationKey = KeyFactory.createKey("invitation", "default");
		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userId);
		Filter projFilter = new FilterPredicate("projid", FilterOperator.EQUAL,
				projid);
		CompositeFilter comFilter = CompositeFilterOperator.and(userFilter,
				projFilter);
		Query query = new Query("invitation", invitationKey)
				.setFilter(comFilter);
		Entity invitation = datastore.prepare(query).asSingleEntity();
		invitation.setProperty("status", status);
		datastore.put(invitation);
	}

	public String createTaskId(long projectId, Key keyforTask) {
		Filter taskFilter = new FilterPredicate("parentProj",
				FilterOperator.EQUAL, projectId);
		Query query = new Query("task", keyforTask).setFilter(taskFilter);
		List<Entity> list = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(1000));
		int tid = list.size() + 1;
		return projectId + "-" + tid;
	}

	public void createTask(Task task) {
		String projid = Long.toString(task.getParentProj());

		Key keyforPair = KeyFactory.createKey("taskpair", "default");
		Key keyforTask = KeyFactory.createKey("task", "default");

		long parentProj = task.getParentProj();
		String tid = createTaskId(parentProj, keyforTask);
		String owner = task.getOwner();
		String desc = task.getDesc();
		String title = task.getTitle();
		Date deadline = task.getDeadline();
		List<String> users = task.getUsers();
		long status = task.getStatus();

		/* Create a <user, task> pair first */
		for (Object user : users) {
			Entity userpair = new Entity("taskpair", keyforPair);
			user = (String) user;
			userpair.setProperty("userid", user);
			userpair.setProperty("taskid", tid);
			userpair.setProperty("deadline", deadline);
			datastore.put(userpair);
		}

		Entity taskentity = new Entity("task", keyforTask);
		taskentity.setProperty("tid", tid);
		taskentity.setProperty("owner", owner);
		taskentity.setProperty("desc", desc);
		taskentity.setProperty("deadline", deadline);
		taskentity.setProperty("status", status);
		taskentity.setProperty("parentProj", parentProj);
		taskentity.setProperty("title", title);

		datastore.put(taskentity);
	}

	// get all tasks for one person
	public ArrayList<Task> findTasks(String userId) {
		ArrayList<String> tids = findAllTaskPairs(userId);
		ArrayList<Task> tasks = getTasks(tids);
		return tasks;
	}

	private ArrayList<String> findAllTaskPairs(String userId) {
		Key keyforPair = KeyFactory.createKey("taskpair", "default");
		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userId);
		Query query = new Query("taskpair", keyforPair).setFilter(userFilter)
				.addSort("deadline", Query.SortDirection.DESCENDING);
		List<Entity> pairs = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		ArrayList<String> result = new ArrayList<String>();
		for (Entity pair : pairs) {
			String tmp = (String) pair.getProperty("taskid");
			result.add(tmp);
		}
		return result;
	}

	private ArrayList<Task> getTasks(ArrayList<String> tids) {
		Key keyforTask = KeyFactory.createKey("task", "default");
		ArrayList<Task> tasks = new ArrayList<Task>();

		for (String tid : tids) {
			Filter taskFilter = new FilterPredicate("tid",
					FilterOperator.EQUAL, tid);
			Query query = new Query("task", keyforTask).setFilter(taskFilter);
			List<Entity> list = datastore.prepare(query).asList(
					FetchOptions.Builder.withLimit(100));
			if (list == null)
				continue;
			Entity tmp = (Entity) list.get(0);
			Task task = new Task();
			task.setTaskId((String) tmp.getProperty("tid"));
			task.setOwner((String) tmp.getProperty("owner"));
			task.setDesc((String) tmp.getProperty("desc"));
			task.setDeadline((Date) tmp.getProperty("deadline"));
			task.setStatus((Long) tmp.getProperty("status"));
			task.setParentProj((Long) tmp.getProperty("parentProj"));
			task.setTitle((String) tmp.getProperty("title"));
			task.setUserList(getAllUsersTask(tid));
			tasks.add(task);
		}
		return tasks;
	}

	/* Get all projects for one person */
	public ArrayList<Project> findAllProjects(String userid) {
		ArrayList<Long> pids = findAllPairs(userid);
		ArrayList<Project> projs = getProjects(pids);
		return projs;
	}

	private ArrayList<Long> findAllPairs(String userid) {
		Key key = KeyFactory.createKey("pair", "default");

		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userid);
		Query query = new Query("pair", key).setFilter(userFilter);
		List<Entity> pairs = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		ArrayList<Long> result = new ArrayList<Long>();
		if (pairs == null)
			return result;
		for (Entity pair : pairs) {
			Long tmp = (Long) pair.getProperty("projid");
			result.add(tmp);
		}

		return result;
	}

	private ArrayList<Project> getProjects(ArrayList<Long> pids) {
		Key key = KeyFactory.createKey("proj", "default");
		ArrayList<Project> projects = new ArrayList<Project>();
		for (Long pid : pids) {
			Filter projFilter = new FilterPredicate("pid",
					FilterOperator.EQUAL, pid);
			Query query = new Query("proj", key).setFilter(projFilter);
			List<Entity> list = datastore.prepare(query).asList(
					FetchOptions.Builder.withLimit(100));
			if (list == null)
				continue;
			Entity tmp = (Entity) list.get(0);
			Project tmpproj = new Project();
			tmpproj.setDeadline((Date) tmp.getProperty("deadline"));
			tmpproj.setDescr((String) tmp.getProperty("desc"));
			tmpproj.setOwner((String) tmp.getProperty("owner"));
			tmpproj.setProid((Long) tmp.getProperty("pid"));
			tmpproj.setStatus((Long) tmp.getProperty("status"));
			tmpproj.setTitle((String) tmp.getProperty("title"));
			// Log.debug("Now the pid is:" + pid);
			ArrayList<User> users = getAllUsersProj(pid);
			if (users == null) {
				tmpproj.setUserlist(new ArrayList<User>());
			} else {
				tmpproj.setUserlist(users);
			}

			projects.add(tmpproj);
		}

		return projects;
	}

	public ArrayList<Task> getAllTasks(long projid) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Key keyforTask = KeyFactory.createKey("task", "default");
		Filter taskFilter = new FilterPredicate("parentProj",
				FilterOperator.EQUAL, projid);
		Query query = new Query("task", keyforTask).setFilter(taskFilter);
		List<Entity> list = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(1000));
		if (list == null) {
			return tasks;
		}
		for (Entity t : list) {
			Task task = new Task();
			task.setParentProj(projid);
			task.setOwner((String) t.getProperty("owner"));
			task.setDesc((String) t.getProperty("desc"));
			task.setStatus((Long) t.getProperty("status"));
			task.setTaskId((String) t.getProperty("tid"));
			task.setTitle((String) t.getProperty("title"));
			task.setDeadline((Date) t.getProperty("deadline"));
			tasks.add(task);
		}
		return tasks;

	}

	private ArrayList<User> getAllUsersTask(String taskid) {
		Key key = KeyFactory.createKey("taskpair", "default");
		Key keyforUser = KeyFactory.createKey("user", "default");
		Filter taskFilter = new FilterPredicate("taskid", FilterOperator.EQUAL,
				taskid);
		Query query = new Query("taskpair", key).setFilter(taskFilter);
		List<Entity> pairs = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		ArrayList<User> result = new ArrayList<User>();
		if (pairs == null) {
			return result;
		}
		for (Entity pair : pairs) {
			String userid = (String) pair.getProperty("userid");
			// System.out.println(userid);
			Filter userFilter = new FilterPredicate("userId",
					FilterOperator.EQUAL, userid);
			Query q = new Query("user", keyforUser).setFilter(userFilter);
			List<Entity> list = datastore.prepare(q).asList(
					FetchOptions.Builder.withLimit(100));
			if (list == null || list.size() > 1) {
				continue;
			}
			Entity userentity = list.get(0);
			User user = new User();
			user.setFirstName((String) userentity.getProperty("firstName"));
			user.setLastName((String) userentity.getProperty("lastName"));
			user.setSex((String) userentity.getProperty("sex"));
			user.setUserId((String) userentity.getProperty("userId"));
			result.add(user);
		}
		return result;
	}

	private ArrayList<User> getAllUsersProj(long projid) {
		Key key = KeyFactory.createKey("pair", "default");
		Key keyforUser = KeyFactory.createKey("user", "default");
		Filter projFilter = new FilterPredicate("projid", FilterOperator.EQUAL,
				projid);
		Query query = new Query("pair", key).setFilter(projFilter);
		List<Entity> pairs = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		ArrayList<User> result = new ArrayList<User>();
		if (pairs == null) {
			return result;
		}
		for (Entity pair : pairs) {
			String userid = (String) pair.getProperty("userid");

			Filter userFilter = new FilterPredicate("userId",
					FilterOperator.EQUAL, userid);
			Query q = new Query("user", keyforUser).setFilter(userFilter);
			List<Entity> list = datastore.prepare(q).asList(
					FetchOptions.Builder.withLimit(100));
			if (list == null || list.size() > 1 || list.size() == 0) {
				continue;
			}
			Entity userentity = list.get(0);
			User user = new User();
			user.setFirstName((String) userentity.getProperty("firstName"));
			user.setLastName((String) userentity.getProperty("lastName"));
			user.setSex((String) userentity.getProperty("sex"));
			user.setUserId((String) userentity.getProperty("userId"));
			result.add(user);
		}
		return result;
	}

	// get all upcoming projects for a user
	public ArrayList<Project> getUpcomingProjects(String userId) {
		ArrayList<Project> all = findAllProjects(userId);
		ArrayList<Project> upcoming = new ArrayList<Project>();
		for (Project proj : all) {
			long status = proj.getStatus();
			Date deadline = proj.getDeadline();
			Date today = new Date();
			if (isUpcoming(status, today, deadline)) {
				upcoming.add(proj);
			}
		}
		return upcoming;
	}

	// check whether (deadline - 7 < today < deadline)
	public boolean isUpcoming(long status, Date today, Date deadline) {
		long todayTime = today.getTime();
		long deadlineTime = deadline.getTime();
		long diffDays = (deadlineTime - todayTime) / (24 * 60 * 60 * 1000);
		if (status == 0 && today.before(deadline) && diffDays < 7) {
			return true;
		} else
			return false;
	}

	// get all on-going projects for a user
	public ArrayList<Project> getOngoingProjects(String userId) {
		ArrayList<Project> all = findAllProjects(userId);
		ArrayList<Project> ongoing = new ArrayList<Project>();
		for (Project proj : all) {
			long status = proj.getStatus();
			Date deadline = proj.getDeadline();
			Date today = new Date();
			if (status == 0)// && today.before(deadline))
				ongoing.add(proj);
		}
		return ongoing;
	}

	// get all completed projects for a user
	public ArrayList<Project> getCompletedProjects(String userId) {
		ArrayList<Project> all = findAllProjects(userId);
		ArrayList<Project> completed = new ArrayList<Project>();
		for (Project proj : all) {
			long status = proj.getStatus();
			if (status == 1)
				completed.add(proj);
		}
		return completed;
	}

	// get all favorite projects for a user
	public ArrayList<Project> getFavoriteProjects(String userId) {
		Key keyforPair = KeyFactory.createKey("pair", "default");
		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userId);
		Filter favFilter = new FilterPredicate("fav", FilterOperator.EQUAL, Long.valueOf("1"));
		CompositeFilter comFilter = CompositeFilterOperator.and(userFilter,
				favFilter);
		Query query = new Query("pair", keyforPair).setFilter(comFilter);
		List<Entity> pairs = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		ArrayList<Long> projids = new ArrayList<Long>();
		
		for (Entity pair : pairs) {
			projids.add((Long) pair.getProperty("projid"));
		}
//		return projids;
		
		return getProjects(projids);

//		for (Entity proj : list) {
//			Project tmp = new Project();
//			/*
//			 * No need to fecth tasks here, since a separate api will be
//			 * provided to get tasks
//			 */
//
//			tmp.setDeadline((Date) proj.getProperty("deadline"));
//			tmp.setTitle((String) proj.getProperty("title"));
//			tmp.setProid((Long) proj.getProperty("pid"));
//			tmp.setOwner((String) proj.getProperty("owner"));
//			tmp.setDescr((String) proj.getProperty("desc"));
//			tmp.setStatus((Long) proj.getProperty("status"));
//
//			ArrayList<User> users = getAllUsersProj((Long) proj
//					.getProperty("pid"));
//			if (users == null) {
//				tmp.setUserlist(new ArrayList<User>());
//			} else {
//				tmp.setUserlist(users);
//			}
//			result.add(tmp);
//		}
//		return result;
	}

	// get 4 most recent projects for a user
	public ArrayList<Project> getRecentProjects(String userId) {

		ArrayList<Long> ids = getRecentProjectIds(userId);
		return getProjects(ids);
	}

	// get 4 most recent project id for a user
	private ArrayList<Long> getRecentProjectIds(String userId) {
		Key key = KeyFactory.createKey("pair", "default");

		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userId);
		Query query = new Query("pair", key).setFilter(userFilter).addSort(
				"visittime", Query.SortDirection.DESCENDING);
		List<Entity> pairs = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(4));
		ArrayList<Long> result = new ArrayList<Long>();

		for (Entity pair : pairs) {
			Long tmp = (Long) pair.getProperty("projid");
			result.add(tmp);
		}
		return result;
	}

	public void editProject(Project proj) {
		long projid = proj.getProid();
		Key key = KeyFactory.createKey("proj", "default");
		Filter projFilter = new FilterPredicate("pid", FilterOperator.EQUAL,
				projid);
		Query query = new Query("proj", key).setFilter(projFilter);
		Entity project = datastore.prepare(query).asSingleEntity();

		project.setProperty("title", proj.getTitle());
		project.setProperty("desc", proj.getDescr());
		project.setProperty("status", proj.getStatus());
		project.setProperty("deadline", proj.getDeadline());

		datastore.put(project);
	}

	public void editTask(Task task) {
		String taskid = task.getTaskId();
		Key key = KeyFactory.createKey("task", "default");
		Filter projFilter = new FilterPredicate("tid", FilterOperator.EQUAL,
				taskid);
		Query query = new Query("task", key).setFilter(projFilter);
		Entity taskE = datastore.prepare(query).asSingleEntity();

		taskE.setProperty("title", task.getTitle());
		taskE.setProperty("desc", task.getDesc());
		taskE.setProperty("status", task.getStatus());
		taskE.setProperty("deadline", task.getDeadline());

		datastore.put(taskE);
	}
	
	public User getUserFromId(String userId) {
		Entity user = findUserEntity(userId);
		return getUserFromEntity(user);
	}
	
	public void addFavoriteProject(String userId, long projid) {
		Key key = KeyFactory.createKey("pair", "default");
		Filter projFilter = new FilterPredicate("projid", FilterOperator.EQUAL,
				projid);
		Filter userFilter = new FilterPredicate("userid", FilterOperator.EQUAL,
				userId);
		CompositeFilter comFilter = CompositeFilterOperator.and(userFilter,
				projFilter);
		Query query = new Query("pair", key).setFilter(comFilter);
		Entity pair = datastore.prepare(query).asSingleEntity();
		pair.setProperty("fav", 1);
		datastore.put(pair);
	}
	
	public void addCompleteProject(long projid) {
		Key key = KeyFactory.createKey("proj", "default");
		Filter projFilter = new FilterPredicate("pid", FilterOperator.EQUAL,
				projid);
		Query query = new Query("proj", key).setFilter(projFilter);
		Entity proj = datastore.prepare(query).asSingleEntity();
		proj.setProperty("status", 1);
		datastore.put(proj);
	}
	
}
