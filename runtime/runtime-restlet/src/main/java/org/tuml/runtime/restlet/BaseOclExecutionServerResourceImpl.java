package org.tuml.runtime.restlet;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.tuml.ocl.TumlOclExecutor;
import org.tuml.runtime.adaptor.GraphDb;
import org.tuml.runtime.adaptor.GremlinExecutor;
import org.tuml.runtime.domain.PersistentObject;
import org.tuml.runtime.domain.TumlNode;

import java.util.Collection;
import java.util.Map;

public abstract class BaseOclExecutionServerResourceImpl extends ServerResource {

    public BaseOclExecutionServerResourceImpl() {
    }

    protected Representation execute(String query, Long contextId, String type) {
        if (type.equalsIgnoreCase("ocl")) {
            TumlNode context = GraphDb.getDb().instantiateClassifier(contextId);
            Object result = TumlOclExecutor.executeOclQuery(context.getQualifiedName(), context, query);
            if (result instanceof Map) {
//            return TumlOclExecutor.tupleMapToJson((Map<String, Object>) result);
                //TODO
                return new JsonRepresentation(result.toString());
            } else if (result instanceof Collection) {

                //TODO need to sort out polymorphic queries
                Collection<PersistentObject> poCollection = (Collection<PersistentObject>) result;

                StringBuilder json = new StringBuilder();
                json.append("[");
                json.append("{\"data\": [");
                int count = 0;
                PersistentObject poForMetaData = null;
                for (PersistentObject po : poCollection) {
                    count++;
                    String objectAsJson = po.toJsonWithoutCompositeParent();
                    String objectAsJsonWithRow = "{\"row\": " + count + ", " + objectAsJson.substring(1);
                    json.append(objectAsJsonWithRow);
                    if (count != poCollection.size()) {
                        json.append(",");
                    } else {
                        poForMetaData = po;
                    }
                }
                json.append("],");
                json.append(" \"meta\" : {");
                //TODO some hardcoding to sort out
                json.append("\"qualifiedName\": \"restAndJson::org::tuml::test::Hand::finger\"");
                json.append(", \"to\": ");
                if (poForMetaData != null) {
                    json.append(poForMetaData.getMetaDataAsJson());
                } else {
                    json.append("null");
                }
                json.append("}");
                json.append("}]");
                return new JsonRepresentation(json.toString());
            } else if (result instanceof PersistentObject) {
                PersistentObject po = (PersistentObject) result;
                return getRepresentation(po);
            } else {
                return new JsonRepresentation("{\"result\": " + "\"" + result.toString() + "\"}");
            }
        } else if (type.equalsIgnoreCase("gremlin")) {
            String result =  GremlinExecutor.executeGremlinQuery(contextId, query);
            return new JsonRepresentation("{\"result\": " + "\"" + result + "\"}");
        } else {
            throw new RuntimeException("Unknown query type " + type);
        }
    }

    //static
    protected Representation execute(String ocl) {
        //TODO This will only wotk for allInstances
        int startOfAllInstances = ocl.indexOf(".");
        String context = ocl.substring(0, startOfAllInstances);

        Object result = TumlOclExecutor.executeOclQuery(context, ocl);
        if (result instanceof Map) {
//            return TumlOclExecutor.tupleMapToJson((Map<String, Object>) result);
            //TODO
            return new JsonRepresentation(result.toString());
        } else if (result instanceof Collection) {
            Collection<PersistentObject> poCollection = (Collection<PersistentObject>) result;

            StringBuilder json = new StringBuilder();
            json.append("[");
            json.append("{\"data\": [");
            int count = 0;
            PersistentObject poForMetaData = null;
            for (PersistentObject po : poCollection) {
                count++;
                String objectAsJson = po.toJsonWithoutCompositeParent();
                String objectAsJsonWithRow = "{\"row\": " + count + ", " + objectAsJson.substring(1);
                json.append(objectAsJsonWithRow);
                if (count != poCollection.size()) {
                    json.append(",");
                } else {
                    poForMetaData = po;
                }
            }
            json.append("],");
            json.append(" \"meta\" : {");
            json.append("\"qualifiedName\": \"restAndJson::org::tuml::test::Hand::finger\"");
            json.append(", \"to\": ");
            if (poForMetaData != null) {
                json.append(poForMetaData.getMetaDataAsJson());
            } else {
                json.append("null");
            }
            json.append("}");
            json.append("}]");
            return new JsonRepresentation(json.toString());
        } else if (result instanceof PersistentObject) {
            PersistentObject po = (PersistentObject) result;
            return getRepresentation(po);
        } else {
            return new JsonRepresentation(result.toString());
        }

    }

    protected Representation getRepresentation(PersistentObject po) throws ResourceException {
        StringBuilder json = new StringBuilder();
        json.append("[{\"data\": [");
        String poAsJson = po.toJson();
        json.append(poAsJson.substring(0, poAsJson.length() - 1) + ", \"row\": 1}");
        json.append("], \"meta\" : {");
        json.append("\"qualifiedName\": \"restAndJson::org::tuml::test::Human\"");
        json.append(", \"to\": ");
        json.append(po.getMetaDataAsJson());
        json.append("}}]");
        return new JsonRepresentation(json.toString());
    }


}