package ext.com.lnties.mahesh.changeManagement;

import java.util.ArrayList;
import java.util.List;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeIssue;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.part.WTPart;
import wt.fc.WTObject;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

public class ProblemReportTest {

	private static String containerPath = "/wt.inf.container.OrgContainer=LnT/wt.pdmlink.PDMLinkProduct=LnT_Product";
	

	public static void main(String[] args) throws WTPropertyVetoException {
		String prName = "Karthican PR";

		try {
			
			ProblemReportTest.createProblemReport(prName);

			System.out.println("Fetching details....");

			WTChangeIssue problemReport = ProblemReportTest.getProblemReportByName(prName);

			System.out.println("Name :" + problemReport.getName());
			System.out.println("Number :" + problemReport.getNumber());
			System.out.println("Creator Name :" + problemReport.getCreatorFullName());
			System.out.println("Life cycle state :" + problemReport.getLifeCycleState());

			List<WTObject> affectedObjectsList = ProblemReportTest.getAffectedObjects(problemReport);
			System.out.println("Fetching affected objects");

			for (WTObject wtObject : affectedObjectsList) {

				if (wtObject != null) {

					if (wtObject instanceof WTPart) {
						WTPart currentPart = (WTPart) wtObject;
						System.out.println("Part Name :" + currentPart.getName() + " | " + " Part Number "
								+ currentPart.getNumber());
					}
				} else {
					System.out.println("No affected objects specified");
					break;
				}

			}
			
			List<WorkItem> tasksList = ProblemReportTest.getAlltasks(problemReport);
			
			for (WorkItem task : tasksList) {
				if (task != null) {
				System.out.println("Task Description : "+task.getDescription()+" | State : "+task.getStatus());
				}
			}
			

		} catch (WTException e) {

			e.printStackTrace();
		}

	}

	public static void createProblemReport(String prName) throws WTException, WTPropertyVetoException {
		WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);

		WTChangeIssue pr = WTChangeIssue.newWTChangeIssue();
		pr.setName(prName);
		pr.setContainerReference(containerRef);

		PersistenceHelper.manager.store(pr);
		
		System.out.println("Created problem report...");
	}

	public static WTChangeIssue getProblemReportByName(String name) throws WTException {

		WTChangeIssue pr = WTChangeIssue.newWTChangeIssue();

		QuerySpec querySpec = new QuerySpec(WTChangeIssue.class);
		querySpec.appendWhere(new SearchCondition(WTChangeIssue.class, WTChangeIssue.NAME, SearchCondition.EQUAL, name),
				null);
		QueryResult result = PersistenceHelper.manager.find(querySpec);
		while (result.hasMoreElements()) {
			pr = (WTChangeIssue) result.nextElement();

		}

		return pr;
	}

	public static WTChangeIssue getProblemReportByNumber(String prNumber) throws WTException {
		WTChangeIssue pr = WTChangeIssue.newWTChangeIssue();

		QuerySpec querySpec = new QuerySpec(WTChangeIssue.class);
		querySpec.appendWhere(
				new SearchCondition(WTChangeIssue.class, WTChangeIssue.NUMBER, SearchCondition.EQUAL, prNumber), null);
		QueryResult result = PersistenceHelper.manager.find(querySpec);
		while (result.hasMoreElements()) {
			pr = (WTChangeIssue) result.nextElement();

		}

		return pr;
	}

	public static List<WTObject> getAffectedObjects(WTChangeIssue issue) throws ChangeException2, WTException {
		List<WTObject> affectedObjectsList = new ArrayList<>();

		QueryResult result = ChangeHelper2.service.getChangeables(issue);

		while (result.hasMoreElements()) {
			WTObject wtObject = (WTObject) result.nextElement();
			affectedObjectsList.add(wtObject);
		}

		return affectedObjectsList;
	}
	
	public static List<WorkItem> getAlltasks(WTChangeIssue issue) throws WTException{
		
		QueryResult result = WorkflowHelper.service.getWorkItems(issue);
		
		List<WorkItem> tasksList = new ArrayList<>();
		
		while (result.hasMoreElements()) {
			WorkItem object =  (WorkItem) result.nextElement();
			tasksList.add(object);
		}
		
		return tasksList;
		
	}
}
