package org.tuml.ocl.test;

import junit.framework.Assert;

import org.junit.Test;
import org.tuml.runtime.test.BaseLocalDbTest;
import org.tuml.testocl.OclTest1;
import org.tuml.testocl.OclTestCollection;
import org.tuml.testocl.OclTestCollection2;

import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;

public class TestOcl extends BaseLocalDbTest {

	@Test
	public void test() {
		db.startTransaction();
		OclTest1 oclTest1 = new OclTest1(true);
		oclTest1.setProperty1("property1");
		OclTestCollection oclTestCollection1 = new OclTestCollection(oclTest1);
		oclTestCollection1.setName("oclTestCollection1");
		OclTestCollection oclTestCollection2 = new OclTestCollection(oclTest1);
		oclTestCollection2.setName("oclTestCollection2");
		OclTestCollection oclTestCollectionJohn = new OclTestCollection(oclTest1);
		oclTestCollectionJohn.setName("john");
		
		OclTestCollection2 oclTestCollection2_1_1 = new OclTestCollection2(oclTestCollection1);
		oclTestCollection2_1_1.setName("1_1");
		OclTestCollection2 oclTestCollection2_1_2 = new OclTestCollection2(oclTestCollection1);
		oclTestCollection2_1_2.setName("1_2");
		OclTestCollection2 oclTestCollection2_1_3 = new OclTestCollection2(oclTestCollection1);
		oclTestCollection2_1_3.setName("1_3");

		OclTestCollection2 oclTestCollection2_2_1 = new OclTestCollection2(oclTestCollection2);
		oclTestCollection2_2_1.setName("2_1");
		OclTestCollection2 oclTestCollection2_2_2 = new OclTestCollection2(oclTestCollection2);
		oclTestCollection2_2_2.setName("2_2");
		OclTestCollection2 oclTestCollection2_2_3 = new OclTestCollection2(oclTestCollection2);
		oclTestCollection2_2_3.setName("2_3");
		
		db.stopTransaction(Conclusion.SUCCESS);
		Assert.assertEquals(oclTest1.getProperty1(), oclTest1.getDerivedProperty1());
		Assert.assertEquals(1, oclTest1.getOclTestCollectionSelect().size());
		Assert.assertNotNull(oclTest1.getOclTestAny());
		Assert.assertEquals(3, oclTest1.getOclTestFlatten().size());
		Assert.assertEquals(6, oclTest1.getOclTestCollection2().size());
		Assert.assertEquals(6, oclTest1.getOclTestCollection2Name().size());
	}
	
}