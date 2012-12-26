package org.tuml.javageneration.ocl.visitor;

import org.eclipse.ocl.expressions.TupleLiteralPart;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;
import org.tuml.java.metamodel.annotation.OJAnnotatedClass;

public interface HandleTupleLiteralPart {
	String handleTupleLiteralPart(TupleLiteralPart<Classifier, Property> part, String valueResult);
	HandleTupleLiteralPart setOJClass(OJAnnotatedClass ojClass);
}
