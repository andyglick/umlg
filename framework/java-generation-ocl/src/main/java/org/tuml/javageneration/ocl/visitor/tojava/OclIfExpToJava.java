package org.tuml.javageneration.ocl.visitor.tojava;

import org.eclipse.ocl.expressions.IfExp;
import org.eclipse.ocl.uml.CollectionType;
import org.eclipse.uml2.uml.Classifier;
import org.tuml.java.metamodel.OJIfStatement;
import org.tuml.java.metamodel.OJPathName;
import org.tuml.java.metamodel.OJVisibilityKind;
import org.tuml.java.metamodel.annotation.OJAnnotatedClass;
import org.tuml.java.metamodel.annotation.OJAnnotatedOperation;
import org.tuml.javageneration.ocl.visitor.HandleIfExp;
import org.tuml.javageneration.util.TumlClassOperations;
import org.tuml.javageneration.util.TumlCollectionKindEnum;

public class OclIfExpToJava implements HandleIfExp {

	protected OJAnnotatedClass ojClass;

	@Override
	public String handleIfExp(IfExp<Classifier> ifExp, String conditionResult, String thenResult, String elseResult) {
		OJIfStatement ifStatement = new OJIfStatement(conditionResult, "return " + thenResult, "return " + elseResult);
		String ifOperationName = "ifExp";
		OJPathName ifExpPathName;
		if (ifExp.getType() instanceof CollectionType) {
			CollectionType collectionType = (CollectionType)ifExp.getType();
			ifExpPathName = TumlCollectionKindEnum.from(collectionType.getKind()).getOjPathName();
			ifExpPathName.addToGenerics(TumlClassOperations.getPathName(collectionType.getElementType()));
		} else {
			ifExpPathName = TumlClassOperations.getPathName(ifExp.getType());
		}
		OJAnnotatedOperation oper = new OJAnnotatedOperation(ifOperationName + this.ojClass.countOperationsStartingWith(ifOperationName), ifExpPathName);
		this.ojClass.addToOperations(oper);
		oper.setVisibility(OJVisibilityKind.PRIVATE);
		oper.getBody().addToStatements(ifStatement);
		return oper.getName() + "()";
	}

	@Override
	public HandleIfExp setOJClass(OJAnnotatedClass ojClass) {
		this.ojClass = ojClass;
		return this;
	}

}
