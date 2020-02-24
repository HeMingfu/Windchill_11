package ext.com.lnties.mahesh.validators;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.fc.Persistable;

import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;

public class MyPartValidator extends DefaultUIComponentValidator {

	@Override
	public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey, UIValidationCriteria validationCriteria,
			java.util.Locale locale) throws WTException {
		
		System.out.println("This is Part data Validation method..");
		Persistable persistable = validationCriteria.getContextObject().getObject();
		
		WTPart wtPart = (WTPart) persistable;
		
		boolean ischeckedOut = WorkInProgressHelper.isCheckedOut(wtPart);
		
		
		
		
		
		// tested and working
		if(ischeckedOut) {
			return UIValidationResultSet
					.newInstance(UIValidationResult.newInstance(validationKey, UIValidationStatus.ENABLED));
		}else {
			return UIValidationResultSet
					.newInstance(UIValidationResult.newInstance(validationKey, UIValidationStatus.DISABLED));
		}
	}
}
