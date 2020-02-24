package ext.com.lnties.mahesh.PartDemo;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import wt.folder.CabinetBased;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;

public class CheckinCheckout {

	
	static String partNumber = "123852";
	private static boolean partExists;
	
	enum ObjectAction {CHECKIN, CHECKOUT, UNDOCHECKOUT}
	
	public static void main(String[] args)  {
		List<WTPart> partsList = NewPart.getPartByNumber(partNumber);
		
		Iterator<WTPart> partIterator = partsList.iterator();
		
		CheckinCheckout.partExists = false;
		WTPart part = null;
		while (partIterator.hasNext()) {
			part = partIterator.next();
			System.out.println("Part with Number : " + part.getNumber()+" already exists.");
			CheckinCheckout.partExists = true;
			break;
			
			}
		
		if(!partExists) 
			System.out.println("Part with Number : " + part.getNumber()+" does not exist.");
		else
			CheckinCheckout.performActionOnPart (part);		
	
	}
	
	public static void performActionOnPart (WTPart part) {
		
		System.out.println("****************************************");
		System.out.println("Please select the desired action: ");
		System.out.println("1. Checkin");
		System.out.println("2. Checkout");
		System.out.println("3. Undo-checkout");
		System.out.println("****************************************");		
		
		try (Scanner sc = new Scanner(System.in)){
			System.out.print("Enter value :");
			
			String choice = sc.nextLine().toUpperCase();
			
			System.out.println("");
			
			ObjectAction action = ObjectAction.valueOf(choice);
			
			System.out.println(action.toString());
			
			switch (action) {
			case CHECKIN:
				
				if(isPartCheckedOut(part)) {
					part = CheckinCheckout.doCheckin(part);
				}
				else {
					System.out.println("Part is already checked in");
				}
				break;
			case CHECKOUT:
				
				if(isPartCheckedOut(part)) {
					System.out.println("Part is already checked out");
				}
				else {
					CheckinCheckout.doCheckout(part);
				}
				
				break;
			case UNDOCHECKOUT:
				System.out.println("option unavailable, coming soon");
				break;
			}
		} catch (Exception e) {
			System.out.println("wrong input, please enter either of CHECKIN, CHECKOUT, UNDOCHECKOUT only");
		}
		
	}
	
	
	public static boolean isPartCheckedOut(WTPart part) throws WTException {		
		
		return  WorkInProgressHelper.isCheckedOut(part);
				
	}
	
	public static void doCheckout(WTPart part) throws WTException, WTPropertyVetoException {
		
		try {
			System.out.println("inside checkout method");
			
			String folderPath = FolderHelper.service.getFolderPath((CabinetBased)part);
			
			System.out.println(folderPath);		
			
			Folder folder = FolderHelper.service.getFolder((FolderEntry)part);			
			
			CheckoutLink link = WorkInProgressHelper.service.checkout(part, folder, "checked out for test");
			
			System.out.println(link.toString());
			
		} catch (Exception e) {
			System.out.println("Error : "+ e.toString());
		}		
		
	}
	
	public static WTPart doCheckin(WTPart part) throws WorkInProgressException, WTPropertyVetoException, PersistenceException, WTException {
		
		System.out.println("inside checkin method");
		
		
		String comment = "";
		try (Scanner sc = new Scanner(System.in)){
			
			System.out.print("Enter checkin comments: ");
			comment = sc.nextLine();
			System.out.println();
			
			part = (WTPart) WorkInProgressHelper.service.checkin(part, comment);
		} catch (Exception e) {
			System.out.println("Invalid input : Error : "+e.toString());
		}
				
		return part;
	}

}
