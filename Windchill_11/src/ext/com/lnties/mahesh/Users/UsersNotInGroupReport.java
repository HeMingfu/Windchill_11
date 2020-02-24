package ext.com.lnties.mahesh.Users;

import java.util.Enumeration;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.util.WTException;

public class UsersNotInGroupReport {

	public static void main(String[] args) throws WTException {
		QuerySpec qs = new QuerySpec(WTUser.class);
		QueryResult qr = PersistenceHelper.manager.find((QuerySpec) qs);
		System.out.println("Below are the User not mapped into any role-");
		int count=0;
		WTUser user=null;
		while (qr.hasMoreElements()) {
			user = (WTUser) qr.nextElement();


			if(!(user.getDomainRef().getName().equals("Unaffiliated")) && !(user.getDomainRef().getName().equals("System")) ){
				
				String name = user.getName();		
				Enumeration<?> enumGroup = OrganizationServicesHelper.manager.parentGroups(SessionHelper.manager.setPrincipal(name), false);
				
				
				while (enumGroup.hasMoreElements()) {
					WTPrincipalReference principal = (WTPrincipalReference) enumGroup.nextElement();
					
					WTPrincipal groupPrincipal = (principal).getPrincipal();
					if (groupPrincipal instanceof WTGroup) {
						String groupDn = groupPrincipal.getDn();
						if (groupDn != null) {
							String groupName = groupPrincipal.getName();

							/*if(user.getOrganizationName() == null){
								break;
							}*/


							if (!user.getOrganization().getName().equals(groupName)) {
								count++;
								break;
							}

						}
					}

				}
				if(count==0){
					System.out.println(user.getName()+":: context::"+user.getOrganization().getName()+user.getDomainRef().getName());
				}
				count=0;
			}
		}
	}

	}


