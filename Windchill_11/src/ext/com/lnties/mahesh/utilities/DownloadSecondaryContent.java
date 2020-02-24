package ext.com.lnties.mahesh.utilities;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ptc.netmarkets.model.NmException;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;

public class DownloadSecondaryContent implements Serializable {
	
	private static final Logger log = LogR.getLogger(DownloadSecondaryContent.class.getName());
	
	
	public static void downloadFolderContentFiles(NmCommandBean paramNmCommandBean)
			throws WTException, WTInvalidParameterException, PropertyVetoException, IOException {
		
		String PATH = WTProperties.getLocalProperties().getProperty("wt.home")+ File.separator + "tempSecondaryDownload";
		System.out.println("Inside Class ParamNmCommandBean getNmOidSelectedInOpener() ### => "+ paramNmCommandBean.getNmOidSelectedInOpener());
		/*FileOutputStream fos = new FileOutputStream("sample.zip");
        ZipOutputStream zipOS = new ZipOutputStream(fos);*/
		ArrayList al = paramNmCommandBean.getNmOidSelectedInOpener();
		HashMap hm=paramNmCommandBean.getMap();
		
		
		System.out.println("Command Bean Hash Map is"+hm);
		
		System.out.println("ArrayList with OID Selected"+al);
		Iterator itr = al.iterator();
		List<File> arr=new ArrayList<>();
		while (itr.hasNext()) {
			ReferenceFactory rf = new ReferenceFactory();
			WTObject obj = (WTObject) rf.getReference(itr.next().toString()).getObject();
			System.out.println(obj.getClass());
			ContentHolder content = wt.content.ContentHelper.service.getContents((ContentHolder) obj);
			
			System.out.println("Content Object"+content);
			
			Vector vcontent = wt.content.ContentHelper.getApplicationData(content); 
			
			log.info("Iterating the Application Data Contents starts");
			
			if (vcontent.size() > 0) {
				for (int i = 0; i < vcontent.size(); i++) {
					wt.content.ApplicationData appData = (ApplicationData) vcontent.get(i);
					System.out.println("App Data"+appData);
					
					String currfileName = appData.getFileName();
					System.out.println("Current File Name::"+currfileName);
					
					File saveAsFile = new java.io.File(PATH, currfileName);
					System.out.println("saveAsFile::"+saveAsFile);
					// writeToZipFile(saveAsFile, zipOS);
					
					wt.content.ContentServerHelper.service.writeContentStream((ApplicationData) appData,saveAsFile.getCanonicalPath());
					arr.add(saveAsFile);
					
				}
			}
			
			log.info("Iterating the Application Data Contents stops");
			
			System.out.println("Array List after adding files"+arr);
		}
		// call zip maker call
		writeToZipFile(arr,paramNmCommandBean);
		System.exit(0);
		/*for (File fileToZip : arr) {
			if (fileToZip.delete()) {
				System.out.println("File : " + fileToZip.getName() + " deleted...");
			} else
				System.out.println("File file.txt doesn't exist in the project root directory");
		}
*/
		
		
	}
	public static void writeToZipFile(List<File> arr, NmCommandBean paramNmCommandBean)throws FileNotFoundException, IOException {
		
		
		String PATH = WTProperties.getLocalProperties().getProperty("wt.home")+ File.separator + "tempSecondaryDownload";
		log.info("writeToZipFile Method STARTS");
		 List<String> srcFiles = new ArrayList<>();
		 
		 for (File file : arr) {
			srcFiles.add(PATH+File.separator+file.getName());
		}
		
		System.out.println("All the src files"+srcFiles);
		Date date = new Date();  
    	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");  
    	    String strDate= formatter.format(date);  
    	    System.out.println("Start Date"+strDate);  
			
			String ZipFilePath=PATH+ File.separator+strDate+".zip";
	        FileOutputStream fos = new FileOutputStream(ZipFilePath);
	        ZipOutputStream zipOut = new ZipOutputStream(fos);
	        for (String srcFile : srcFiles) {
	            File fileToZip = new File(srcFile);
	            FileInputStream fis = new FileInputStream(fileToZip);
	            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
	            zipOut.putNextEntry(zipEntry);
	 
	            byte[] bytes = new byte[4*1024];
	            int size =0;
	            while((size  = fis.read(bytes)) >= 0) {
	                zipOut.write(bytes, 0, size);
	            }
				zipOut.flush();
	            fis.close();
	        }
	        zipOut.close();
	        System.out.println("Zip Out File"+zipOut);
	        
	        fos.close();
	        
	        for (File fileToZip : arr) {
				if (fileToZip.delete()) {
					System.out.println("File : " + fileToZip.getName() + " deleted...");
				} else
					System.out.println("File file.txt doesn't exist in the project root directory");
			}
	        
	        //Downloading of file starts
	        
	        HttpServletResponse response=paramNmCommandBean.getResponse();
	        
	        File folder = new File(PATH);
	        
	        File[] files = folder.listFiles();
	 
	        System.out.println("Zip File inside "+folder+" is"+files[0]);
	        
	        File zipFile=files[0];
	        
	        
	        
	       /* File file = new File(ZipFilePath);
	        File fil = new File()*/
	        
	        System.out.println("File to be downloaded------> "+zipFile+"\n FIle Name ---> "+zipFile.getName());
	        
	        
	        byte[] ba = java.nio.file.Files.readAllBytes(Paths.get(zipFile.getAbsolutePath()));
	        		 
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition","attachment; filename=\"" + zipFile.getName() + "\"");
	
			ServletOutputStream sos = response.getOutputStream();
			sos.write(ba);
			sos.flush();
	        
            /*if(!zipFile.exists()){
                System.out.println("file not found");
            }
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition","attachment; filename=\"" + zipFile.getName() + "\"");

            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(zipFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0){
               out.write(buffer, 0, length);
            }
            in.close();
            out.flush();	 */     
    }
	
}