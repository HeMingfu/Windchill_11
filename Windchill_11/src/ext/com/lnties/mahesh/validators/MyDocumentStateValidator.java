package ext.com.lnties.mahesh.validators;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;

import com.ptc.wvs.livecycle.assembler.Locale;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.lifecycle.State;
import wt.util.WTException;

public class MyDocumentStateValidator extends DefaultUIComponentValidator{

	@Override
	public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey, UIValidationCriteria validationCriteria,
			java.util.Locale locale) throws WTException {
		//UIValidationResultSet resultSet = UIValidationResultSet.newInstance();
		
		
		System.out.println("This is Data Validation method..");
		Persistable persistable = validationCriteria.getContextObject().getObject();
		WTDocument document = (WTDocument) persistable;
		
		document = (WTDocument) persistable;
		System.out.println("Document Lifecycle name is: " + document.getLifeCycleName());
		State LCState = document.getLifeCycleState();
		System.out.println("Document State: " + LCState);
		System.out.println("Document Creater Name " + document.getCreator().getName());
		
		if(State.RELEASED.equals(LCState)) {
			return UIValidationResultSet
					.newInstance(UIValidationResult.newInstance(validationKey, UIValidationStatus.ENABLED));
		}else {
			return UIValidationResultSet
					.newInstance(UIValidationResult.newInstance(validationKey, UIValidationStatus.DISABLED));
		}
	}
	
	
	
	
}
