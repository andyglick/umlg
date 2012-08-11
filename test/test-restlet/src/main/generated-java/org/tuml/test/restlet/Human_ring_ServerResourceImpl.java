package org.tuml.test.restlet;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.tuml.runtime.adaptor.GraphDb;
import org.tuml.runtime.domain.json.ToJsonUtil;
import org.tuml.test.Human;
import org.tuml.test.Human.HumanRuntimePropertyEnum;
import org.tuml.test.Ring.RingRuntimePropertyEnum;

public class Human_ring_ServerResourceImpl extends ServerResource implements Human_ring_ServerResource {
	private int humanId;

	/**
	 * default constructor for Human_ring_ServerResourceImpl
	 */
	public Human_ring_ServerResourceImpl() {
		setNegotiated(false);
	}

	@Override
	public Representation get() throws ResourceException {
		this.humanId = Integer.parseInt((String)getRequestAttributes().get("humanId"));
		Human parentResource = new Human(GraphDb.getDb().getVertex(this.humanId));
		StringBuilder json = new StringBuilder();
		json.append("[");
		json.append(ToJsonUtil.toJson(parentResource.getRing()));
		json.append(",");
		json.append(" {\"meta\" : ");
		json.append(HumanRuntimePropertyEnum.asJson());
		json.append("}, ");
		json.append(" {\"meta\" : ");
		json.append(RingRuntimePropertyEnum.asJson());
		json.append("}]");
		return new JsonRepresentation(json.toString());
	}


}