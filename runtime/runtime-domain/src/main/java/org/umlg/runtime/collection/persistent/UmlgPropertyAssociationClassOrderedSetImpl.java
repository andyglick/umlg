package org.umlg.runtime.collection.persistent;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.umlg.runtime.adaptor.UMLG;
import org.umlg.runtime.collection.UmlgCollection;
import org.umlg.runtime.collection.UmlgRuntimeProperty;
import org.umlg.runtime.collection.UmlgPropertyAssociationClassOrderedSet;
import org.umlg.runtime.domain.AssociationClassNode;
import org.umlg.runtime.domain.UmlgNode;

import java.util.Set;

/**
 * Date: 2013/06/18
 * Time: 5:59 PM
 */
public class UmlgPropertyAssociationClassOrderedSetImpl<E, AC extends AssociationClassNode> extends UmlgOrderedSetImpl<E> implements UmlgPropertyAssociationClassOrderedSet<E, AC> {

    public UmlgPropertyAssociationClassOrderedSetImpl(UmlgNode owner, UmlgRuntimeProperty runtimeProperty, UmlgRuntimeProperty associationClassRuntimeProperty) {
        super(owner, runtimeProperty);
    }

    @Override
    public boolean add(E e, AC associationClass) {
        if (super.add(e)) {
            associationClass.internalAdder(umlgRuntimeProperty, true, this.owner);
            associationClass.internalAdder(umlgRuntimeProperty, false, (UmlgNode) e);
            this.edge.setProperty(UmlgCollection.ASSOCIATION_CLASS_VERTEX_ID, associationClass.getId());
            this.edge.setProperty("className", associationClass.getClass().getName());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void add(int index, E e, AC associationClass) {
        super.add(index, e);
        associationClass.internalAdder(umlgRuntimeProperty, true, this.owner);
        associationClass.internalAdder(umlgRuntimeProperty, false, (UmlgNode) e);
        this.edge.setProperty(UmlgCollection.ASSOCIATION_CLASS_VERTEX_ID, associationClass.getId());
        this.edge.setProperty("className", associationClass.getClass().getName());
    }

    @Override
    public boolean add(E e) {
        throw new RuntimeException("The standard add method can not be used on an edge that represents and association class. Please use the add(element, associationClass) method.");
    }

    @Override
    public void move(int index, E e, AC associationClass) {
        maybeLoad();
        Vertex v;
        if (e instanceof UmlgNode) {
            UmlgNode node = (UmlgNode) e;
            v = node.getVertex();
        } else if (e.getClass().isEnum()) {
            v = removeFromInternalMap(e);
            UMLG.get().removeVertex(v);
        } else if (isOnePrimitive() || getDataTypeEnum() != null) {
            throw new IllegalStateException("one primitive or data type can not have an association class.");
        } else {
            if (true) {
                throw new RuntimeException("wtf");
            }
            v = removeFromInternalMap(e);
            UMLG.get().removeVertex(v);
        }
        //remove the edge
        super.remove(e);

        //add a new the edge
        super.add(index, e);
        //set association class vertex id on new edge
        this.edge.setProperty(UmlgCollection.ASSOCIATION_CLASS_VERTEX_ID, associationClass.getId());
        this.edge.setProperty("className", associationClass.getClass().getName());
    }

        @Override
    public boolean remove(Object o) {
        maybeLoad();
        Vertex v;
        if (o instanceof UmlgNode) {
            UmlgNode node = (UmlgNode) o;
            v = node.getVertex();
            removeAssociationClassVertex(v);
        } else if (o.getClass().isEnum()) {
            v = removeFromInternalMap(o);
            removeAssociationClassVertex(v);
            UMLG.get().removeVertex(v);
        } else if (isOnePrimitive() || getDataTypeEnum() != null) {
            throw new IllegalStateException("one primitive or data type can not have an association class.");
        } else {
            if (true) {
                throw new RuntimeException("wtf");
            }
            v = removeFromInternalMap(o);
            removeAssociationClassVertex(v);
            UMLG.get().removeVertex(v);
        }

        return super.remove(o);
    }

    private void removeAssociationClassVertex(Vertex v) {
        Set<Edge> edges = UMLG.get().getEdgesBetween(this.vertex, v, this.getLabel());
        for (Edge edge : edges) {
            Vertex associationClassVertex = UMLG.get().getVertex(edge.getProperty(UmlgCollection.ASSOCIATION_CLASS_VERTEX_ID));
            //The remove code will delete all in and out edges
            UMLG.get().removeVertex(associationClassVertex);
        }
    }

}