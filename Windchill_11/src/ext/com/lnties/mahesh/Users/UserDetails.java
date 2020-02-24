package ext.com.lnties.mahesh.Users;

import java.util.ArrayList;

import java.util.Enumeration;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;

import wt.query.QuerySpec;
import wt.query.SearchCondition;

import wt.util.WTException;

public class UserDetails {

	enum groups {};
	static String userName = "maheshawaji@gmail.com"; 
	public static void main(String[] args)  {
	
		List<WTUser> usersList = UserDetails.getUsersList(userName);
		Enumeration<?> groups = null;
		for (WTUser wtUser : usersList) {
			try {
				System.out.println("********************* User Details *********************");
				System.out.println("Full Name = "+ wtUser.getFullName());
				System.out.println("Email id = "+ wtUser.getEMail());
				groups = OrganizationServicesHelper.manager.parentGroups(wtUser, false);
				
				while (groups.hasMoreElements()) {
					/*String groupName = ((WTPrincipalReference)groups.nextElement()).getPrincipal().getName();
					System.out.println(groupName);*/
					
					WTPrincipal principal = ((WTPrincipalReference) groups.nextElement()).getPrincipal();
					if (principal instanceof WTGroup ) {
						String groupDn = principal.getDn();
						//System.out.println(groupName);
						
						if(groupDn != null) {
							String group = principal.getName();
						
							if (!wtUser.getOrganizationName().equals(group) ) {
								System.out.println(group);
							}
							
							
						}
					}
				}
				System.out.println("********************* User Details *********************");
			} catch (WTException e) {
				System.out.println(e.toString());
			}
		}
	}
	
	public static List<WTUser> getUsersList (String userName){
		List<WTUser> usersList = new ArrayList<WTUser>();
		try {
			QuerySpec querySpec  = new QuerySpec(WTUser.class);
			
			querySpec.appendWhere((new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL,userName)), null);
			
			QueryResult result = PersistenceHelper.manager.find(querySpec);
			
			while(result.hasMoreElements()) {
				
				WTUser user = (WTUser) result.nextElement();
				usersList.add(user);
			}
			
			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return usersList;
		
	}

}
