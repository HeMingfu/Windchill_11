package ext.com.lnties.mahesh.Folder;

import java.util.Iterator;
import java.util.List;

import ext.com.lnties.mahesh.PartDemo.NewPart;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class PartToFolder {

	static String containerPath = "/wt.inf.container.OrgContainer=LnT/wt.pdmlink.PDMLinkProduct=LnT_Product"; 
	static String folderPath = "/Default/Parts/";

	private static boolean partExists = false;
	
	public static void main(String[] args) throws WTException {

		String partName = "Folder_part_2";
		String partNumber = "0100111";

		Folder folder = PartToFolder.getMyFolder(containerPath, folderPath);

		System.out.println(folder.getFolderPath());

		
		List<WTPart> partsList = NewPart.getPartByNumber(partNumber);
		
		Iterator<WTPart> partIterator = partsList.iterator();
		while (partIterator.hasNext()) {
			WTPart part = partIterator.next();
			System.out.println("Part with Number : " + part.getNumber()+" already exists.");
			PartToFolder.partExists = true;
			break;
		}
		
		if(!PartToFolder.partExists) {
			PartToFolder.createMyWTpart(folder, containerPath, partName, partNumber);
		}

	}

	public static Folder getMyFolder(String containerPath, String folderPath) throws WTException {
		Folder folder = null;
		WTContainerRef containerRef = null;
		try {
			containerRef = WTContainerHelper.service.getByPath(containerPath);
			folder = FolderHelper.service.getFolder(folderPath, containerRef);

			if (folder != null) {
				System.out.println("folder with path: "+folderPath+" already esists.");
			}

		}catch(FolderNotFoundException f) {

			folder = FolderHelper.service.createSubFolder(folderPath, containerRef);
			System.out.println(f.toString());

			String res =  folder.getFolderPath();
			System.out.println("Created folder : "+ res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return folder;
	}

	public static void createMyWTpart(Folder folder, String containerPath, String partName, String partNumber) {

		WTPart part = null;


		try {
			part = WTPart.newWTPart();
			part.setName(partName);
			part.setNumber(partNumber);



			WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);//gets the container refernce by path

			part.setContainerReference(containerRef);//sets the container reference for part


			// assign the folder here
			FolderHelper.assignLocation((FolderEntry)part, folder);


			PersistenceHelper.manager.save(part);
			System.out.println("Part created successfully: "+"Part name :"+partName+" and Part Number"+partNumber);
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
