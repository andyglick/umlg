package org.tuml.javageneration.ocl.visitor.tojava;

import java.util.List;

import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Parameter;
import org.tuml.java.metamodel.annotation.OJAnnotatedClass;
import org.tuml.javageneration.ocl.util.TumlOclUtil;
import org.tuml.javageneration.ocl.visitor.HandleIteratorExp;
import org.tuml.javageneration.util.TumlClassOperations;

public class OclAnyExpToJava implements HandleIteratorExp {

	/**
	 * Generates something like below
	 * 
	 * return getSet().select(
	 * 		new BooleanBodyExpressionEvaluator<OclTestCollection>() {
	 * 			@Override
	 * 			public Boolean evaluate(OclTestCollection e) {
	 * 				return getName().equals("john");
	 * 			});
	 * 		}
	 */
	public String handleIteratorExp(OJAnnotatedClass ojClass, IteratorExp<Classifier, Parameter> callExp, String sourceResult, List<String> variableResults, String bodyResult) {
		if (variableResults.size() != 1) {
			throw new IllegalStateException("An ocl select iterator expression may only have on iterator, i.e. variable");
		}
		Variable<Classifier, Parameter> variable = callExp.getIterator().get(0);
		
		String variableType = TumlClassOperations.className(variable.getType());
		StringBuilder result = new StringBuilder(sourceResult);
		result.append(".any(");
		result.append("new ");
		result.append(HandleIteratorExp.BooleanExpressionEvaluator);
		result.append("<");
		result.append(variableType);
		result.append(">() {\n");
		result.append("    @Override\n");
		result.append("    public Boolean evaluate(");
		result.append(TumlOclUtil.removeVariableInit(variableResults.get(0)));
		result.append(") {\n");
		result.append("        return ");
		result.append(bodyResult);
		result.append(";\n    }");
		result.append("\n})");
		return result.toString();
	}
	
}
