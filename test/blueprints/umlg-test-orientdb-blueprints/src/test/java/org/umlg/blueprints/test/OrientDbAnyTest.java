package org.umlg.blueprints.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.umlg.meta.TestMetaClasses;
import org.umlg.tinker.collectiontest.OrderedSetTestTest;
import org.umlg.tinker.collectiontest.QualifiedBagTest;
import org.umlg.tinker.collectiontest.QualifiedSequenceTest;
import org.umlg.tinker.deletiontest.DeletionTest;
import org.umlg.tinker.qualifiertest.TestQualifiedDeletion;
import org.umlg.tinker.qualifiertest.TestQualifier;
import org.umlg.tinker.qualifiertest.TestQualifierOnManyToMany;

/**
 * Date: 2013/01/28
 * Time: 7:18 PM
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestQualifiedDeletion.class, TestQualifier.class, TestQualifierOnManyToMany.class})
public class OrientDbAnyTest {

}