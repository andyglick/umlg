package org.umlg.restlet.visitor.clazz;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.uml2.uml.*;
import org.umlg.java.metamodel.OJBlock;
import org.umlg.java.metamodel.OJField;
import org.umlg.java.metamodel.OJForStatement;
import org.umlg.java.metamodel.OJIfStatement;
import org.umlg.java.metamodel.OJPackage;
import org.umlg.java.metamodel.OJParameter;
import org.umlg.java.metamodel.OJPathName;
import org.umlg.java.metamodel.OJTryStatement;
import org.umlg.java.metamodel.OJVisibilityKind;
import org.umlg.java.metamodel.annotation.OJAnnotatedClass;
import org.umlg.java.metamodel.annotation.OJAnnotatedField;
import org.umlg.java.metamodel.annotation.OJAnnotatedInterface;
import org.umlg.java.metamodel.annotation.OJAnnotatedOperation;
import org.umlg.java.metamodel.annotation.OJAnnotationValue;
import org.umlg.java.metamodel.annotation.OJEnum;
import org.umlg.java.metamodel.annotation.OJEnumLiteral;
import org.umlg.framework.Visitor;
import org.umlg.generation.Workspace;
import org.umlg.javageneration.util.PropertyWrapper;
import org.umlg.javageneration.util.TinkerGenerationUtil;
import org.umlg.javageneration.util.TumlClassOperations;
import org.umlg.restlet.generation.RestletVisitors;
import org.umlg.restlet.util.TumlRestletGenerationUtil;
import org.umlg.restlet.visitor.model.QueryExecuteResourceBuilder;

@Deprecated
public class NavigatePropertyServerResourceBuilder extends BaseServerResourceBuilder implements Visitor<Property> {

    public NavigatePropertyServerResourceBuilder(Workspace workspace, String sourceDir) {
        super(workspace, sourceDir);
    }

    @Override
    public void visitBefore(Property p) {
        PropertyWrapper pWrap = new PropertyWrapper(p);
        if (!pWrap.isDataType() && !pWrap.isEnumeration() && pWrap.isNavigable()) {

            Set<Classifier> concreteImplementations = TumlClassOperations.getConcreteImplementations((Classifier) pWrap.getType());
            for (Classifier concreteClassifierTo : concreteImplementations) {
                OJAnnotatedClass owner = findOJClass(concreteClassifierTo);


                OJAnnotatedInterface annotatedInf = new OJAnnotatedInterface(TumlClassOperations.getPathName(pWrap.getOwningType()).getLast() + "_"
                        + pWrap.getOtherEnd().getName() + "_" + pWrap.getName() + "_" + StringUtils.capitalize(concreteClassifierTo.getName()) + "_ServerResource");
                OJPackage ojPackage = new OJPackage(owner.getMyPackage().toString() + ".restlet");
                annotatedInf.setMyPackage(ojPackage);
                addToSource(annotatedInf);

                OJAnnotatedClass annotatedClass = new OJAnnotatedClass(TumlClassOperations.getPathName(pWrap.getOwningType()).getLast() + "_"
                        + pWrap.getOtherEnd().getName() + "_" + pWrap.getName() + "_" + StringUtils.capitalize(concreteClassifierTo.getName()) + "_ServerResourceImpl");
                annotatedClass.setSuperclass(TumlRestletGenerationUtil.ServerResource);
                annotatedClass.addToImplementedInterfaces(annotatedInf.getPathName());
                annotatedClass.setMyPackage(ojPackage);
                addToSource(annotatedClass);
                addDefaultConstructor(annotatedClass);

                addCompositeParentIdField(pWrap, annotatedClass);
                addGetObjectRepresentation(pWrap, annotatedInf, annotatedClass);

                // Put must be Idempotence, i.e. calling it many times must make
                // no
                // difference to server state
                // non unique sequence or a bag can not put as adding the same
                // value
                // more than once changes the state
                addPostObjectRepresentation(concreteClassifierTo, pWrap, annotatedInf, annotatedClass);
                addPutDeleteObjectRepresentation(concreteClassifierTo, pWrap, annotatedInf, annotatedClass, true);
                addPutDeleteObjectRepresentation(concreteClassifierTo, pWrap, annotatedInf, annotatedClass, false);
                addServerResourceToRouterEnum(concreteClassifierTo, pWrap, annotatedClass);
            }
        }
//        }
    }

