package org.tuml.test.restlet;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface HumansServerResource {
	@Get(	"json")
	public Representation get();


}