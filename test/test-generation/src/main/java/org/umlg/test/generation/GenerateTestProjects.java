package org.umlg.test.generation;

import org.umlg.generation.JavaGenerator;
import org.umlg.javageneration.DefaultVisitors;

import java.io.File;
import java.net.URL;
import java.util.logging.LogManager;

public class GenerateTestProjects {

    public static void main(String[] args) {
        try {
            URL url = GenerateTestProjects.class.getResource("/logging.properties");
            LogManager.getLogManager().readConfiguration(url.openStream());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (args.length == 0) {
            args = new String[]{"."};
        }
        JavaGenerator javaGenerator = new JavaGenerator();
        javaGenerator.generate(new File(args[0] + "/test/blueprints/umlg-test-blueprints/src/main/model/umlg-test.uml"), new File(args[0] + "/test/blueprints/umlg-test-blueprints/"), DefaultVisitors.getDefaultJavaVisitors());
//        javaGenerator.generate(new File(args[0] + "/test/blueprints/umlg-test-blueprints/src/main/magicdraw-model/export/umlg-test.uml"), new File(args[0] + "/test/blueprints/umlg-test-blueprints/"), DefaultVisitors.getDefaultJavaVisitors());

//        javaGenerator = new JavaGenerator();
//        javaGenerator.generate(new File(args[0] + "/test/test-restlet/src/main/model/restAndJson.uml"), new File(args[0] + "/test/test-restlet/"), RestletVisitors.getDefaultJavaVisitors());
//        javaGenerator = new JavaGenerator();
//        javaGenerator.generate(new File(args[0] + "/test/umlg-test-basic/src/main/model/umlg-test-basic.uml"), new File(args[0] + "/test/umlg-test-basic/"), DefaultVisitors.getDefaultJavaVisitors());
//        javaGenerator = new JavaGenerator();
//        javaGenerator.generate(new File(args[0] + "/test/umlg-test-ocl/src/main/model/test-ocl.uml"), new File(args[0] + "/test/umlg-test-ocl/"), DefaultVisitors.getDefaultJavaVisitors());
//        javaGenerator = new JavaGenerator();
//        javaGenerator.generate(new File(args[0] + "/test/umlg-test-tinkergraph/src/main/model/tinkergraph.uml"), new File(args[0] + "/test/umlg-test-tinkergraph/"), DefaultVisitors.getDefaultJavaVisitors());
//        javaGenerator = new JavaGenerator();
//        javaGenerator.generate(new File(args[0] + "/test/test-model-import/src/main/model/model1.uml"),
//                new File(args[0] + "/test/test-model-import/"), DefaultVisitors.getDefaultJavaVisitors());
    }
}
