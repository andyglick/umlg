package org.nakeuml.tinker.concretetest;

import org.junit.Assert;

import org.junit.Test;
import org.umlg.concretetest.God;
import org.umlg.inheritencetest.Mamal;
import org.umlg.runtime.test.BaseLocalDbTest;

import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;

public class TestOriginalUid extends BaseLocalDbTest {

	@Test
	public void testOriginalUid() {
		db.startTransaction();
		God god = new God(true);
		god.setName("GODDER");
		Mamal mamal = new Mamal(god);
		mamal.setName("mamal1");
		db.stopTransaction(Conclusion.SUCCESS);
		Assert.assertEquals(god.getUid(), god.getAudits().get(0).getOriginalUid());
		Assert.assertEquals(mamal.getUid(), mamal.getAudits().get(0).getOriginalUid());
	}
	
}