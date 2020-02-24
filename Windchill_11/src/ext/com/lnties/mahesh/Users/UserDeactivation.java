package ext.com.lnties.mahesh.Users;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import wt.change2.WTChangeIssue;
import wt.change2.WTChangeRequest2;
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTSet;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

public class UserDeactivation {

	public static void main(String[] args) {

		String userName = "";
		try (Scanner sc = new Scanner(System.in)) {

			System.out.print("Enter username : ");
			userName = sc.nextLine().trim();

			System.out.println("");

			System.out.println("Fetching details of : " + userName);

			WTUser user = UserDeactivation.getUserByname(userName); // fetching user here

			if (user != null) {

				System.out.println("Full Name = " + user.getFullName());
				System.out.println("Email id = " + user.getEMail());

				/*
				 * System.out.println("********************* User tasks *********************");
				 * // fetching user's pending // tasks List<WorkItem> userTasksList =
				 * UserDeactivation.getAllTasks(user);
				 * 
				 * UserDeactivation.displayTasks(userTasksList); // calling display method to
				 * print task details
				 * System.out.println("********************* User tasks *********************");
				 */

				// UserDeactivation.getWorkSpacesByUser(user);

				UserDeactivation.getUserRoles(user);

				System.out.print("Want to remove user from role? Press : Y/N :- ");
				String vote = sc.next().trim();

				if (vote.equalsIgnoreCase("y")) {

					System.out.print("Enter role to remove :");
					String roleName = sc.nextLine().trim();
					System.out.println("");

					System.out.print("Enter container :");
					String containerName = sc.nextLine().trim();
					System.out.println("");

					UserDeactivation.removeUserFromRole(user, containerName, roleName);

					System.out.println("deleted user from role....");
					UserDeactivation.getUserRoles(user);
				}

				System.out.println("Thanks, Bye!");

			} else {
				System.out.println("user does not exist.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void removeUserFromRole(WTUser user, String containerName, String roleName)
			throws QueryException, WTException {

		QueryResult containers = PersistenceHelper.manager.find(new QuerySpec(ContainerTeam.class));

		Enumeration<?> enumRoleList = null;

		while (containers.hasMoreElements()) {
			ContainerTeam contTeam = (ContainerTeam) containers.nextElement();

			enumRoleList = ContainerTeamHelper.service.findContainerTeamGroups(contTeam,
					ContainerTeamHelper.ROLE_GROUPS);

			while (enumRoleList.hasMoreElements()) {
				WTGroup group = (WTGroup) enumRoleList.nextElement();

				if (group.isMember(user)) {
					Role role = Role.toRole(group.getName());

					if (roleName.equals(role.getFullDisplay()) && containerName.equals(contTeam.getName())) {
						System.out.println(contTeam.getName() + " | " + role.getFullDisplay());
						ContainerTeamHelper.service.removeMember(contTeam, role, user);

					}

					// System.out.println(contTeam.getName()+" | "+role.getFullDisplay());

				}

			}

		}
	}

	public static void getUserRoles(WTUser user) throws QueryException, WTException {

		QueryResult containers = PersistenceHelper.manager.find(new QuerySpec(ContainerTeam.class));

		/*
		 * List<String> containerList = new ArrayList<>(); List<String> rolesList = new
		 * ArrayList<>();
		 */
		Enumeration<?> enumRoleList = null;

		while (containers.hasMoreElements()) {
			ContainerTeam contTeam = (ContainerTeam) containers.nextElement();

			enumRoleList = ContainerTeamHelper.service.findContainerTeamGroups(contTeam,
					ContainerTeamHelper.ROLE_GROUPS);

			while (enumRoleList.hasMoreElements()) {
				WTGroup group = (WTGroup) enumRoleList.nextElement();

				if (group.isMember(user)) {
					Role role = Role.toRole(group.getName());

					System.out.println(contTeam.getName() + " | " + role.getFullDisplay());

					System.out.println(contTeam.getContainerReference().getName() + " | " + role.getFullDisplay());

				}

			}

		}

	}

	public static void getWorkSpacesByUser(WTUser user) throws WTException {

		WTSet set = EPMWorkspaceHelper.manager.getWorkspaces(user, null);

		Iterator<?> value = set.iterator();

		System.out.println("**** Worsk space ****");
		while (value.hasNext()) {

			ObjectReference objectRef = (ObjectReference) value.next();

			WTObject obj = (WTObject) objectRef.getObject();

			if (obj instanceof EPMWorkspace) {
				EPMWorkspace workspace = (EPMWorkspace) obj;
				System.out.println(workspace.getName());

			}

			// System.out.println(value.next().toString());
		}
		System.out.println("**** Worsk space ****");

	}

	public static void displayTasks(List<WorkItem> userTasksList) {
		for (WorkItem workItem : userTasksList) {

			if (workItem != null) {
				System.out.println(workItem.getPrimaryBusinessObject().getObject().getClass().getSimpleName());
				WTObject obj = (WTObject) workItem.getPrimaryBusinessObject().getObject();

				if (obj instanceof WTChangeRequest2) {
					WTChangeRequest2 req = (WTChangeRequest2) obj;
					System.out.println("Name : " + req.getName() + " | Number: " + req.getNumber());
				} else if (obj instanceof WTChangeIssue) {
					WTChangeIssue req = (WTChangeIssue) obj;
					System.out.println("Name : " + req.getName() + " | Number: " + req.getNumber());
				}

				System.out.println(workItem.getDescription() + " | State : " + workItem.getStatus());
			} else {
				System.out.println("no tasks");
				break;
			}
		}

	}

	public static WTUser getUserByname(String userName) throws WTException {

		WTUser user = null;

		QuerySpec querySpec = new QuerySpec(WTUser.class);
		querySpec.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, userName), null);
		QueryResult result = PersistenceHelper.manager.find(querySpec);

		while (result.hasMoreElements()) {
			user = (WTUser) result.nextElement();
		}

		return user;

	}

	public static List<WorkItem> getAllTasks(WTUser user) throws WTException {

		QueryResult result = WorkflowHelper.service.getWorkItems(user);

		List<WorkItem> tasksList = new ArrayList<>();

		while (result.hasMoreElements()) {
			WorkItem object = (WorkItem) result.nextElement();
			tasksList.add(object);
		}

		return tasksList;

	}

	/*
	 * public static void displayContainerRolesDeatils (QueryResult containers,
	 * Enumeration<?> enumRoleList, WTUser user) throws WTInvalidParameterException,
	 * WTException { System.out.println("Inside display method...");
	 * 
	 * 
	 * while(containers.hasMoreElements()) { ContainerTeam contTeam =
	 * (ContainerTeam) containers.nextElement();
	 * 
	 * enumRoleList = ContainerTeamHelper.service.findContainerTeamGroups(contTeam,
	 * ContainerTeamHelper.ROLE_GROUPS);
	 * 
	 * while(enumRoleList.hasMoreElements()) { WTGroup group = (WTGroup)
	 * enumRoleList.nextElement();
	 * 
	 * if (group.isMember(user)) { Role role = Role.toRole(group.getName());
	 * 
	 * System.out.println(contTeam.getName()+" | "+role.getFullDisplay());
	 * 
	 * 
	 * }
	 * 
	 * } }
	 * 
	 * }
	 */

}
