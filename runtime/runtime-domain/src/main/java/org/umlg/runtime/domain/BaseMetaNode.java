package org.umlg.runtime.domain;

import com.tinkerpop.blueprints.Vertex;
import org.umlg.runtime.adaptor.TinkerIdUtilFactory;

import java.util.UUID;

/**
 * Date: 2013/03/09
 * Time: 8:42 AM
 */
public abstract class BaseMetaNode implements TumlMetaNode {
    protected Vertex vertex;

    public BaseMetaNode() {
        super();
    }

    @Override
    public Vertex getVertex() {
        return vertex;
    }

    @Override
    public final Object getId() {
        return TinkerIdUtilFactory.getIdUtil().getId(this.vertex);
    }

    @Override
    public String getUid() {
        String uid = this.vertex.getProperty("uid");
        if ( uid==null || uid.trim().length()==0 ) {
            uid=UUID.randomUUID().toString();
            this.vertex.setProperty("uid", uid);
        }
        return uid;
    }

    public void defaultCreate() {
        getUid();
    }


}
