package org.umlg.javageneration.ocl.visitor.tojava;

import java.util.List;

import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;

public class OclFirstExprToJava extends BaseHandleOperationExp {

	@Override
	public String handleOperationExp(OperationCallExp<Classifier, Operation> oc, String sourceResult, List<String> argumentResults) {
		StringBuilder result = new StringBuilder();
		result.append(sourceResult);
		result.append(".first()");
		return result.toString();
	}

}
