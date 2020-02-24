package ext.com.lnties.mahesh.Users;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.util.WTException;
import java.util.*;

public class AllWindchillUsers {

	public static void main(String[] args) {
		
		
		try {
			
			
			QuerySpec qs = new QuerySpec(WTUser.class);
			QueryResult qr = PersistenceHelper.manager.find((QuerySpec) qs);
			
			List<WTUser> usersList = new ArrayList<>();
			
			while (qr.hasMoreElements()) {
				WTUser user = (WTUser) qr.nextElement();
				usersList.add(user);
			}
			
			
			for (WTUser wtUser : usersList) {
				
				if(!(wtUser.getDomainRef().getName().equals("Unaffiliated")) &&  !(wtUser.getDomainRef().getName().equals("System")) ) {
				
				System.out.println("Name : " + wtUser.getName() + " Org : " + wtUser.getOrganization().getName());
			}
			}
			
		} catch (WTException e) {
			
			e.printStackTrace();
		}
		
		
		
	}

}
