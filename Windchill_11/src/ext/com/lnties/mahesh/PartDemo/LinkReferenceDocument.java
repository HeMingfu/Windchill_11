package ext.com.lnties.mahesh.PartDemo;

import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;

import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.part.WTPart;

import wt.part.WTPartReferenceLink;

import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.CheckoutLink;

import wt.vc.wip.WorkInProgressHelper;

public class LinkReferenceDocument {

	public static void linkReferenceDoc(NmCommandBean paramNmCommandBean) {
		System.out.println("**** Inside linkReferenceDoc () ***********");

		try {
			NmOid NMOId = paramNmCommandBean.getActionOid();
			Object obj = NMOId.getRefObject();

			WTPart wTPart = null;

			WTDocument document = null;

			if (obj instanceof WTPart) {
				wTPart = (WTPart) obj;

				String partNumber = wTPart.getNumber();

				System.out.println("This is part : " + partNumber);

				document = getDocumentByNumber(partNumber);

				if (document != null) {
					System.out.println("This is Document : " + document.getName() + " :: " + document.getNumber());

					/*
					 * HashMap<WTPart, WTDocument> hashMap =
					 * getPartDocWorkingCopy(wTPart, document);
					 * 
					 * for(Entry<WTPart, WTDocument> entry: hashMap.entrySet())
					 * { System.out.println(entry.getKey() + " : " +
					 * entry.getValue());
					 * 
					 * wTPart = entry.getKey(); document = entry.getValue();
					 * 
					 * }
					 */

					// Checkout and getting Working copy of object
					
					// Document check out
					Folder docFolder = FolderHelper.service.getFolder((FolderEntry) document);
					CheckoutLink docCOLink = WorkInProgressHelper.service.checkout(document, docFolder, "");

					document = (WTDocument) docCOLink.getWorkingCopy();

					// part check out
					Folder partFolder = FolderHelper.service.getFolder((FolderEntry) wTPart);
					CheckoutLink partCOLink = WorkInProgressHelper.service.checkout(wTPart, partFolder, "");
					wTPart = (WTPart) partCOLink.getWorkingCopy();

					// Creating Link between part and doc
					// WTPartDescribeLink link=WTPartDescribeLink.newWTPartDescribeLink(wTPart, document);

					WTDocumentMaster docMaster = (WTDocumentMaster) document.getMaster();
					
					WTPartReferenceLink link = WTPartReferenceLink.newWTPartReferenceLink(wTPart, docMaster);
					
					PersistenceHelper.manager.save(link);
					
					System.out.println("Part and reference document link is created...");

					// checking in part and doc
					
					// Document check in
					document = (WTDocument) WorkInProgressHelper.service.checkin(document, " checked in");
					System.out.println("Checked in document...");
					
					// Document check in
					wTPart = (WTPart) WorkInProgressHelper.service.checkin(wTPart, "Checked in");
					System.out.println("checked in part...");

				} else {
					System.out.println("Document does not exist");
				}

			}

		} catch (WTException | WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static WTDocument getDocumentByNumber(String partNumber) {

		WTDocument document = null;
		try {

			QuerySpec querySpec = new QuerySpec(WTDocument.class);
			querySpec.appendWhere(
					new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, partNumber), null);
			QueryResult result = PersistenceHelper.manager.find(querySpec);

			while (result.hasMoreElements()) {
				document = (WTDocument) result.nextElement();
				System.out.println("Found document : " + document.getName() + " :: " + document.getNumber());
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return document;

	}

	

	/*
	 * public static HashMap <WTPart, WTDocument> getPartDocWorkingCopy (WTPart
	 * wtPart, WTDocument wtDocument) throws NonLatestCheckoutException,
	 * WorkInProgressException, WTPropertyVetoException, PersistenceException,
	 * WTException{
	 * 
	 * HashMap <WTPart, WTDocument> hashMap = new HashMap<WTPart, WTDocument>();
	 * 
	 * Document check out Folder docFolder =
	 * FolderHelper.service.getFolder((FolderEntry) document); CheckoutLink
	 * docCOLink = WorkInProgressHelper.service.checkout(document, docFolder,"");
	 * 
	 * document = (WTDocument) docCOLink.getWorkingCopy();
	 * 
	 * // part check out Folder partFolder =
	 * FolderHelper.service.getFolder((FolderEntry) wTPart); CheckoutLink
	 * partCOLink = WorkInProgressHelper.service.checkout(wTPart, partFolder,""); 
	 * wTPart = (WTPart) partCOLink.getWorkingCopy();
	 * 
	 * hashMap.put(wtPart, wtDocument);
	 * 
	 * return hashMap;
	 * 
	 * }
	 */

	
}
