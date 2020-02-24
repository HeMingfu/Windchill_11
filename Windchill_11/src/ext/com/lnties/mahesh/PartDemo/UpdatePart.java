package ext.com.lnties.mahesh.PartDemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
//import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class UpdatePart {

	static String name=null;
	static WTPart part=null;

	public static void main(String[] args) throws WTException, WTPropertyVetoException  {

		List<WTPart> partsList = UpdatePart.getPartByNumber("0100123");

		Iterator<WTPart> partIterator = partsList.iterator();

		while (partIterator.hasNext()) {
			WTPart part = partIterator.next();
			System.out.println("Part Number : " + part.getNumber());
			System.out.println("Part Name : " + part.getName());

			// Changing the Name and Number of part
			
			WTPartMaster partMaster = part.getMaster();
			WTPartMasterIdentity newPartMaster = (WTPartMasterIdentity) partMaster.getIdentificationObject();
			
			newPartMaster.setName("Test_Part1");
			IdentityHelper.service.changeIdentity(partMaster, newPartMaster);
			PersistenceHelper.manager.refresh(partMaster);
			}
	}

	public static List<WTPart> getPartByNumber (String partNumber) {

		List<WTPart> partsList = null;
		try {
			partsList = new ArrayList<WTPart>();

			QuerySpec querySpec = new QuerySpec(WTPart.class);
			querySpec.appendWhere(new SearchCondition(WTPart.class,WTPart.NUMBER, SearchCondition.EQUAL, partNumber), null);
			QueryResult result = PersistenceHelper.manager.find(querySpec);

			while (result.hasMoreElements()) {
				WTPart part = (WTPart) result.nextElement();
				partsList.add(part);
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return partsList;

	}
}	


