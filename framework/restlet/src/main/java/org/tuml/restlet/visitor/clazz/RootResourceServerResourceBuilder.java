package org.tuml.restlet.visitor.clazz;

import java.util.Set;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.tuml.java.metamodel.OJBlock;
import org.tuml.java.metamodel.OJField;
import org.tuml.java.metamodel.OJForStatement;
import org.tuml.java.metamodel.OJIfStatement;
import org.tuml.java.metamodel.OJPackage;
import org.tuml.java.metamodel.OJParameter;
import org.tuml.java.metamodel.OJPathName;
import org.tuml.java.metamodel.OJTryStatement;
import org.tuml.java.metamodel.OJVisibilityKind;
import org.tuml.java.metamodel.annotation.OJAnnotatedClass;
import org.tuml.java.metamodel.annotation.OJAnnotatedField;
import org.tuml.java.metamodel.annotation.OJAnnotatedInterface;
import org.tuml.java.metamodel.annotation.OJAnnotatedOperation;
import org.tuml.java.metamodel.annotation.OJAnnotationValue;
import org.tuml.java.metamodel.annotation.OJEnum;
import org.tuml.java.metamodel.annotation.OJEnumLiteral;
import org.tuml.framework.Visitor;
import org.tuml.generation.Workspace;
import org.tuml.javageneration.util.Namer;
import org.tuml.javageneration.util.TinkerGenerationUtil;
import org.tuml.javageneration.util.TumlClassOperations;
import org.tuml.restlet.util.TumlRestletGenerationUtil;

public class RootResourceServerResourceBuilder extends BaseServerResourceBuilder implements Visitor<Class> {

    public RootResourceServerResourceBuilder(Workspace workspace, String sourceDir) {
        super(workspace, sourceDir);
    }

    @Override
    public void visitBefore(Class clazz) {
        if (!clazz.isAbstract() && !TumlClassOperations.hasCompositeOwner(clazz)) {

            OJAnnotatedInterface annotatedInf = new OJAnnotatedInterface(TumlClassOperations.className(clazz) + "sServerResource");
            OJPackage ojPackage = new OJPackage(Namer.name(clazz.getNearestPackage()) + ".restlet");
            annotatedInf.setMyPackage(ojPackage);
            addToSource(annotatedInf);

            OJAnnotatedClass annotatedClass = new OJAnnotatedClass(TumlClassOperations.className(clazz) + "sServerResourceImpl");
            annotatedClass.setSuperclass(TumlRestletGenerationUtil.ServerResource);
            annotatedClass.setMyPackage(ojPackage);
            annotatedClass.addToImplementedInterfaces(annotatedInf.getPathName());
            annotatedClass.setVisibility(TumlClassOperations.getVisibility(clazz.getVisibility()));
            addToSource(annotatedClass);

            addDefaultConstructor(annotatedClass);
            addGetRootObjectRepresentation(clazz, annotatedInf, annotatedClass);
//            addPutDeleteObjectRepresentation(clazz, annotatedInf, annotatedClass, true);
//            addPutDeleteObjectRepresentation(clazz, annotatedInf, annotatedClass, false);
            addPostObjectRepresentation(clazz, annotatedInf, annotatedClass, REST.POST);
            addPostObjectRepresentation(clazz, annotatedInf, annotatedClass, REST.PUT);
            addPostObjectRepresentation(clazz, annotatedInf, annotatedClass, REST.DELETE);
            addToRouterEnum(clazz, annotatedClass);

        }
    }

    @Override
    public void visitAfter(Class clazz) {
    }

