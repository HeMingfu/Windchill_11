package ext.com.lnties.mahesh.Users;


import java.util.Enumeration;

import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;

import wt.session.SessionHelper;
import wt.util.WTException;


public class UserInfo {

	public static void main(String[] args) throws WTException {

		String name = "maheshawaji@gmail.com";
		WTUser user = (WTUser) SessionHelper.manager.setPrincipal(name); //imp
		Enumeration<?> enumGroup = OrganizationServicesHelper.manager
				.parentGroups(SessionHelper.manager.setPrincipal(name), false);
		System.out.println("********************* User Details *********************");
		System.out.println("Full Name = "+ user.getFullName());
		System.out.println("Email id = "+ user.getEMail() +" " + user.getAuthenticationName());
		System.out.println("***Groups****");
		while (enumGroup.hasMoreElements()) {
			WTPrincipalReference principal = (WTPrincipalReference) enumGroup.nextElement();
			WTPrincipal groupPrincipal = (principal).getPrincipal();
			if(groupPrincipal instanceof WTGroup) {
				String groupDn =  groupPrincipal.getDn();
				if(groupDn != null) {
					String groupName = groupPrincipal.getName();
					if(!user.getOrganizationName().equals(groupName)){
						
						System.out.println(groupName);
						

					}
				}
			}

		}
		System.out.println("********************* User Details *********************");




	}
	
	/*public static String getLastLoginTime (WTUser user) {
		
		String userID = user.getAuthenticationName();
		String lastlogin = ""
				
				//String query = "SELECT a.ida2a2, a.username, TO_CHAR(a.eventtime, 'yyyy-mm-dd hh24:mi:ss') as eventtime FROM auditrecord a,(SELECT max(ida2a2)as max_ida2a2, username FROM auditrecord WHERE eventlabel='Login' GROUP BY username) b WHERE a.ida2a2=b.max_ida2a2 AND a.username=b.username ORDER BY eventtime desc;";
		
				
			{
					//String lastLoginDetails ="select eventtime FROM auditrecord where ida2a2=(select max(ida2a2) FROM auditrecord where eventlabel='Login' and idb5=(select 	ida2a2 from wtuser where name like '"+userID+"'))";
					DBQuery dbQuery = new DBQuery();
					String WTUserval = "(select ida2a2 from wtuser where upper(name) like upper('"+userID+"'))";
					wtUserval = (dbQuery.getResults(WTUserval));
					String wtUservallistString = "";
					for (String str : wtUserval){
						wtUservallistString = str;
					}
					System.out.println("#########wtUservallistString : " +wtUservallistString);
					System.out.println("#########wtUserval : " +wtUserval);
					String maxAuditRecord = "select max(ida2a2) FROM auditrecord where eventlabel='Login' and idb5 =" +wtUservallistString.trim();
					maxAuditRecordval = (dbQuery.getResults(maxAuditRecord));
					String maxAuditRecordvallistString = "";
					for (String st : maxAuditRecordval){
						maxAuditRecordvallistString = (st!=null)? st : "0";
					}
					System.out.println("#########maxAuditRecordvallistString : " +maxAuditRecordvallistString);
					maxAuditRecordvallistint = Long.parseLong(maxAuditRecordvallistString);
					System.out.println("#########maxAuditRecordval : " +maxAuditRecordval);
					System.out.println("#########maxAuditRecordvallistint : " +maxAuditRecordvallistint);
					//String loginTime = "select eventtime FROM auditrecord where ida2a2 =" +maxAuditRecordvallistint;
					String loginTime = "select to_char(cast(eventtime as timestamp) at time zone 'US/Eastern','MON dd, YYYY hh:mm:ss') || ' Eastern Time Zone' from auditrecord where ida2a2 =" +maxAuditRecordvallistint;

					login = (dbQuery.getResults(loginTime));
					System.out.println("#########login : " +login);
				}
				
		return "";
	}*/

}
