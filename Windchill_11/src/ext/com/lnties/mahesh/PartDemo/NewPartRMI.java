package ext.com.lnties.mahesh.PartDemo;


	import java.lang.reflect.InvocationTargetException;
	import java.rmi.RemoteException;

import org.springframework.remoting.RemoteAccessException;

import com.ptc.core.lwc.server.PersistableAdapter;

	import com.ptc.core.meta.common.UpdateOperationIdentifier;

//import ext.com.lnties.mahesh.PartDemo.NewPartRMI;
import wt.fc.PersistenceHelper;
	import wt.fc.QueryResult;
	import wt.method.RemoteAccess;
	import wt.method.RemoteMethodServer;
	import wt.part.WTPart;
	import wt.pds.StatementSpec;
	import wt.query.QueryException;
	import wt.query.QuerySpec;
	import wt.query.SearchCondition;
	import wt.query.WhereExpression;
	import wt.session.SessionHelper;
	import wt.vc.wip.WorkInProgressHelper;



	/**
	 * @author 40000206
	 *
	 */
	public class NewPartRMI implements RemoteAccess {

		static String name=null;
		static WTPart part=null;
		
		public static void main(String[] args) throws RemoteException, InvocationTargetException, RemoteAccessException
		
		{
			
				RemoteMethodServer myServer = RemoteMethodServer.getDefault();
				
				myServer.setUserName("wcadmin");
				myServer.setPassword("wcadmin");
//				Class<?> classObjArr[] = {};
//				Object objArr[] = {};
				
				System.out.println("Inside main method");
				myServer.invoke("createPart", NewPartRMI.class.getName(), null, null, null); //invoking no-argument static method
			
			
			/*
			 * The arguments for invoke are:
	(String) the method name to be invoked
	(String) the class wherein the method may be found *
	(Object) the object upon which to execute the method *
	(Class[]) an array of class objects, one element for each of the method's arguments
	(Object[]) an array of objects, one element for each method argument.
	* Note that either the target class must be supplied (if performing a static invocation), or the target object must be supplied (if performing an instance invocation).

	To invoke a no-argument static method on a remote server, you would do something like:
	rms.invoke("methodName","fully.qualified.ClassName",null,null,null);

	Similarly, to invoke a no-argument instance method on an object, you would do something like:
	rms.invoke("methodName",null,targetObject,null,null);

	If the method has required arguments, the argument type arrays and argument arrays must be constructed and passed to the invoke.
	rms.invoke(
	    "methodName",
	    "fully.qualified.ClassName",
	    null,
	    new Class[]{String.class, String.class},
	    new Object[]{"Hi", "MrK"}
	  );
	  
	  */
		}
		

		public static void createPart() throws QueryException {
			System.out.println("Inside remote method");
			try {
				String partnumber="01001";	
				QuerySpec qspec=new QuerySpec(WTPart.class);
				qspec.appendWhere((WhereExpression) new SearchCondition(WTPart.class,WTPart.NUMBER,SearchCondition.LIKE,partnumber),new int[]{0,1});
				
				qspec.appendAnd();
				
				qspec.appendWhere((WhereExpression)(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE)), new int[]{0,1});
				
				
				QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) qspec );
				
				System.out.println("***********Part Information *************\n");
				
				while ( queryResult.hasMoreElements() ) {
					part = (WTPart) queryResult.nextElement();
					partnumber = part.getNumber();
					name = part.getName();
					System.out.println("Part Number is:" +partnumber);
					System.out.println("Part Name is :" +name);
					
					System.out.println("Assembly mode is :"+part.getPartType());
					System.out.println("************************"); 
					System.out.println("you are in working Copy");
					 
					 PersistableAdapter obj = new PersistableAdapter(part, null, SessionHelper.getLocale(), new UpdateOperationIdentifier());
					 
					 obj.load("name");
					 obj.set("name", "Test_Part1");
					 obj.persist();
					 obj.apply();
					 WorkInProgressHelper.service.checkin(part, "Updated by Workflow");
					 System.out.println("Part is checked in successfully...");
					 
					 if(part==null)
						{
							System.out.println("Parts are not present in Windchill system");
						}	
						
					 
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		

	}


