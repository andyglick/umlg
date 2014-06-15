package org.umlg.javageneration.visitor.model;

import org.eclipse.uml2.uml.*;
import org.umlg.framework.ModelLoader;
import org.umlg.framework.Visitor;
import org.umlg.generation.Workspace;
import org.umlg.java.metamodel.OJPackage;
import org.umlg.java.metamodel.annotation.OJAnnotatedClass;
import org.umlg.java.metamodel.annotation.OJAnnotatedOperation;
import org.umlg.javageneration.util.PropertyWrapper;
import org.umlg.javageneration.util.UmlgClassOperations;
import org.umlg.javageneration.util.UmlgGenerationUtil;
import org.umlg.javageneration.util.UmlgPropertyOperations;
import org.umlg.javageneration.visitor.BaseVisitor;

import java.util.List;

/**
 * Date: 2013/03/24
 * Time: 9:21 AM
 */
public class IndexCreator extends BaseVisitor implements Visitor<Model> {

    public IndexCreator(Workspace workspace) {
        super(workspace);
    }

    @Override
    public void visitBefore(Model element) {
        OJAnnotatedClass indexCreator = new OJAnnotatedClass("IndexCreator");
        indexCreator.setComment("This class is responsible to create all keyed indexes.\n * It is invoked via reflection the first time a graph is created.");
        OJPackage ojPackage = new OJPackage(UmlgGenerationUtil.UmlgRootPackage.toJavaString());
        indexCreator.setMyPackage(ojPackage);
        indexCreator.addToImplementedInterfaces(UmlgGenerationUtil.UmlgIndexManager);
        addToSource(indexCreator);

        OJAnnotatedOperation createIndexes = new OJAnnotatedOperation("createIndexes");
        indexCreator.addToOperations(createIndexes);

        List<Property> qualifiers = ModelLoader.INSTANCE.getAllQualifiers();
        for (Property q : qualifiers) {
            PropertyWrapper qualifierWrap = new PropertyWrapper(q);
            createIndexes.getBody().addToStatements(UmlgGenerationUtil.UMLGAccess + ".createKeyIndex(" +
                    "\"" + qualifierWrap.getPersistentName() + "\", " +
                    UmlgGenerationUtil.edgePathName.getLast() + ".class, " +
                    "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("Class<?>").getLast() + "(\"unusedIndexValueType\", String.class), " +
                    "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("Boolean").getLast() + "(\"unusedUniqueorNot\", false), " +
                    "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("String").getLast() + "(\"unusedLabel\", \"" + qualifierWrap.javaBaseTypePath().getLast() + "\"))"
            );
            indexCreator.addToImports(UmlgGenerationUtil.edgePathName);
        }
        indexCreator.addToImports(UmlgGenerationUtil.Parameter);

        //Create index for the application root
        createIndexes.getBody().addToStatements(UmlgGenerationUtil.UMLGAccess +
                ".createKeyIndex(" +
                UmlgGenerationUtil.UmlgGraph.getLast() +
                ".ROOT_VERTEX, " +
                UmlgGenerationUtil.vertexPathName.getLast() + ".class, " +
                "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("Class<?>").getLast() + "(\"unusedIndexValueType\", String.class), " +
                "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("Boolean").getLast() + "(\"unusedUniqueorNot\", true), " +
                "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("String").getLast() + "(\"unusedLabel\", \"ROOT_VERTEX\"))"
        );

        Stereotype stereotype = ModelLoader.INSTANCE.findStereotype("Index");
        List<Property> indexedProperties = ModelLoader.INSTANCE.getAllIndexedFields();
        for (Property indexedProperty : indexedProperties) {
            PropertyWrapper indexedPWrap = new PropertyWrapper(indexedProperty);
            String type;
            if (indexedProperty.getType() instanceof DataType) {
                type = UmlgPropertyOperations.umlPrimitiveTypeToJava(indexedProperty.getType());
            } else {
                throw new RuntimeException("Only DataType may be indexed currently!");
            }
            String unique;
            EnumerationLiteral enumerationLiteral = (EnumerationLiteral) indexedProperty.getValue(stereotype, "type");
            if (enumerationLiteral.getName().equals("UNIQUE")) {
                unique = "true";
            } else {
                unique = "false";
            }
            createIndexes.getBody().addToStatements(
                    UmlgGenerationUtil.UMLGAccess + ".createKeyIndex(" +
                            "\"" + new PropertyWrapper(indexedProperty).getPersistentName() + "\", " +
                            UmlgGenerationUtil.vertexPathName.getLast() + ".class, " +
                            "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("Class<?>").getLast() + "(\"unusedIndexValueType\", " + type + ".class), " +
                            "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("Boolean").getLast() + "(\"unusedUniqueorNot\", " + unique + "), " +
                            "new " + UmlgGenerationUtil.Parameter.getCopy().addToGenerics("String").addToGenerics("String").getLast() + "(\"unusedLabel\", \"" + indexedPWrap.javaBaseTypePath().getLast() + "\"))"

            );
            indexCreator.addToImports(UmlgGenerationUtil.vertexPathName);
            indexCreator.addToImports(indexedPWrap.javaBaseTypePath());


        }

        indexCreator.addToImports(UmlgGenerationUtil.UmlgGraph);
        indexCreator.addToImports(UmlgGenerationUtil.vertexPathName);
        indexCreator.addToImports(UmlgGenerationUtil.UMLGPathName);
    }

    @Override
    public void visitAfter(Model element) {
    }
}
