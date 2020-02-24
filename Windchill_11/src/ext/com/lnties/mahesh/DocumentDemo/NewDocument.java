/**
 * 
 */
package ext.com.lnties.mahesh.DocumentDemo;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author 40000206
 *
 */
public class NewDocument {

	static String docName = "DemoDocument_1";
	static String docNumber = "01111";
	
	//the container where the document will be created/located
	static String containerPath = "/wt.inf.container.OrgContainer=LnT/wt.pdmlink.PDMLinkProduct=LnT_Product";
	
	public static void main(String[] args) throws WTPropertyVetoException, WTException
	
	{
		

		try {
			WTDocument doc = WTDocument.newWTDocument();
			doc.setName(docName);
			doc.setNumber(docNumber);
			
			WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);
			doc.setContainerReference(containerRef);
			PersistenceHelper.manager.save(doc);
			
			System.out.println("Document created successfully: "+"Document name : "+docName+" and Part Number: "+docNumber);
			
		} catch (Exception e) {
			System.out.println("Exception: "+e);
		}
		
	}

}
