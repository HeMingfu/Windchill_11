package ext.com.lnties.mahesh.utilities;


import java.util.*;

import ext.com.lnties.mahesh.PartDemo.UpdatePart;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

public class FindPartParent {

	public static void main(String[] args) {
		try  {

			String partNumber = "0000000101";
			
			List<WTPart> partslist = UpdatePart.getPartByNumber(partNumber);
			WTPart part = null;
			//WTPart parentPart = null;
			for (WTPart wtPart : partslist) {
				part= wtPart;
			}
			
			
			List<String> resObjList = new ArrayList<>();
			
			//Set<WTPart> parentList = new HashSet<WTPart>();
			
			if(part!=null) {
				
				WTPartMaster partmaster = part.getMaster();
				//parentPart = getParent(part);
				
					
					resObjList = getResObjList();
					WTPart parentPart  = getParentcode(partmaster, resObjList);
					/*System.out.println("Child = "+part.getNumber()
							+ " | "+"Parent = "+parentPart.getNumber());*/
					
					/*for (WTPart wtPart : parentList) {
						System.out.println("Part Number:" +wtPart.getNumber() );
					}*/
					
					
					if(parentPart!=null) {
					System.out.println("Part Number:" +parentPart.getNumber() );
					
					}else {
						System.out.println("Part Number:" +part.getNumber() );
					}
				}
			
			

		}catch (Exception e) {
			
		}

	}
	
	public static WTPart getParent(WTPart part) throws PersistenceException, WTException {
		WTPart parentPart = null;
		
		QueryResult queryresult = ConfigHelper.service
				.filteredIterationsOf(
						(WTPartMaster) part.getMaster(),
						new LatestConfigSpec());
		
		while (queryresult.hasMoreElements()) {
			parentPart = (WTPart) queryresult.nextElement();
			
		}
		
		
		
		return parentPart;
		
	}
	
	
	
	public static List<String> getResObjList() throws WTException{
		String ECN_number = "00041";
		
		List<String> resObjList = new ArrayList<>();
		
		
		WTChangeOrder2 ecnObj = WTChangeOrder2.newWTChangeOrder2();

		QuerySpec querySpec = new QuerySpec(WTChangeOrder2.class);

		// finding ECN from number
		querySpec.appendWhere(
				new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.EQUAL, ECN_number),
				null);
		QueryResult result = PersistenceHelper.manager.find(querySpec);
		while (result.hasMoreElements()) {
			ecnObj = (WTChangeOrder2) result.nextElement();

		}
		
		WTPart wtpart = null;
		
		
		
		WTChangeActivity2 activity = WTChangeActivity2.newWTChangeActivity2();
		
		if (ecnObj != null) {
			QueryResult changeActivities = ChangeHelper2.service.getChangeActivities(ecnObj, true);

			//resObjList = fetchAllResObj(ecnObj);
			while (changeActivities.hasMoreElements()) {

				activity = (WTChangeActivity2) changeActivities.nextElement();

				// fetching release content/ resulting objects
				QueryResult queryResult = ChangeHelper2.service.getChangeablesAfter(activity);
				
				while(queryResult.hasMoreElements()) {
					
					wtpart = (WTPart) queryResult.nextElement();
					
					resObjList.add(wtpart.getNumber());
					
				}
				
				
				
				

			}

		}
		
		return  resObjList;
	}
	
	
	public static WTPart getParentcode(WTPartMaster part,
			List<String> resObjList) {

		WTPart parentPart = null;
		HashMap<String, WTPart> resList = new HashMap<String, WTPart>();

		try {
			wt.fc.QueryResult result = wt.part.WTPartHelper.service
					.getUsedByWTParts(part);
			
			while (result.hasMoreElements()) {
				WTPart wtUsedByPart = (WTPart) result.nextElement();
				
				if (resObjList.contains(wtUsedByPart.getNumber())) {
					resList.put(wtUsedByPart.getNumber(), wtUsedByPart);
				}
			}
			
			if (resList.size() > 0) {
				for (String parMapDetails : resList.keySet()) {
					WTPart wtParentList = (WTPart) resList.get(parMapDetails);
					QueryResult queryresult = ConfigHelper.service
							.filteredIterationsOf(
									(WTPartMaster) wtParentList.getMaster(),
									new LatestConfigSpec());
					
					while (queryresult.hasMoreElements()) {
						 parentPart = (WTPart) queryresult.nextElement();
						
						

					}
				}
				
					
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return parentPart;
	}

}
