package org.umlg.tests.primitive;

import org.junit.Assert;
import org.junit.Test;
import org.umlg.concretetest.God;
import org.umlg.runtime.test.BaseLocalDbTest;

public class TestBooleanPrimitive extends BaseLocalDbTest {
	
	@Test
	public void testBooleanDefaultsToFalse() {
		God g = new God(true);
		g.setName("G");
        db.commit();
		Assert.assertFalse(g.getTestBoolean());
	}

}