    @Override
    public void visitAfter(Property p) {
    }

    private void addGetObjectRepresentation(PropertyWrapper pWrap, OJAnnotatedInterface annotatedInf, OJAnnotatedClass annotatedClass) {

        OJAnnotatedOperation getInf = new OJAnnotatedOperation("get", TumlRestletGenerationUtil.Representation);
        annotatedInf.addToOperations(getInf);
        getInf.addAnnotationIfNew(new OJAnnotationValue(TumlRestletGenerationUtil.Get, "json"));

        OJAnnotatedOperation get = new OJAnnotatedOperation("get", TumlRestletGenerationUtil.Representation);
        get.addToThrows(TumlRestletGenerationUtil.ResourceException);
        annotatedClass.addToImports(TumlRestletGenerationUtil.ResourceException);
        TinkerGenerationUtil.addOverrideAnnotation(get);

        OJTryStatement tryStatement = new OJTryStatement();

        OJPathName parentPathName = TumlClassOperations.getPathName(pWrap.getOtherEnd().getType());
        tryStatement.getTryPart().addToStatements(
                "this." + parentPathName.getLast().toLowerCase() + "Id = Long.valueOf((String)getRequestAttributes().get(\""
                        + parentPathName.getLast().toLowerCase() + "Id\"))");
        tryStatement.getTryPart().addToStatements(
                parentPathName.getLast() + " parentResource = GraphDb.getDb().instantiateClassifier(" + parentPathName.getLast().toLowerCase() + "Id" + ")");
        annotatedClass.addToImports(parentPathName);
        buildToJson(pWrap, annotatedClass, tryStatement.getTryPart());
        tryStatement.getFinallyPart().addToStatements(TinkerGenerationUtil.graphDbAccess + ".rollback()");
        tryStatement.setCatchPart(null);
        get.getBody().addToStatements(tryStatement);
        annotatedClass.addToImports(TinkerGenerationUtil.graphDbPathName);
        annotatedClass.addToImports(TumlRestletGenerationUtil.JsonRepresentation);
        annotatedClass.addToOperations(get);
    }

