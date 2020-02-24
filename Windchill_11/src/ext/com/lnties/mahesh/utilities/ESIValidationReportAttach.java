package ext.com.lnties.mahesh.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ESIValidationReportAttach {

	
	public String ESIvalidationReportAttach(String ECNNumber,HSSFWorkbook report) throws IOException {
		String logFileName = "D:/Temp/" +ECNNumber+"_"+getCurrentTime("yyyyMMddHHmmss") + ".xls";
		
		File file = new File (logFileName);
		
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		try {

			report.write(fileOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		return logFileName;
	}
	
	public String getCurrentTime(String dateFormat) {
		java.util.Calendar calender = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
				dateFormat);
		format.setTimeZone(java.util.TimeZone.getTimeZone("America/New_York"));
		return format.format(calender.getTime());
	}
}
