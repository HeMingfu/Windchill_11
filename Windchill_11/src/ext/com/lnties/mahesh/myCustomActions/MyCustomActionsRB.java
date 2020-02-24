package ext.com.lnties.mahesh.myCustomActions;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;


@RBUUID("ext.com.lnties.mahesh.myCustomActions.MyCustomActionsRB")
public class MyCustomActionsRB extends WTListResourceBundle{

	@RBEntry("Utilities")
	public static final String UTILITIES = "object.utilities.description";
	
	@RBEntry("State Validator")
	public static final String STATE_VALIDATOR = "object.utilities.description";
	
	
	@RBEntry("Custom Utilities Page")
	public static final String Custom_Utilities_Page = "object.customUtilitiesPage.description";
}
