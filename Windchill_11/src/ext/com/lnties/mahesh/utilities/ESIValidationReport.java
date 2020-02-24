package ext.com.lnties.mahesh.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;




import com.ptc.windchill.enterprise.epmdoc.config.epmdocResource;

import ext.carrier.wc.change.changenotice.IBAReader;
import ext.carrier.wc.esi.CarrierESIValidationReportAttach;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.clients.beans.explorer.WT;
import wt.clients.prodmgmt.WTPartHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

public class ESIValidationReport {

	public static Properties prop;

	public static Properties properties = null;

	static File propertyFile;

	static {
		try {
			properties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		WTProperties wtProperties = null;
		try {
			wtProperties = WTProperties.getLocalProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String wtHome = wtProperties.getProperty("wt.codebase.location");
		propertyFile = new File(wtHome + File.separator + "esiReport.properties");
		prop = new Properties();
		try {
			prop.load(new FileInputStream(propertyFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// **** main method starts here *****
	public static void main(String[] args) {

		try (Scanner sc = new Scanner(System.in)) {

			System.out.print("Enter ECN number = ");
			String ECN_number = sc.nextLine();
			System.out.println("");
			ESIValidationReport.processECN(ECN_number);

		}
	}

	public static ArrayList<String> fetchAllResObj(WTChangeOrder2 ecnObject) {
		ArrayList<String> fetchResObjList = new ArrayList<String>();
		WTChangeActivity2 activity = null;
		try {
			QueryResult activities = ChangeHelper2.service.getChangeActivities(ecnObject, true);

			while (activities.hasMoreElements()) {
				activity = (WTChangeActivity2) activities.nextElement();
				QueryResult queryResult = ChangeHelper2.service.getChangeablesAfter(activity);

				wt.fc.WTObject wtObject = null;
				WTPart wtPart = null;
				while (queryResult.hasMoreElements()) {
					wtObject = (wt.fc.WTObject) queryResult.nextElement();
					try {
						if (wtObject instanceof WTPart) {
							wtPart = (WTPart) wtObject;
							fetchResObjList.add(wtPart.getNumber());
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			}
			fetchResObjList.trimToSize();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fetchResObjList;
	}

	public static HSSFWorkbook createReportHeader(HSSFWorkbook workbook, WTChangeOrder2 ecnObject,
			ArrayList<String> headerForPartList) {

		HSSFSheet sheet = workbook.createSheet(ecnObject.getNumber());
		sheet.setDefaultColumnWidth(25);
		/*
		 * HSSFSheet sheet2 = workbook.createSheet(ecnObject.getNumber()+"-2");
		 * sheet2.setDefaultColumnWidth(25);
		 */

		HSSFCellStyle cellStyle;
		HSSFFont font;
		font = workbook.createFont();
		font.setFontName("Courier New");
		font.setFontHeightInPoints(Short.parseShort("12"));
		// font.setBoldweight((short) 4);
		font.setBold(true); // ESR65193 WC11 CPS12
		font.setColor((short) 12);
		cellStyle = workbook.createCellStyle();
		cellStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		cellStyle.setFont(font);
		cellStyle.setWrapText(true);

		sheet = workbook.getSheetAt(0);
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN Number"));
		cell.setCellStyle(cellStyle);

		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(ecnObject.getNumber()));

		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN Name"));
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(ecnObject.getName()));

		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN DT"));
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(""));

		row = sheet.createRow(3);
		cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN State"));
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(ecnObject.getState().toString()));

		row = sheet.createRow(4);
		row = sheet.createRow(5);
		for (int col = 0; col < headerForPartList.size(); col++) {
			cell = row.createCell(col);
			cell.setCellValue(new HSSFRichTextString(headerForPartList.get(col)));
			cell.setCellStyle(cellStyle);
		}

		/*
		 * sheet2 = workbook.getSheetAt(1); HSSFRow row1 = sheet2.createRow(0); HSSFCell
		 * cell1 = row1.createCell(0); cell1.setCellValue(new
		 * HSSFRichTextString("ECN Number")); cell1.setCellStyle(cellStyle); cell1 =
		 * row1.createCell(1); cell1.setCellValue(new
		 * HSSFRichTextString(ecnObject.getNumber()));
		 * 
		 * row1 = sheet2.createRow(1); cell1 = row1.createCell(0);
		 * cell1.setCellValue(new HSSFRichTextString("ECN Name"));
		 * cell1.setCellStyle(cellStyle); cell1 = row1.createCell(1);
		 * cell1.setCellValue(new HSSFRichTextString(ecnObject.getName()));
		 * 
		 * row1 = sheet2.createRow(2); cell1 = row1.createCell(0);
		 * cell1.setCellValue(new HSSFRichTextString("ECN DT"));
		 * cell1.setCellStyle(cellStyle); cell1 = row1.createCell(1);
		 * cell1.setCellValue(new HSSFRichTextString(""));
		 * 
		 * row1 = sheet2.createRow(3); cell1 = row1.createCell(0);
		 * cell1.setCellValue(new HSSFRichTextString("ECN State"));
		 * cell1.setCellStyle(cellStyle); cell1 = row1.createCell(1);
		 * cell1.setCellValue(new HSSFRichTextString(ecnObject.getState() .toString()));
		 * 
		 * row1 = sheet2.createRow(4); row1 = sheet2.createRow(5);
		 * 
		 * for (int col1 = 0; col1 < headerList2.size(); col1++) { cell1 =
		 * row1.createCell(col1); cell1.setCellValue(new
		 * HSSFRichTextString(headerList2.get(col1))); cell1.setCellStyle(cellStyle); }
		 */

		return workbook;
	}

	public static HSSFWorkbook createReportData(HSSFWorkbook workbook, WTPart resultingPart,
			WTChangeActivity2 cActivity, int rowcnt, boolean isParent, boolean resObjPresent,
			boolean isEPM, String describeDocNumber, boolean hasParent,String parentNumber )
			throws WTException {

		// System.out.println("****createReportData********");
		String resObjNumber = "";
		String resObjName = "";
		String resObjSource = "";
		String resObjDefaultUnit = "";
		String resObjDrawingNum = "";
		String resAssDrawNum = "";
		String resDescribedByDocNum = "";
		String resObjPartQualifier = "";
		String resObjDesignCenter = "";
		String lineNumber = "";
		String findNumber = "";
		String resObjUnit = "";
		String resObjLc = "";
		String resParentNum = "";
		String resParentDT = "";
		String resQuantity = "";
		String resCurrentDT = "";
		String notAppl = "N/A";
		String mapDT = "";
		String resObjRev = "";
		String resObjIter = "";
		String LocDefault = "";
		String resLocation = "";
		String epmDrawNumList = "";
		String resContainer = "";
		String resUSCategory = "";
		String resUSClassifierEmail = "";
		String resUSDateClassified = "";
		String resUSECCN = "";
		String resUSJurisdiction = "";
		String resUSRationale = "";
		String resUSSource = "";

		QueryResult result1 = wt.part.WTPartHelper.service.getDescribedByDocuments(resultingPart);
		WTObject wtObject2 = null;
		WTDocument doc = null;
		EPMDocument epmDoc = null;

		// WTObject wtObject3= null;
		// EPMDocument epmDoc2 = null;

		/*
		 * while (result1.hasMoreElements()) {
		 * 
		 * wtObject2 = (WTObject) result1.nextElement(); if (wtObject2 != null) { if
		 * (wtObject2 instanceof EPMDocument) { epmDoc = (EPMDocument) wtObject2;
		 * System.out.println("Described by : EPM Document : " + epmDoc.getNumber());
		 * 
		 * } else if (wtObject2 instanceof WTDocument ) { doc = (WTDocument) wtObject2;
		 * System.out.println("Described by : WTDocument : " + doc.getNumber()); } }
		 * 
		 * }
		 */

		resParentNum = resultingPart.getNumber();
		resObjName = resultingPart.getName();
		LocDefault = resultingPart.getLocation();
		resContainer = resultingPart.getContainerName();
		resLocation = LocDefault.replace("Default", resContainer);
		resObjLc = resultingPart.getLifeCycleState().getDisplay();
		resObjRev = ((WTPart) resultingPart).getVersionIdentifier().getValue();
		resObjIter = ((WTPart) resultingPart).getIterationIdentifier().getValue();
		/*
		 * IBAHolder ibahldrPart = (IBAHolder) resultingPart; IBAReader compreader = new
		 * ext.carrier.wc.change.changenotice.IBAReader( ibahldrPart);
		 */
		resObjPartQualifier = notAppl;

		resObjDrawingNum = notAppl;

		resObjDesignCenter = notAppl;
		resUSCategory = notAppl;
		resUSClassifierEmail = notAppl;
		resUSDateClassified = notAppl;
		resUSECCN = notAppl;
		resUSJurisdiction = notAppl;
		resUSRationale = notAppl;
		resUSSource = notAppl;
		resObjSource = resultingPart.getSource().getDisplay();
		resObjDefaultUnit = resultingPart.getDefaultUnit().getDisplay();

		resCurrentDT = notAppl;
		
		if(describeDocNumber.equals("")) {
			describeDocNumber=notAppl;
		}
		
		
		
		
			/*resObjNumber=resultingPart.getNumber();*/
		
		if(hasParent) {
			resObjNumber=resultingPart.getNumber();
		}else {
			resObjNumber = parentNumber;
		}
		
			//resParentNum = parentNumber ;			
		

		// creating sheet
		
		

		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = sheet.createRow(rowcnt);

		HSSFRow ecnDTValue = sheet.getRow(2);
		String ecnCellValue = ecnDTValue.getCell(1).toString();

		HSSFCell cell = row.createCell(0);
		cell.setCellValue(parentNumber);

		cell = row.createCell(1);	
		cell.setCellValue(resObjNumber);
		 
			
		
		cell = row.createCell(2);
		cell.setCellValue(resObjName);

		cell = row.createCell(3);
		cell.setCellValue(resObjPartQualifier);

		cell = row.createCell(4);
		cell.setCellValue(resObjRev + "." + resObjIter);

		cell = row.createCell(5);
		cell.setCellValue(resQuantity);

		cell = row.createCell(6);
		cell.setCellValue(resObjDefaultUnit);

		cell = row.createCell(7);
		cell.setCellValue(resObjSource);

		cell = row.createCell(8);
		if (lineNumber != "") {
			cell.setCellValue(lineNumber);
		} else {
			cell.setCellValue(notAppl);
		}

		cell = row.createCell(9);
		cell.setCellValue(resObjDrawingNum);
		cell = row.createCell(10);
		cell.setCellValue(resObjDesignCenter);
		cell = row.createCell(11);
		cell.setCellValue(cActivity.getNumber());
		cell = row.createCell(12);
		cell.setCellValue(cActivity.getLifeCycleState().getDisplay());
		cell = row.createCell(13);
		cell.setCellValue(cActivity.getName());
		cell = row.createCell(14);
		if (resParentDT != "") {
			cell.setCellValue(resParentDT);
		} else {
			cell.setCellValue("N/A");
		}
		cell = row.createCell(15);
		if (resCurrentDT != "") {
			cell.setCellValue(resCurrentDT);
		} else {
			cell.setCellValue("");
		}

		cell = row.createCell(16);

		if (resCurrentDT != "") {
			if (resCurrentDT.contains(ecnCellValue)) {
				cell.setCellValue(resCurrentDT);
			} else {
				cell.setCellValue(resCurrentDT + "," + ecnCellValue);
			}
		} else {
			cell.setCellValue(ecnCellValue);
		}

		cell = row.createCell(17);
		if (resObjPresent) {
			cell.setCellValue("YES");
		} else {
			cell.setCellValue("NO");
		}
		cell = row.createCell(18);
		cell.setCellValue(resObjLc);
		cell = row.createCell(19);
		cell.setCellValue(resObjUnit);
		cell = row.createCell(20);
		if (findNumber != "") {
			cell.setCellValue(findNumber);
		} else {
			cell.setCellValue(notAppl);
		}
		cell = row.createCell(21);
		cell.setCellValue(resLocation);

		cell = row.createCell(22);

		if (!isEPM && (!describeDocNumber.equals("")) ) {
			cell.setCellValue(describeDocNumber);
		} else {
			cell.setCellValue(notAppl);
		}

		// adding new cell here
		cell = row.createCell(23);

		if (isEPM  && (!describeDocNumber.equals("")) ) {
			cell.setCellValue(describeDocNumber);
		} else {
			cell.setCellValue(notAppl);
		}

		cell = row.createCell(24);
		cell.setCellValue(resUSCategory);
		cell = row.createCell(25);
		cell.setCellValue(resUSClassifierEmail);
		cell = row.createCell(26);
		cell.setCellValue(resUSDateClassified);
		cell = row.createCell(27);
		cell.setCellValue(resUSECCN);
		cell = row.createCell(28);
		cell.setCellValue(resUSJurisdiction);
		cell = row.createCell(29);
		cell.setCellValue(resUSRationale);
		cell = row.createCell(30);
		cell.setCellValue(resUSSource);

		return workbook;

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
	
	public static HSSFWorkbook wirteSinglePart( QueryResult queryResult, HSSFWorkbook report, int rowcnt,
			WTChangeActivity2 activity, List<String> resObjList) throws WTException {
		
		WTPart wtPart = null;
		WTObject wtObject = null;
		WTObject wtObject2 = null;
		WTDocument doc = null;
		EPMDocument epmDoc = null;
		
		boolean hasParent = false;
		String parentNumber = "";
		
		
		WTPart parentPart = null;
		//WTObject wtObject3 = null;
		//EPMDocument epmDoc2 = null;
		// iterating resulting objects result
		boolean isEPM = false;
		while (queryResult.hasMoreElements()) {
			wtObject = (wt.fc.WTObject) queryResult.nextElement();

			if (wtObject instanceof WTPart) {
				wtPart = (WTPart) wtObject;
				
				WTPartMaster partmaster = wtPart.getMaster();
				parentPart = getParentcode(partmaster,resObjList );
				
				
				
				if(parentPart!=null) {
					System.out.println("Part Number:" +parentPart.getNumber() );
					parentNumber = parentPart.getNumber();
					hasParent = true;
					}else {
						System.out.println("Part Number:" +wtPart.getNumber() );
						hasParent = false;
						parentNumber = wtPart.getNumber();
					}
				//parentNumber = parentPart.getNumber();
				

				String describeDocNumber = "";
				// processing described by documents
				QueryResult result1 = wt.part.WTPartHelper.service.getDescribedByDocuments(wtPart);
				if(result1.size()>0) {
				while (result1.hasMoreElements()) {
					wtObject2 = (WTObject) result1.nextElement();
					if (wtObject2 != null) {
						if (wtObject2 instanceof EPMDocument) {
							epmDoc = (EPMDocument) wtObject2;
							System.out.println("Described by : EPM Document : " + epmDoc.getNumber());
							isEPM = true;
							describeDocNumber = epmDoc.getNumber();

						} else if (wtObject2 instanceof WTDocument) {
							doc = (WTDocument) wtObject2;
							System.out.println("Described by : WTDocument : " + doc.getNumber());
							isEPM = false;
							describeDocNumber = doc.getNumber();
						}

						// write here
						// call createReportdata method here
						report = createReportData(report, wtPart, activity, rowcnt, false, true, isEPM,
								describeDocNumber, hasParent, parentNumber);
								
						rowcnt++;
						isEPM = false;
					}

					

				}
			}else {
				report = createReportData(report, wtPart, activity, rowcnt, false, true, isEPM,
						describeDocNumber,hasParent, parentNumber);
						
				rowcnt++;
				
			}

				
			}

		}
		
		return report;
	}

	public static void processECN(String ECN_number) {

		HSSFWorkbook report = new HSSFWorkbook();

		try {

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

			ArrayList<String> headerForPartList = new ArrayList<>();
			String headerForPartString = prop.getProperty("headerForPart");

			StringTokenizer headerPartDetail = new StringTokenizer(headerForPartString, "|");

			while (headerPartDetail.hasMoreElements()) {
				headerForPartList.add(headerPartDetail.nextToken());
			}

			for (String string : headerForPartList) {
				System.out.println(string);
			}

			report = createReportHeader(report, ecnObj, headerForPartList);

			

			List<String> resObjList = new ArrayList<String>();
			WTChangeActivity2 activity = WTChangeActivity2.newWTChangeActivity2();
			//boolean BOMexists = false;
			int rowcnt = 6;
			//WTPart wtPart = null;
			if (ecnObj != null) {
				QueryResult changeActivities = ChangeHelper2.service.getChangeActivities(ecnObj, true);

				resObjList = fetchAllResObj(ecnObj);
				while (changeActivities.hasMoreElements()) {

					activity = (WTChangeActivity2) changeActivities.nextElement();

					// fetching release content/ resulting objects
					QueryResult queryResult = ChangeHelper2.service.getChangeablesAfter(activity);
					
					
					report = wirteSinglePart(queryResult, report, rowcnt, activity, resObjList);
					
					
					

				}

			}
			ESIValidationReportAttach reportWriter = new ESIValidationReportAttach();
			String reportPath = reportWriter.ESIvalidationReportAttach(ecnObj.getNumber(), report);
			System.out.println(reportPath);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