    private void addPutDeleteObjectRepresentation(Classifier concreteClassifier, PropertyWrapper pWrap, OJAnnotatedInterface annotatedInf, OJAnnotatedClass annotatedClass, boolean put) {
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


        PropertyWrapper otherEndPWrap = new PropertyWrapper(pWrap.getOtherEnd());
        OJPathName parentPathName = otherEndPWrap.javaBaseTypePath();
        putOrDelete.getBody().addToStatements(
                "this." + parentPathName.getLast().toLowerCase() + "Id = Long.valueOf((String)getRequestAttributes().get(\""
                        + parentPathName.getLast().toLowerCase() + "Id\"))");
        putOrDelete.getBody().addToStatements(
                parentPathName.getLast() + " parentResource = GraphDb.getDb().instantiateClassifier(" + parentPathName.getLast().toLowerCase() + "Id" + ")");


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
        if (put) {
            OJField count = new OJField("count", "int");
            count.setInitExp("1");
            ifArray.getThenPart().addToLocals(count);
            forArray.getBody().addToStatements("json.append(put(map))");
            OJIfStatement ifCount = new OJIfStatement("count++ != array.size()");
            ifCount.getThenPart().addToStatements("json.append(\",\")");
            forArray.getBody().addToStatements(ifCount);
        } else if (pWrap.isComposite()) {
            forArray.getBody().addToStatements("delete(map)");
        } else {
            forArray.getBody().addToStatements("delete(parentResource, map)");
        }

        OJField map = new OJField("map", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object"));
        map.setInitExp("(Map<String, Object>) o");
        ifArray.setElsePart(new OJBlock());
        ifArray.getElsePart().addToLocals(map);
        if (put) {
            ifArray.getElsePart().addToStatements("json.append(put(map))");
        } else if (pWrap.isComposite()) {
            ifArray.getElsePart().addToStatements("delete(map)");
        } else {
            ifArray.getElsePart().addToStatements("delete(parentResource, map)");
        }

        if (put) {
            addPutResource(pWrap, annotatedClass, parentPathName);
        } else {
            addDeleteResource(pWrap, annotatedClass, parentPathName);
        }

        ojTryStatement.getTryPart().addToStatements("json.append(\"],\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\" \\\"meta\\\" : {\")");
        // The execute ocl query resource is only required if the below
        // visitor is available
        if (RestletVisitors.containsVisitorForClass(QueryExecuteResourceBuilder.class)
                && (pWrap.getType().getQualifiedName().equals(TumlRestletGenerationUtil.instanceQueryQualifiedName) || pWrap.getType().getQualifiedName().equals(TumlRestletGenerationUtil.classQueryQualifiedName))) {
            ojTryStatement.getTryPart().addToStatements("json.append(\"\\\"oclExecuteUri\\\": \\\"/" + pWrap.getModel().getName() + "/{contextId}/oclExecuteQuery\\\", \")");
        }
        ojTryStatement.getTryPart().addToStatements("json.append(\"\\\"qualifiedName\\\": \\\"" + pWrap.getQualifiedName() + "\\\"\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\", \\\"to\\\": \")");

        OJIfStatement ifStatementFrom = new OJIfStatement();
        Set<Classifier> concreteImplementationsFrom = TumlClassOperations.getConcreteImplementations((Classifier) pWrap.getOwningType());
        int countFrom = 1;
        for (Classifier concreteClassifierFrom : concreteImplementationsFrom) {
            OJBlock conditionBlockFrom = new OJBlock();
            annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierFrom));
            String condition = "parentResource instanceof " + TumlClassOperations.getPathName(concreteClassifierFrom).getLast();
            if (countFrom == 1) {
                ifStatementFrom.setCondition(condition);
                ifStatementFrom.setThenPart(conditionBlockFrom);
            } else if (countFrom == concreteImplementationsFrom.size()) {
                ifStatementFrom.setElsePart(conditionBlockFrom);
            } else {
                conditionBlockFrom = ifStatementFrom.addToElseIfCondition(condition, "");
            }
            conditionBlockFrom.addToStatements("json.append(" + TumlClassOperations.propertyEnumName(concreteClassifier) + ".asJson())");
            conditionBlockFrom.addToStatements("json.append(\", \\\"from\\\": \")");
            conditionBlockFrom.addToStatements("json.append(" + TumlClassOperations.propertyEnumName(concreteClassifierFrom) + ".asJson())");
            annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierFrom).append(
                    TumlClassOperations.propertyEnumName(concreteClassifierFrom)));
            countFrom++;
        }
        ojTryStatement.getTryPart().addToStatements(ifStatementFrom);

        ojTryStatement.getTryPart().addToStatements("json.append(\"}\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\"}]\")");

        commitOrRollback(ojTryStatement.getTryPart());
        ojTryStatement.getTryPart().addToStatements("return new JsonRepresentation(json.toString())");

        ojTryStatement.setCatchParam(new OJParameter("e", new OJPathName("java.lang.Exception")));
        ojTryStatement.getCatchPart().addToStatements(TinkerGenerationUtil.graphDbAccess + ".rollback()");

        OJIfStatement ifRuntime = new OJIfStatement("e instanceof RuntimeException");
        ifRuntime.addToThenPart("throw (RuntimeException)e");
        ojTryStatement.getCatchPart().addToStatements(ifRuntime);
        ojTryStatement.getCatchPart().addToStatements("throw new RuntimeException(e)");

        putOrDelete.getBody().addToStatements(ojTryStatement);

        annotatedClass.addToImports(parentPathName);

        annotatedClass.addToImports(TinkerGenerationUtil.graphDbPathName);
        annotatedClass.addToImports(TumlRestletGenerationUtil.JsonRepresentation);
        annotatedClass.addToOperations(putOrDelete);
    }

    private void addPostObjectRepresentation(Classifier concreteClassifier, PropertyWrapper pWrap, OJAnnotatedInterface annotatedInf,
                                             OJAnnotatedClass annotatedClass) {
        OJAnnotatedOperation postInf = new OJAnnotatedOperation("post", TumlRestletGenerationUtil.Representation);
        postInf.addToParameters(new OJParameter("entity", TumlRestletGenerationUtil.Representation));
        annotatedInf.addToOperations(postInf);
        postInf.addAnnotationIfNew(new OJAnnotationValue(TumlRestletGenerationUtil.Post, "json"));

        OJAnnotatedOperation post = new OJAnnotatedOperation("post", TumlRestletGenerationUtil.Representation);
        post.addToParameters(new OJParameter("entity", TumlRestletGenerationUtil.Representation));
        post.addToThrows(TumlRestletGenerationUtil.ResourceException);
        annotatedClass.addToImports(TumlRestletGenerationUtil.ResourceException);
        TinkerGenerationUtil.addOverrideAnnotation(post);
        TinkerGenerationUtil.addSuppressWarning(post);

        PropertyWrapper otherEndPWrap = new PropertyWrapper(pWrap.getOtherEnd());

        OJPathName parentPathName = otherEndPWrap.javaBaseTypePath();
        post.getBody().addToStatements(
                "this." + parentPathName.getLast().toLowerCase() + "Id = Long.valueOf((String)getRequestAttributes().get(\""
                        + parentPathName.getLast().toLowerCase() + "Id\"))");
        post.getBody().addToStatements(
                parentPathName.getLast() + " parentResource = GraphDb.getDb().instantiateClassifier(" + parentPathName.getLast().toLowerCase() + "Id" + ")");

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

        OJIfStatement ifEntityTextNull = new OJIfStatement("entityText != null");
        ojTryStatement.getTryPart().addToStatements(ifEntityTextNull);
        ifEntityTextNull.addToElsePart("json.append(add(parentResource))");

        OJAnnotatedField objectO = new OJAnnotatedField("o", new OJPathName("Object"));
        objectO.setInitExp("mapper.readValue(" + entityText.getName() + ", Object.class)");
        ifEntityTextNull.getThenPart().addToLocals(objectO);
        OJIfStatement ifArray = new OJIfStatement("o instanceof ArrayList");
        OJPathName genericsForArray = new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object");
        OJField array = new OJField("array", new OJPathName("java.util.ArrayList").addToGenerics(genericsForArray));
        array.setInitExp("(ArrayList<Map<String, Object>>)o");
        ifArray.getThenPart().addToLocals(array);
        ifEntityTextNull.addToThenPart(ifArray);

        OJField count = new OJField("count", "int");
        count.setInitExp("1");
        ifArray.getThenPart().addToLocals(count);
        OJForStatement forArray = new OJForStatement("map", new OJPathName("java.util.Map").addToGenerics(new OJPathName("String")).addToGenerics(
                new OJPathName("Object")), "array");
        ifArray.addToThenPart(forArray);
        forArray.getBody().addToStatements("json.append(add(parentResource, map))");
        OJIfStatement ifCount = new OJIfStatement("count++ != array.size()");
        ifCount.getThenPart().addToStatements("json.append(\",\")");
        forArray.getBody().addToStatements(ifCount);

        OJField map = new OJField("map", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object"));
        map.setInitExp("(Map<String, Object>) o");
        ifArray.setElsePart(new OJBlock());
        ifArray.getElsePart().addToLocals(map);
        ifArray.getElsePart().addToStatements("json.append(add(parentResource, map))");

        addPostResource(concreteClassifier, pWrap, annotatedClass, parentPathName);

        ojTryStatement.getTryPart().addToStatements("json.append(\"],\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\" \\\"meta\\\" : {\")");
        // The execute ocl query resource is only required if the below
        // visitor is available
        if (RestletVisitors.containsVisitorForClass(QueryExecuteResourceBuilder.class)
                && (pWrap.getType().getQualifiedName().equals(TumlRestletGenerationUtil.instanceQueryQualifiedName) || pWrap.getType().getQualifiedName().equals(TumlRestletGenerationUtil.classQueryQualifiedName))) {
            ojTryStatement.getTryPart().addToStatements("json.append(\"\\\"oclExecuteUri\\\": \\\"/" + pWrap.getModel().getName() + "/{contextId}/oclExecuteQuery\\\", \")");
        }
        ojTryStatement.getTryPart().addToStatements("json.append(\"\\\"qualifiedName\\\": \\\"" + pWrap.getQualifiedName() + "\\\"\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\", \\\"to\\\": \")");

        OJIfStatement ifStatementFrom = new OJIfStatement();
        Set<Classifier> concreteImplementationsFrom = TumlClassOperations.getConcreteImplementations((Classifier) pWrap.getOwningType());
        int countFrom = 1;
        for (Classifier concreteClassifierFrom : concreteImplementationsFrom) {
            OJBlock conditionBlockFrom = new OJBlock();
            annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierFrom));
            String condition = "parentResource instanceof " + TumlClassOperations.getPathName(concreteClassifierFrom).getLast();
            if (countFrom == 1) {
                ifStatementFrom.setCondition(condition);
                ifStatementFrom.setThenPart(conditionBlockFrom);
            } else if (countFrom == concreteImplementationsFrom.size()) {
                ifStatementFrom.setElsePart(conditionBlockFrom);
            } else {
                conditionBlockFrom = ifStatementFrom.addToElseIfCondition(condition, "");
            }
            conditionBlockFrom.addToStatements("json.append(" + TumlClassOperations.propertyEnumName(concreteClassifier) + ".asJson())");
            conditionBlockFrom.addToStatements("json.append(\", \\\"from\\\": \")");
            conditionBlockFrom.addToStatements("json.append(" + TumlClassOperations.propertyEnumName(concreteClassifierFrom) + ".asJson())");
            annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierFrom).append(
                    TumlClassOperations.propertyEnumName(concreteClassifierFrom)));
            countFrom++;
        }

        ojTryStatement.getTryPart().addToStatements(ifStatementFrom);
        ojTryStatement.getTryPart().addToStatements("json.append(\"}\")");
        ojTryStatement.getTryPart().addToStatements("json.append(\"}]\")");

        commitOrRollback(ojTryStatement.getTryPart());

        ojTryStatement.getTryPart().addToStatements("return new JsonRepresentation(json.toString())");

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

    private void addPutResource(PropertyWrapper pWrap, OJAnnotatedClass annotatedClass, OJPathName parentPathName) {
        OJAnnotatedOperation put = new OJAnnotatedOperation("put", "String");
        put.setVisibility(OJVisibilityKind.PRIVATE);
        put.addToParameters(new OJParameter("propertyMap", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object")));
        annotatedClass.addToOperations(put);
        put.getBody().addToStatements("Long id = Long.valueOf((Integer)propertyMap.get(\"id\"))");
        put.getBody().addToStatements(pWrap.javaBaseTypePath().getLast() + " childResource = GraphDb.getDb().instantiateClassifier(id)");
        annotatedClass.addToImports(pWrap.javaBaseTypePath());
        put.getBody().addToStatements("childResource.fromJson(propertyMap)");
        put.getBody().addToStatements("return childResource.toJson()");
    }

    private void addPostResource(Classifier concreteClassifier, PropertyWrapper pWrap, OJAnnotatedClass annotatedClass, OJPathName parentPathName) {
        OJAnnotatedOperation add = new OJAnnotatedOperation("add", "String");
        add.setVisibility(OJVisibilityKind.PRIVATE);
        add.addToParameters(new OJParameter("parentResource", parentPathName));
        add.addToParameters(new OJParameter("propertyMap", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object")));
        annotatedClass.addToOperations(add);
        if (pWrap.isComposite()) {
            add.getBody().addToStatements(
                    pWrap.javaBaseTypePath().getLast() + " childResource = new " + TumlClassOperations.getPathName(concreteClassifier).getLast() + "(true)");
        } else {
            add.getBody().addToStatements("Long id = Long.valueOf((Integer)propertyMap.get(\"id\"))");
            add.getBody().addToStatements(pWrap.javaBaseTypePath().getLast() + " childResource = GraphDb.getDb().instantiateClassifier(id)");
        }
        annotatedClass.addToImports(pWrap.javaBaseTypePath());
        annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifier));
        add.getBody().addToStatements("childResource.fromJson(propertyMap)");
        if (pWrap.isOrdered()) {
            annotatedClass.addToImports(TumlRestletGenerationUtil.Parameter);
            add.getBody().addToStatements("Parameter indexParameter = getQuery().getFirst(\"index\")");
            OJIfStatement ifIndexNull = new OJIfStatement("indexParameter != null");
            ifIndexNull.addToThenPart("int index = Integer.valueOf(indexParameter.getValue())");
            ifIndexNull.addToThenPart("parentResource." + pWrap.getter() + "().add(index, childResource)");
            ifIndexNull.addToElsePart("parentResource." + pWrap.adder() + "(childResource)");
            add.getBody().addToStatements(ifIndexNull);
        } else {
            add.getBody().addToStatements("parentResource." + pWrap.adder() + "(childResource)");
        }
        add.getBody().addToStatements("return childResource.toJson()");

        //Add the add without the map, i.e. create a empty resource
        OJAnnotatedOperation emptyAdd = new OJAnnotatedOperation("add", "String");
        emptyAdd.setVisibility(OJVisibilityKind.PRIVATE);
        emptyAdd.addToParameters(new OJParameter("parentResource", parentPathName));
        emptyAdd.getBody().addToStatements(pWrap.javaBaseTypePath().getLast() + " childResource = new " + TumlClassOperations.getPathName(concreteClassifier).getLast() + "(true)");
        emptyAdd.getBody().addToStatements("parentResource." + pWrap.adder() + "(childResource)");
        emptyAdd.getBody().addToStatements("return childResource.toJson()");
        annotatedClass.addToOperations(emptyAdd);
    }

    private void buildToJson(PropertyWrapper pWrap, OJAnnotatedClass annotatedClass, OJBlock block) {

        Set<Classifier> concreteImplementations = TumlClassOperations.getConcreteImplementations((Classifier) pWrap.getType());
        Set<Classifier> concreteImplementationsFrom = TumlClassOperations.getConcreteImplementations((Classifier) pWrap.getOwningType());
        if (!concreteImplementationsFrom.isEmpty()) {
            annotatedClass.addToImports(TinkerGenerationUtil.ToJsonUtil);
            block.addToStatements("StringBuilder json = new StringBuilder()");
            block.addToStatements("json.append(\"[\")");

            int count = 1;
            // For meta data, put where one is navigating to first, then where one is
            // navigating from
            // This is consistent with navigating to a entity with a vertex where
            // there is no navigating from.
            // i.e. the first meta data in the array is the entity navigating to.
            for (Classifier concreteClassifierTo : concreteImplementations) {
                annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierTo));
                block.addToStatements("json.append(\"{\\\"data\\\": [\")");
                if (pWrap.isOne()) {
                    OJIfStatement ifOneInstanceOf = new OJIfStatement("parentResource." + pWrap.getter() + "() instanceof "
                            + TumlClassOperations.getPathName(concreteClassifierTo).getLast());
                    ifOneInstanceOf.addToThenPart("json.append(ToJsonUtil.toJsonWithoutCompositeParent(parentResource." + pWrap.getter() + "()))");
                    block.addToStatements(ifOneInstanceOf);
                } else {
                    block.addToStatements("json.append(ToJsonUtil.toJsonWithoutCompositeParent(parentResource." + pWrap.getter() + "().select(new "
                            + TinkerGenerationUtil.BooleanExpressionEvaluator.getCopy().addToGenerics(TumlClassOperations.getPathName(pWrap.getType())).getLast()
                            + "() {\n			@Override\n			public Boolean evaluate(" + TumlClassOperations.getPathName(pWrap.getType()).getLast()
                            + " e) {\n				return e instanceof " + TumlClassOperations.getPathName(concreteClassifierTo).getLast() + ";\n			}\n		})))");
                    annotatedClass.addToImports(TinkerGenerationUtil.BooleanExpressionEvaluator);
                }
                annotatedClass.addToImports(TumlClassOperations.getPathName(pWrap.getType()));
                block.addToStatements("json.append(\"],\")");

                block.addToStatements("json.append(\" \\\"meta\\\" : {\")");
                // The execute ocl query resource is only required if the below
                // visitor is available
                if (RestletVisitors.containsVisitorForClass(QueryExecuteResourceBuilder.class)
                        && (pWrap.getType().getQualifiedName().equals(TumlRestletGenerationUtil.instanceQueryQualifiedName) || pWrap.getType().getQualifiedName().equals(TumlRestletGenerationUtil.classQueryQualifiedName))) {
                    block.addToStatements("json.append(\"\\\"oclExecuteUri\\\": \\\"/" + pWrap.getModel().getName() + "/{contextId}/oclExecuteQuery\\\", \")");
                }
                block.addToStatements("json.append(\"\\\"qualifiedName\\\": \\\"" + pWrap.getQualifiedName() + "\\\"\")");
                block.addToStatements("json.append(\", \\\"to\\\": \")");
                int countFrom = 1;
                OJIfStatement ifStatementFrom = new OJIfStatement();
                for (Classifier concreteClassifierFrom : concreteImplementationsFrom) {
                    OJBlock conditionBlockFrom = new OJBlock();
                    annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierFrom));
                    String condition = "parentResource instanceof " + TumlClassOperations.getPathName(concreteClassifierFrom).getLast();
                    if (countFrom == 1) {
                        ifStatementFrom.setCondition(condition);
                        ifStatementFrom.setThenPart(conditionBlockFrom);
                    } else if (countFrom == concreteImplementationsFrom.size()) {
                        ifStatementFrom.setElsePart(conditionBlockFrom);
                    } else {
                        conditionBlockFrom = ifStatementFrom.addToElseIfCondition(condition, "");
                    }
                    conditionBlockFrom.addToStatements("json.append(" + TumlClassOperations.propertyEnumName(concreteClassifierTo) + ".asJson())");
                    conditionBlockFrom.addToStatements("json.append(\", \\\"from\\\": \")");
                    conditionBlockFrom.addToStatements("json.append(" + TumlClassOperations.propertyEnumName(concreteClassifierFrom) + ".asJson())");
                    annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierFrom).append(
                            TumlClassOperations.propertyEnumName(concreteClassifierFrom)));
                    countFrom++;
                }
                block.addToStatements(ifStatementFrom);

                annotatedClass.addToImports(TumlClassOperations.getPathName(pWrap.getOwningType()).append(
                        TumlClassOperations.propertyEnumName(pWrap.getOwningType())));
                annotatedClass.addToImports(TumlClassOperations.getPathName(concreteClassifierTo)
                        .append(TumlClassOperations.propertyEnumName(concreteClassifierTo)));
                block.addToStatements("json.append(\"}\")");
                if (concreteImplementations.size() != 1 && count != concreteImplementations.size()) {
                    block.addToStatements("json.append(\"}, \")");
                }
                count++;
            }
            block.addToStatements("json.append(\"}]\")");
            block.addToStatements("return new " + TumlRestletGenerationUtil.JsonRepresentation.getLast() + "(json.toString())");
        } else {
            //TODO not thought through
            block.addToStatements("return null");
        }
    }

    private void addServerResourceToRouterEnum(Classifier concreteClassifier, PropertyWrapper pWrap, OJAnnotatedClass annotatedClass) {
        OJEnum routerEnum = (OJEnum) this.workspace.findOJClass("restlet.RestletRouterEnum");

        OJEnumLiteral ojLiteral = new OJEnumLiteral(TumlClassOperations.getPathName(pWrap.getOwningType()).getLast().toUpperCase() + "_" + pWrap.fieldname()
                + "_" + concreteClassifier.getName());

        OJField uri = new OJField();
        uri.setType(new OJPathName("String"));
        uri.setInitExp("\"/" + TumlClassOperations.getPathName(pWrap.getOwningType()).getLast().toLowerCase() + "s/{"
                + TumlClassOperations.getPathName(pWrap.getOwningType()).getLast().toLowerCase() + "Id}/" + pWrap.fieldname() + "\"");
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

        //Add the url for post/put to the resource
        ojLiteral = new OJEnumLiteral(TumlClassOperations.getPathName(pWrap.getOwningType()).getLast().toUpperCase() + "_" + pWrap.fieldname()
                + "_" + pWrap.fieldname() + "_" + concreteClassifier.getName());

        uri = new OJField();
        uri.setType(new OJPathName("String"));
        uri.setInitExp("\"/" + TumlClassOperations.getPathName(pWrap.getOwningType()).getLast().toLowerCase() + "s/{"
                + TumlClassOperations.getPathName(pWrap.getOwningType()).getLast().toLowerCase() + "Id}/" + pWrap.fieldname() + "_" + concreteClassifier.getName() + "\"");
        ojLiteral.addToAttributeValues(uri);

        serverResourceClassField = new OJField();
        serverResourceClassField.setType(new OJPathName("java.lang.Class"));
        serverResourceClassField.setInitExp(annotatedClass.getName() + ".class");
        ojLiteral.addToAttributeValues(serverResourceClassField);
        routerEnum.addToImports(annotatedClass.getPathName());
        routerEnum.addToImports(TumlRestletGenerationUtil.ServerResource);

        routerEnum.addToLiterals(ojLiteral);

        attachAll.getBody().addToStatements(routerEnum.getName() + "." + ojLiteral.getName() + ".attach(router)");

    }

    private void addCompositeParentIdField(PropertyWrapper pWrap, OJAnnotatedClass annotatedClass) {
        OJField compositeParentFieldId = new OJField(TumlClassOperations.getPathName(pWrap.getOtherEnd().getType()).getLast().toLowerCase() + "Id",
                new OJPathName("Long"));
        compositeParentFieldId.setVisibility(OJVisibilityKind.PRIVATE);
        annotatedClass.addToFields(compositeParentFieldId);
    }

    private void addDeleteResource(PropertyWrapper pWrap, OJAnnotatedClass annotatedClass, OJPathName parentPathName) {
        OJAnnotatedOperation delete = new OJAnnotatedOperation("delete");
        delete.setVisibility(OJVisibilityKind.PRIVATE);
        annotatedClass.addToOperations(delete);
        annotatedClass.addToImports(pWrap.javaBaseTypePath());
        if (pWrap.isComposite()) {
            delete.addToParameters(new OJParameter("propertyMap", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object")));
            delete.getBody().addToStatements("Long id = Long.valueOf((Integer)propertyMap.get(\"id\"))");
            delete.getBody().addToStatements(pWrap.javaBaseTypePath().getLast() + " childResource = GraphDb.getDb().instantiateClassifier(id)");
            delete.getBody().addToStatements("childResource.delete()");
        } else {
            delete.addToParameters(new OJParameter("parentResource", parentPathName));
            delete.addToParameters(new OJParameter("propertyMap", new OJPathName("java.util.Map").addToGenerics("String").addToGenerics("Object")));
            delete.getBody().addToStatements("Long id = Long.valueOf((Integer)propertyMap.get(\"id\"))");
            delete.getBody().addToStatements(pWrap.javaBaseTypePath().getLast() + " childResource = GraphDb.getDb().instantiateClassifier(id)");
            delete.getBody().addToStatements("parentResource." + pWrap.remover() + "(childResource)");
        }
    }

}