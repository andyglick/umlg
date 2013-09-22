package org.umlg.runtime.adaptor;

import com.lambdazen.bitsy.BitsyGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.io.FileUtils;
import org.umlg.runtime.domain.UmlgNode;
import org.umlg.runtime.util.UmlgProperties;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Date: 2013/01/09
 * Time: 8:09 PM
 */
public class UmlgBitsyGraph extends BitsyGraph implements UmlgGraph {

    private UmlgTransactionEventHandler transactionEventHandler;
    private static final Logger logger = Logger.getLogger(UmlgBitsyGraph.class.getPackage().getName());

    public UmlgBitsyGraph(Path directory) {
        super(directory);
        this.transactionEventHandler = new UmlgTransactionEventHandlerImpl();
    }

    /** Generic for all graphs start */
    @Override
    public void incrementTransactionCount() {
        this.getRoot().setProperty("transactionCount", (Integer) this.getRoot().getProperty("transactionCount") + 1);
    }

    @Override
    public long getTransactionCount() {
        return this.getRoot().getProperty("transactionCount");
    }

    @Override
    public void addRoot() {
        Vertex root = addVertex(ROOT_VERTEX);
        root.setProperty(ROOT_VERTEX, ROOT_VERTEX);
        root.setProperty("transactionCount", 1);
        root.setProperty("className", ROOT_CLASS_NAME);
    }

    @Override
    public void commit() {
        try {
            //This null check is here for when a graph is created. It calls commit before the listener has been set.
            if (this.transactionEventHandler != null) {
                this.transactionEventHandler.beforeCommit();
            }
            super.commit();
        } finally {
            TransactionThreadEntityVar.remove();
            TransactionThreadMetaNodeVar.remove();
        }
    }

    @Override
    public void rollback() {
        try {
            super.rollback();
        } finally {
            TransactionThreadEntityVar.remove();
            TransactionThreadMetaNodeVar.remove();
        }
    }

    @Override
    public Vertex addVertex(String className) {
        Vertex v = super.addVertex(null);
        if (className != null) {
            v.setProperty("className", className);
        }
        return v;
    }

    @Override
    public void addDeletionNode() {
        Vertex v = addVertex(DELETION_VERTEX);
        addEdge(null, getRoot(), v, DELETED_VERTEX_EDGE);
    }

    private Vertex getDeletionVertex() {
        if (getRoot() != null && getRoot().getEdges(Direction.OUT, DELETED_VERTEX_EDGE).iterator().hasNext()) {
            return getRoot().getEdges(Direction.OUT, DELETED_VERTEX_EDGE).iterator().next().getVertex(Direction.IN);
        } else {
            return null;
        }
    }

    @Override
    public <T> T instantiateClassifier(Object id) {
        try {
            Vertex v = this.getVertex(id);
            if (v == null) {
                throw new RuntimeException(String.format("No vertex found for id %d", new Object[]{id}));
            }
            // TODO reimplement schemaHelper
            String className = v.getProperty("className");
            Class<?> c = Class.forName(className);
            return (T) c.getConstructor(Vertex.class).newInstance(v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearThreadVars() {
//        if (TransactionThreadEntityVar.get()!=null && TransactionThreadEntityVar.get().size()>0) {
//            throw new RuntimeException("wtf");
//        }
//        if (TransactionThreadMetaNodeVar.get()!=null && TransactionThreadMetaNodeVar.get().size()>0) {
//            throw new RuntimeException("wtf");
//        }
        TransactionThreadEntityVar.remove();
        TransactionThreadMetaNodeVar.remove();
        UmlgAssociationClassManager.remove();
    }

    /** Generic for all graphs end */

    @Override
    public String executeQuery(UmlgQueryEnum umlgQueryEnum, Object contextId, String query) {

        switch (umlgQueryEnum) {
            case OCL:
                try {
                    Class<?> tumlOclExecutor = Class.forName("org.umlg.ocl.TumlOclExecutor");
                    Method method = tumlOclExecutor.getMethod("executeOclQueryToJson", String.class, UmlgNode.class, String.class);
                    UmlgNode context = (UmlgNode) GraphDb.getDb().instantiateClassifier(contextId);
                    String json = (String) method.invoke(null, context.getQualifiedName(), context, query);
                    return json;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("TumlOclExecutor is not on the class path.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case GREMLIN:
                String result;
                if (contextId != null) {
                    result = GremlinExecutor.executeGremlinViaGroovy(contextId, query);
                } else {
                    result = GremlinExecutor.executeGremlinViaGroovy(null, query);
                }
                return result;
            case NATIVE:
                throw new IllegalStateException("Bitsy does not have a native query language!");
        }

        throw new RuntimeException("Unknown query enum");

    }

    @Override
    public void drop() {
        this.shutdown();
        //Delete the files
        String dbUrl = UmlgProperties.INSTANCE.getTumlDbLocation();
        String parsedUrl = dbUrl;
        if (dbUrl.startsWith("local:")) {
            parsedUrl = dbUrl.replace("local:", "");
        }
        File dir = new File(parsedUrl);
        if (dir.exists()) {
            try {
                logger.info(String.format("Deleting dir %s", new Object[]{dir.getAbsolutePath()}));
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Vertex getRoot() {
        return this.query().has(ROOT_VERTEX, ROOT_VERTEX).vertices().iterator().next();
    }

    @Override
    public void removeVertex(final Vertex vertex) {
        this.getTx();
        Iterable<Edge> edges = vertex.getEdges(Direction.BOTH);
        for (final Edge edge : edges) {
            edge.remove();
        }
        if (!vertex.getId().equals(new Long(0))) {
            getDeletionVertex().addEdge(DELETION_VERTEX, vertex);
            vertex.setProperty("deleted", true);
        } else {
            super.removeVertex(vertex);
        }
    }

    @Override
    public Set<Edge> getEdgesBetween(Vertex v1, Vertex v2, String... labels) {
        Set<Edge> result = new HashSet<Edge>();
        Iterable<Edge> edges = v1.getEdges(Direction.BOTH, labels);
        for (Edge edge : edges) {
            if (edge.getVertex(Direction.IN).equals(v2) || edge.getVertex(Direction.OUT).equals(v2)) {
                result.add(edge);
            }
        }
        return result;
    }

    @Override
    public long countVertices() {
        int countDeletedNodes = 0;
        if (getDeletionVertex() != null) {
            for (Edge edge : getDeletionVertex().getEdges(com.tinkerpop.blueprints.Direction.OUT, DELETION_VERTEX)) {
                countDeletedNodes++;
            }
        }
        int count = 0;
        for (Vertex v : getVertices()) {
            count++;
        }
        return count - 2 - countDeletedNodes;
    }

    @Override
    public long countEdges() {
        int countDeletedNodes = 0;
        for (Edge edge : getDeletionVertex().getEdges(com.tinkerpop.blueprints.Direction.OUT, DELETION_VERTEX)) {
            countDeletedNodes++;
        }
        int count = 0;
        for (Edge v : getEdges()) {
            count++;
        }
        return count - 1 - countDeletedNodes;
    }

    @Override
    public boolean hasEdgeBeenDeleted(Edge edge) {
        //This method is only a problem for neo4j indexes
        return false;
    }

    @Override
    public void clearTxThreadVar() {
//        if (tx.get() != null) {
//            logger.warning("Transaction threadvar was not empty!!!!! Bug somewhere in clearing the transaction!!!");
//            rollback();
//            throw new IllegalStateException("Transaction thread var is not empty!!!");
//        }
    }

}