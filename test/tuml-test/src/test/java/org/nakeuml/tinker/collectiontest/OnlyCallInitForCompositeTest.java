package org.nakeuml.tinker.collectiontest;

import org.junit.Test;
import org.neo4j.graphdb.TransactionFailureException;
import org.tinker.concretetest.God;
import org.tinker.interfacetest.ManyA;
import org.tinker.interfacetest.ManyB;
import org.tuml.runtime.test.BaseLocalDbTest;

import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;

public class OnlyCallInitForCompositeTest extends BaseLocalDbTest {

	@Test(expected=TransactionFailureException.class)
	public void onlyCallInitForCompositeTest() {
		db.startTransaction();
		God god = new God(true);
		god.setName("THEGOD");
		ManyA manyA = new ManyA(god);
		manyA.setName("manyA");
		ManyB manyB = new ManyB(true);
		manyB.setName("manyB");
		manyA.addToIManyB(manyB);
		db.stopTransaction(Conclusion.SUCCESS);
	}
	
}