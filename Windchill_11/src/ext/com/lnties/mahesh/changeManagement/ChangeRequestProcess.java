package ext.com.lnties.mahesh.changeManagement;

import wt.change2.WTChangeRequest2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class ChangeRequestProcess implements RemoteAccess {
	public static void processChangeRequest() {
		String ecrName = "ECR Test 1";
		
		try {

			// ChangeRequestProcess.createChangeRequest(ecrName); // creates ECR
			// System.out.println("Created ECR successfully");

			System.out.println("Fetching details...");

			WTChangeRequest2 ecr = ChangeRequestProcess.getChangeRequestByName(ecrName); // fetch ecr by name

			System.out.println("Name :" + ecr.getName());
			System.out.println("Number :" + ecr.getNumber());
			System.out.println("Creator Name :" + ecr.getCreatorFullName());
			System.out.println("Life cycle state :" + ecr.getLifeCycleState());

		} catch (WTException e) {

			e.printStackTrace();
		}
	}
	
	/*public static void createChangeRequest(String ecrName) throws WTException, WTPropertyVetoException {

		WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);

		WTChangeRequest2 ecr = WTChangeRequest2.newWTChangeRequest2();

		ecr.setContainerReference(containerRef);

		ecr.setName(ecrName);

		PersistenceHelper.manager.store(ecr);

	}*/

	public static WTChangeRequest2 getChangeRequestByName(String ecrName) throws WTException {
		WTChangeRequest2 ecr = WTChangeRequest2.newWTChangeRequest2();

		QuerySpec querySpec = new QuerySpec(WTChangeRequest2.class);
		querySpec.appendWhere(
				new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.NAME, SearchCondition.EQUAL, ecrName),
				null);
		QueryResult result = PersistenceHelper.manager.find(querySpec);

		while (result.hasMoreElements()) {
			ecr = (WTChangeRequest2) result.nextElement();
		}
		return ecr;
	}
	
	
	
}