    private void addPostObjectRepresentation(Classifier concreteClassifier, OJAnnotatedInterface annotatedInf, OJAnnotatedClass annotatedClass, REST action) {
        OJAnnotatedOperation postInf;
        if (action == REST.POST) {
            postInf = new OJAnnotatedOperation("post", TumlRestletGenerationUtil.Representation);
        } else if (action == REST.PUT) {
            postInf = new OJAnnotatedOperation("put", TumlRestletGenerationUtil.Representation);
        } else {
            postInf = new OJAnnotatedOperation("delete", TumlRestletGenerationUtil.Representation);
        }

        postInf.addToParameters(new OJParameter("entity", TumlRestletGenerationUtil.Representation));
        annotatedInf.addToOperations(postInf);
        postInf.addAnnotationIfNew(new OJAnnotationValue(TumlRestletGenerationUtil.Post, "json"));

        OJAnnotatedOperation post;
        if (action == REST.POST) {
            post = new OJAnnotatedOperation("post", TumlRestletGenerationUtil.Representation);
        } else if (action == REST.PUT) {
            post = new OJAnnotatedOperation("put", TumlRestletGenerationUtil.Representation);
        } else {
            post = new OJAnnotatedOperation("delete", TumlRestletGenerationUtil.Representation);
        }

        post.addToParameters(new OJParameter("entity", TumlRestletGenerationUtil.Representation));
        post.addToThrows(TumlRestletGenerationUtil.ResourceException);
        annotatedClass.addToImports(TumlRestletGenerationUtil.ResourceException);
        TinkerGenerationUtil.addOverrideAnnotation(post);
        TinkerGenerationUtil.addSuppressWarning(post);

        OJPathName parentPathName = TumlClassOperations.getPathName(concreteClassifier);

        OJTryStatement ojTryStatement = new OJTryStatement();
        OJField mapper = new OJField("mapper", TinkerGenerationUtil.ObjectMapper);
        mapper.setInitExp("new ObjectMapper()");
        ojTryStatement.getTryPart().addToLocals(mapper);

        OJAnnotatedField entityText = new OJAnnotatedField("entityText", "String");
        entityText.setInitExp("entity.getText()");
        ojTryStatement.getTryPart().addToLocals(entityText);

        OJField jsonData = new OJField("json", "java.lang.StringBuilder");
        jsonData.setInitExp("new StringBuilder()");
        ojTryStatement.getTryPart().addToLocals(jsonData);
        ojTryStatement.getTryPart().addToStatements("json.append(\"[\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\"{\\\"data\\\": [\")");

        OJIfStatement ifTextNull = new OJIfStatement("entityText != null");
        ojTryStatement.getTryPart().addToStatements(ifTextNull);

        OJAnnotatedField objectO = new OJAnnotatedField("o", new OJPathName("Object"));
        objectO.setInitExp("mapper.readValue(" + entityText.getName() + ", Object.class)");
        ifTextNull.getThenPart().addToLocals(objectO);
        OJIfStatement ifArray = new OJIfStatement("o instanceof ArrayList");
        OJPathName genericsForArray = new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object");
        OJField array = new OJField("array", new OJPathName("java.util.List").addToGenerics(genericsForArray));
        array.setInitExp("(ArrayList<Map<String, Object>>)o");
        annotatedClass.addToImports("java.util.ArrayList");
        ifArray.getThenPart().addToLocals(array);

        OJField count = new OJField("count", "int");
        count.setInitExp("0");
        ifArray.getThenPart().addToLocals(count);

        ifTextNull.getThenPart().addToStatements(ifArray);
        OJForStatement forArray = new OJForStatement("map", new OJPathName("java.util.Map").addToGenerics(new OJPathName("String")).addToGenerics(
                new OJPathName("Object")), "array");
        ifArray.addToThenPart(forArray);
        forArray.getBody().addToStatements("count++");
        if (action == REST.POST || action == REST.PUT) {
            forArray.getBody().addToStatements("json.append(" + action.getMethodName() + "(map))");
        } else {
            forArray.getBody().addToStatements(action.getMethodName() + "(map)");

        }
        OJIfStatement ifToAddCommaToJson = new OJIfStatement("count < array.size()");
        ifToAddCommaToJson.addToThenPart("json.append(\",\")");
        forArray.getBody().addToStatements(ifToAddCommaToJson);

        OJField map = new OJField("map", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object"));
        map.setInitExp("(Map<String, Object>) o");
        ifArray.setElsePart(new OJBlock());
        ifArray.getElsePart().addToLocals(map);
        if (action == REST.POST || action == REST.PUT) {
            ifArray.getElsePart().addToStatements("json.append(" + action.getMethodName() + "(map))");
        } else {
            ifArray.getElsePart().addToStatements(action.getMethodName() + "(map)");
        }

        ifTextNull.addToElsePart("json.append(add())");

        if (action == REST.POST) {
            addPostResource(concreteClassifier, annotatedClass, parentPathName);
        } else if (action == REST.PUT) {
            addPutResource(concreteClassifier, annotatedClass, parentPathName);
        } else {
            addDeleteResource(concreteClassifier, annotatedClass, parentPathName);
        }

        //Check if transaction needs commiting
        commitOrRollback(ojTryStatement.getTryPart());

        ojTryStatement.getTryPart().addToStatements("meta", "json.append(\"], \\\"meta\\\": {\")");

        ojTryStatement.getTryPart().addToStatements("json.append(\"\\\"qualifiedName\\\": \\\"" + concreteClassifier.getQualifiedName() + "\\\"\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\", \\\"to\\\": \")");

        // Meta data remains for the root object as viewing a many list does
        // not
        // change the context
        ojTryStatement.getTryPart().addToStatements("json.append(" + TumlClassOperations.propertyEnumName(concreteClassifier) + ".asJson())");
        ojTryStatement.getTryPart().addToStatements("json.append(\", \\\"from\\\": \")");
        ojTryStatement.getTryPart().addToStatements("json.append(" + TinkerGenerationUtil.RootRuntimePropertyEnum.getLast() + ".asJson())");
        ojTryStatement.getTryPart().addToStatements("json.append(\"}}]\")");
        ojTryStatement.getTryPart().addToStatements("return new " + TumlRestletGenerationUtil.JsonRepresentation.getLast() + "(json.toString())");

        ojTryStatement.setCatchParam(new OJParameter("e", new OJPathName("java.lang.Exception")));
        ojTryStatement.getCatchPart().addToStatements("GraphDb.getDb().rollback()");
        ojTryStatement.getCatchPart().addToStatements("throw " + TumlRestletGenerationUtil.TumlExceptionUtilFactory.getLast() + ".getTumlExceptionUtil().handle(e)");
        annotatedClass.addToImports(TumlRestletGenerationUtil.TumlExceptionUtilFactory);
        post.getBody().addToStatements(ojTryStatement);

        annotatedClass.addToImports(parentPathName);


        annotatedClass.addToImports(TinkerGenerationUtil.graphDbPathName);
        annotatedClass.addToImports(TumlRestletGenerationUtil.JsonRepresentation);
        annotatedClass.addToOperations(post);
    }

    private void addPutDeleteObjectRepresentation(Classifier classifier, OJAnnotatedInterface annotatedInf, OJAnnotatedClass annotatedClass, boolean put) {
        OJAnnotatedOperation putOrDeleteInf = new OJAnnotatedOperation(put ? "put" : "delete", TumlRestletGenerationUtil.Representation);
        putOrDeleteInf.addToParameters(new OJParameter("entity", TumlRestletGenerationUtil.Representation));
        annotatedInf.addToOperations(putOrDeleteInf);
        putOrDeleteInf.addAnnotationIfNew(new OJAnnotationValue(put ? TumlRestletGenerationUtil.Put : TumlRestletGenerationUtil.Delete, "json"));

        OJAnnotatedOperation putOrDelete = new OJAnnotatedOperation(put ? "put" : "delete", TumlRestletGenerationUtil.Representation);
        putOrDelete.addToParameters(new OJParameter("entity", TumlRestletGenerationUtil.Representation));
        putOrDelete.addToThrows(TumlRestletGenerationUtil.ResourceException);
        annotatedClass.addToImports(TumlRestletGenerationUtil.ResourceException);
        TinkerGenerationUtil.addOverrideAnnotation(putOrDelete);
        TinkerGenerationUtil.addSuppressWarning(putOrDelete);

        OJTryStatement ojTryStatement = new OJTryStatement();
        OJField mapper = new OJField("mapper", TinkerGenerationUtil.ObjectMapper);
        mapper.setInitExp("new ObjectMapper()");
        ojTryStatement.getTryPart().addToLocals(mapper);

        OJAnnotatedField entityText = new OJAnnotatedField("entityText", "String");
        entityText.setInitExp("entity.getText()");
        ojTryStatement.getTryPart().addToLocals(entityText);

        OJField jsonData = new OJField("json", "java.lang.StringBuilder");
        jsonData.setInitExp("new StringBuilder()");
        ojTryStatement.getTryPart().addToLocals(jsonData);
        ojTryStatement.getTryPart().addToStatements("json.append(\"[\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\"{\\\"data\\\": [\")");

        OJAnnotatedField objectO = new OJAnnotatedField("o", new OJPathName("Object"));
        objectO.setInitExp("mapper.readValue(" + entityText.getName() + ", Object.class)");
        ojTryStatement.getTryPart().addToLocals(objectO);
        OJIfStatement ifArray = new OJIfStatement("o instanceof ArrayList");
        OJPathName genericsForArray = new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object");
        OJField array = new OJField("array", new OJPathName("java.util.ArrayList").addToGenerics(genericsForArray));
        array.setInitExp("(ArrayList<Map<String, Object>>)o");
        ifArray.getThenPart().addToLocals(array);
        ojTryStatement.getTryPart().addToStatements(ifArray);
        OJForStatement forArray = new OJForStatement("map", new OJPathName("java.util.Map").addToGenerics(new OJPathName("String")).addToGenerics(
                new OJPathName("Object")), "array");
        ifArray.addToThenPart(forArray);
        forArray.getBody().addToStatements(put ? "json.append(put(map))" : "delete(map)");

        OJField map = new OJField("map", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object"));
        map.setInitExp("(Map<String, Object>) o");
        ifArray.setElsePart(new OJBlock());
        ifArray.getElsePart().addToLocals(map);
        ifArray.getElsePart().addToStatements(put ? "json.append(put(map))" : "delete(map)");

        OJPathName parentPathName = TumlClassOperations.getPathName(classifier);

        if (put) {
            addPutResource(classifier, annotatedClass, parentPathName);
        } else {
            addDeleteResource(classifier, annotatedClass, parentPathName);
        }

        ojTryStatement.getTryPart().addToStatements("GraphDb.getDb().commit()");

        ojTryStatement.getTryPart().addToStatements("meta", "json.append(\"], \\\"meta\\\": {\")");

        ojTryStatement.getTryPart().addToStatements("json.append(\"\\\"qualifiedName\\\": \\\"" + classifier.getQualifiedName() + "\\\"\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\", \\\"to\\\": \")");

        // Meta data remains for the root object as viewing a many list does
        // not
        // change the context
        ojTryStatement.getTryPart().addToStatements("json.append(" + TumlClassOperations.propertyEnumName(classifier) + ".asJson())");
        ojTryStatement.getTryPart().addToStatements("json.append(\", \\\"from\\\": \")");
        ojTryStatement.getTryPart().addToStatements("json.append(" + TinkerGenerationUtil.RootRuntimePropertyEnum.getLast() + ".asJson())");
        ojTryStatement.getTryPart().addToStatements("json.append(\"}}]\")");
        ojTryStatement.getTryPart().addToStatements("return new " + TumlRestletGenerationUtil.JsonRepresentation.getLast() + "(json.toString())");


        ojTryStatement.setCatchParam(new OJParameter("e", new OJPathName("java.lang.Exception")));
        ojTryStatement.getCatchPart().addToStatements("GraphDb.getDb().rollback()");
        ojTryStatement.getCatchPart().addToStatements("throw new RuntimeException(e)");
        putOrDelete.getBody().addToStatements(ojTryStatement);

        annotatedClass.addToImports(parentPathName);

        annotatedClass.addToImports(TinkerGenerationUtil.graphDbPathName);
        annotatedClass.addToImports(TumlRestletGenerationUtil.JsonRepresentation);
        annotatedClass.addToOperations(putOrDelete);
    }

    private void buildToJson(Class clazz, OJAnnotatedClass annotatedClass, OJBlock block) {
        block.addToStatements("StringBuilder json = new StringBuilder()");
        block.addToStatements("json.append(\"{\\\"data\\\": [\")");
        block.addToStatements("json.append(ToJsonUtil.toJsonWithoutCompositeParent(Root.INSTANCE.get" + TumlClassOperations.className(clazz) + "()))");
        annotatedClass.addToImports(TinkerGenerationUtil.ToJsonUtil);
        block.addToStatements("json.append(\"],\")");
        block.addToStatements("json.append(\" \\\"meta\\\" : [\")");
        block.addToStatements("json.append(" + TinkerGenerationUtil.RootRuntimePropertyEnum.getLast() + ".asJson())");
        annotatedClass.addToImports(TinkerGenerationUtil.RootRuntimePropertyEnum);
        block.addToStatements("json.append(\", \")");
        block.addToStatements("json.append(" + TumlClassOperations.propertyEnumName(clazz) + ".asJson())");
        annotatedClass.addToImports(TumlClassOperations.getPathName(clazz).append(TumlClassOperations.propertyEnumName(clazz)));
        block.addToStatements("json.append(\"]}\")");
        block.addToStatements("return new " + TumlRestletGenerationUtil.JsonRepresentation.getLast() + "(json.toString())");
    }

    private void addGetRootObjectRepresentation(Class clazz, OJAnnotatedInterface annotatedInf, OJAnnotatedClass annotatedClass) {
        OJAnnotatedOperation getInf = new OJAnnotatedOperation("get", TumlRestletGenerationUtil.Representation);
        annotatedInf.addToOperations(getInf);
        getInf.addAnnotationIfNew(new OJAnnotationValue(TumlRestletGenerationUtil.Get, "json"));

        OJAnnotatedOperation get = new OJAnnotatedOperation("get", TumlRestletGenerationUtil.Representation);
        get.addToThrows(TumlRestletGenerationUtil.ResourceException);
        annotatedClass.addToImports(TumlRestletGenerationUtil.ResourceException);
        TinkerGenerationUtil.addOverrideAnnotation(get);

        OJTryStatement tryStatement = new OJTryStatement();

        OJField json = new OJField("json", new OJPathName("java.lang.StringBuilder"));
        json.setInitExp("new StringBuilder()");
        tryStatement.getTryPart().addToLocals(json);
        OJField resource = new OJField("resource", new OJPathName("java.util.List").addToGenerics(TumlClassOperations.getPathName(clazz)));
        resource.setInitExp("Root.INSTANCE.get" + TumlClassOperations.className(clazz) + "()");
        tryStatement.getTryPart().addToLocals(resource);
        annotatedClass.addToImports("org.tuml.root.Root");

        tryStatement.getTryPart().addToStatements("json.append(\"[\")");
        Set<Classifier> concreteImplementations = TumlClassOperations.getConcreteImplementations(clazz);
        int count = 1;
        for (Classifier classifier : concreteImplementations) {

            tryStatement.getTryPart().addToStatements("json.append(\"{\\\"data\\\": [\")");

            if (concreteImplementations.size() > 1) {
                tryStatement.getTryPart().addToStatements(
                        "json.append(ToJsonUtil.toJson(resource.select(new "
                                + TinkerGenerationUtil.BooleanExpressionEvaluator.getCopy().addToGenerics(TumlClassOperations.getPathName(clazz)).getLast()
                                + "() {\n			@Override\n			public Boolean evaluate(" + TumlClassOperations.getPathName(clazz).getLast()
                                + " e) {\n				return e instanceof " + TumlClassOperations.getPathName(classifier).getLast() + ";\n			}\n		})))");
                annotatedClass.addToImports(TinkerGenerationUtil.BooleanExpressionEvaluator);
                annotatedClass.addToImports(TumlClassOperations.getPathName(clazz));
                annotatedClass.addToImports(TumlClassOperations.getPathName(classifier));
            } else {
                tryStatement.getTryPart().addToStatements("json.append(ToJsonUtil.toJson(resource))");
            }

            annotatedClass.addToImports(TinkerGenerationUtil.ToJsonUtil);

            tryStatement.getTryPart().addToStatements("meta", "json.append(\"], \\\"meta\\\": {\")");

            tryStatement.getTryPart().addToStatements("json.append(\"\\\"qualifiedName\\\": \\\"" + clazz.getQualifiedName() + "\\\"\")");
            tryStatement.getTryPart().addToStatements("json.append(\", \\\"to\\\": \")");

            // Meta data remains for the root object as viewing a many list does
            // not
            // change the context
            tryStatement.getTryPart().addToStatements("json.append(" + TumlClassOperations.propertyEnumName(clazz) + ".asJson())");
            tryStatement.getTryPart().addToStatements("json.append(\", \\\"from\\\": \")");
            tryStatement.getTryPart().addToStatements("json.append(" + TinkerGenerationUtil.RootRuntimePropertyEnum.getLast() + ".asJson())");
            annotatedClass.addToImports(TinkerGenerationUtil.RootRuntimePropertyEnum);
            annotatedClass.addToImports(TumlClassOperations.getPathName(clazz).append(TumlClassOperations.propertyEnumName(clazz)));
            if (concreteImplementations.size() != 1 && count++ != concreteImplementations.size()) {
                tryStatement.getTryPart().addToStatements("json.append(\",\")");
            }
        }
        tryStatement.getTryPart().addToStatements("json.append(\"}}]\")");
        tryStatement.getTryPart().addToStatements("return new " + TumlRestletGenerationUtil.JsonRepresentation.getLast() + "(json.toString())");

        get.getBody().addToStatements(tryStatement);
        tryStatement.setCatchPart(null);
        tryStatement.getFinallyPart().addToStatements(TinkerGenerationUtil.graphDbAccess + ".rollback()");

        annotatedClass.addToImports(TumlRestletGenerationUtil.JsonRepresentation);
        annotatedClass.addToOperations(get);
    }

    private void addToRouterEnum(Class clazz, OJAnnotatedClass annotatedClass) {
        OJEnum routerEnum = (OJEnum) this.workspace.findOJClass("restlet.RestletRouterEnum");
        OJEnumLiteral ojLiteral = new OJEnumLiteral(TumlClassOperations.className(clazz).toUpperCase() + "S");

        OJField uri = new OJField();
        uri.setType(new OJPathName("String"));
        uri.setInitExp("\"/" + TumlClassOperations.className(clazz).toLowerCase() + "s\"");
        ojLiteral.addToAttributeValues(uri);

        OJField serverResourceClassField = new OJField();
        serverResourceClassField.setType(new OJPathName("java.lang.Class"));
        serverResourceClassField.setInitExp(annotatedClass.getName() + ".class");
        ojLiteral.addToAttributeValues(serverResourceClassField);
        routerEnum.addToImports(annotatedClass.getPathName());
        routerEnum.addToImports(TumlRestletGenerationUtil.ServerResource);

        routerEnum.addToLiterals(ojLiteral);

        OJAnnotatedOperation attachAll = routerEnum.findOperation("attachAll", TumlRestletGenerationUtil.Router);
        attachAll.getBody().addToStatements(routerEnum.getName() + "." + ojLiteral.getName() + ".attach(router)");
    }

    private void addPrivateGetter(org.eclipse.uml2.uml.Class clazz, OJAnnotatedInterface annotatedInf, OJAnnotatedClass annotatedClass) {
        OJField jsonResult = new OJField("jsonEntityData", "java.lang.StringBuilder");
        jsonResult.setInitExp("new StringBuilder()");
        annotatedClass.addToFields(jsonResult);
        OJAnnotatedOperation get = new OJAnnotatedOperation("internalGetJson", "String");
        get.addToThrows(TumlRestletGenerationUtil.ResourceException);
    }

    private enum REST {
        PUT("put"), POST("add"), DELETE("delete");
        private String methodName;

        private REST(String methodName) {
            this.methodName = methodName;
        }

        private String getMethodName() {
            return this.methodName;
        }
    }

}
