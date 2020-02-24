package ext.com.lnties.mahesh.Folder;

import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;

public class NewFolder {

	static String containerPath = "/wt.inf.container.OrgContainer=LnT/wt.pdmlink.PDMLinkProduct=LnT_Product"; 
	static String folderPath = "/Default/Test/";

	public static void main(String[] args) throws WTException {

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
	}

}


/*if (folder == null) 
folder = FolderHelper.service.createSubFolder(folderPath, containerRef);
else 
System.out.println("folder with path: "+folderPath+" already esists.");*/