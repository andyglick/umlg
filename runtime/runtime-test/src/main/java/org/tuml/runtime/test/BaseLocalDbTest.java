package org.tuml.runtime.test;

import java.net.URL;
import java.util.logging.LogManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.tuml.runtime.adaptor.*;

public class BaseLocalDbTest {

	protected TumlGraph db;

	@BeforeClass
	public static void beforeClass() {
		try {
			URL url = BaseLocalDbTest.class.getResource("/logging.properties");
			LogManager.getLogManager().readConfiguration(url.openStream());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Before
	public void before() {
        TumlGraphManager.INSTANCE.deleteGraph();
        this.db = TumlGraphCreator.INSTANCE.startupGraph();
		GraphDb.setDb(this.db);
	}

    @After
    public void after() {
        this.db.shutdown();
        GraphDb.remove();
    }

	protected long countVertices() {
		return db.countVertices();
	}

	protected long countEdges() {
		return db.countEdges();
	}

    protected boolean isTransactionFailedException(Exception e) {
        return TumlTestUtilFactory.getTestUtil().isTransactionFailedException(e);
    }

}
