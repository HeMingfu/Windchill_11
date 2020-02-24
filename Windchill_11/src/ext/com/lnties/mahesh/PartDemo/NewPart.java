/**
 * This program is used to create a new part in Windchill via code
 */
package ext.com.lnties.mahesh.PartDemo;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.part.WTPart;

import wt.query.QuerySpec;
import wt.query.SearchCondition;

/**
 * @author 40000206
 *
 */
public class NewPart {

	static String partName = "DemoPart_1";
	static String partNumber = "0100123";
	
	//the container where the Part will be created/located
	static String containerPath = "/wt.inf.container.OrgContainer=LnT/wt.pdmlink.PDMLinkProduct=LnT_Product"; 
	
	private static boolean partExists = false;
	
	public static void main(String[] args) throws WTException, WTPropertyVetoException
	{
		List<WTPart> partsList = NewPart.getPartByNumber(partNumber);
		
		Iterator<WTPart> partIterator = partsList.iterator();
				
		while (partIterator.hasNext()) {
			WTPart part = partIterator.next();
			System.out.println("Part with Number : " + part.getNumber()+" already exists.");
			NewPart.partExists = true;
			break;
			
			}
		
		
		if (!NewPart.partExists)
			NewPart.createNewPart(partNumber, partName);
		
	}
	
	public static void createNewPart(String partNumber, String partName) {
		
		
		try {
			WTPart part = WTPart.newWTPart();
			
			part.setName(partName);
			part.setNumber(partNumber);
					
			WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);//gets the container refernce by path
			
			part.setContainerReference(containerRef);//sets the container reference for part
			PersistenceHelper.manager.store(part);
			System.out.println("Part created successfully: "+"Part name :"+partName+" and Part Number"+partNumber);
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
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
			
			System.out.println(e.toString());
		}

		return partsList;

	}

}
