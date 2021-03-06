package org.umlg.javageneration.visitor.clazz;

import org.eclipse.uml2.uml.Interface;
import org.umlg.framework.Visitor;
import org.umlg.generation.Workspace;
import org.umlg.java.metamodel.annotation.OJAnnotatedClass;
import org.umlg.javageneration.util.UmlgClassOperations;
import org.umlg.javageneration.util.UmlgInterfaceOperations;
import org.umlg.javageneration.visitor.BaseVisitor;

public class InterfaceRuntimePropertyImplementorVisitor extends BaseVisitor implements Visitor<Interface> {

	public InterfaceRuntimePropertyImplementorVisitor(Workspace workspace) {
		super(workspace);
	}

	@Override
	public void visitBefore(Interface inf) {
		OJAnnotatedClass annotatedClass = findOJClass(inf);
		// addTumlRuntimePropertyEnum(annotatedClass, inf);

		RuntimePropertyImplementor.addTumlRuntimePropertyEnum(annotatedClass, UmlgClassOperations.propertyEnumName(inf), inf,
				UmlgInterfaceOperations.getAllProperties(inf), UmlgInterfaceOperations.hasCompositeOwner(inf), inf.getModel().getName());

	}

	@Override
	public void visitAfter(Interface inf) {
	}

}
