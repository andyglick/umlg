package org.tuml.javageneration.visitor.property;

import org.eclipse.uml2.uml.Property;
import org.opaeum.java.metamodel.OJField;
import org.opaeum.java.metamodel.OJIfStatement;
import org.opaeum.java.metamodel.OJParameter;
import org.opaeum.java.metamodel.OJPathName;
import org.opaeum.java.metamodel.annotation.OJAnnotatedClass;
import org.opaeum.java.metamodel.annotation.OJAnnotatedOperation;
import org.tuml.framework.Visitor;
import org.tuml.generation.Workspace;
import org.tuml.javageneration.util.PropertyWrapper;
import org.tuml.javageneration.util.TinkerGenerationUtil;
import org.tuml.javageneration.util.TumlValidationEnum;
import org.tuml.javageneration.visitor.BaseVisitor;

public class PropertyValidatorBuilder extends BaseVisitor implements Visitor<Property> {

	public PropertyValidatorBuilder(Workspace workspace) {
		super(workspace);
	}

	@Override
	public void visitBefore(Property p) {
		PropertyWrapper propertyWrapper = new PropertyWrapper(p);
		if (propertyWrapper.isOne() && propertyWrapper.isDataType() && !propertyWrapper.isDerived() && !propertyWrapper.isQualifier()) {
			OJAnnotatedClass owner = findOJClass(p);
			buildValidator(owner, propertyWrapper);
		}
	}

	@Override
	public void visitAfter(Property element) {

	}

	public static void buildValidator(OJAnnotatedClass owner, PropertyWrapper propertyWrapper) {
		OJAnnotatedOperation validateProperty = new OJAnnotatedOperation(propertyWrapper.validator());
		validateProperty.setReturnType(new OJPathName("java.util.List").addToGenerics(TinkerGenerationUtil.TumlConstraintViolation));
		validateProperty.addToParameters(new OJParameter(propertyWrapper.fieldname(), propertyWrapper.javaBaseTypePath()));
		owner.addToOperations(validateProperty);
		OJField result = new OJField("result", new OJPathName("java.util.List").addToGenerics(TinkerGenerationUtil.TumlConstraintViolation));
		result.setInitExp("new ArrayList<" + TinkerGenerationUtil.TumlConstraintViolation.getLast() + ">()");
		owner.addToImports(new OJPathName("java.util.ArrayList"));
		validateProperty.getBody().addToLocals(result);

		for (TumlValidationEnum e : TumlValidationEnum.values()) {
			if (propertyWrapper.hasValidation(e)) {
				OJIfStatement ifValidate = new OJIfStatement("!" + TinkerGenerationUtil.TumlValidator.getLast() + "." + e.getMethodName() + "(" + propertyWrapper.fieldname() + ", "
						+ propertyWrapper.getValidation(e).toStringForMethod() + ")");
				ifValidate.addToThenPart("result.add(new " + TinkerGenerationUtil.TumlConstraintViolation.getLast() + "(\"" + e.name() + "\", \""
						+ propertyWrapper.getQualifiedName() + "\", \"" + e.name() + " does not pass validation!\"))");
				validateProperty.getBody().addToStatements(ifValidate);
				owner.addToImports(TinkerGenerationUtil.TumlValidator);
				validateProperty.getBody().addToStatements(ifValidate);
			}
		}

		validateProperty.getBody().addToStatements("return result");
	}
}