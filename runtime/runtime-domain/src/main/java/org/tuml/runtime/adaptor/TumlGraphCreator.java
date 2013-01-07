package org.tuml.runtime.adaptor;

import org.tuml.runtime.util.TinkerImplementation;
import org.tuml.runtime.util.TumlProperties;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Date: 2012/12/29
 * Time: 9:23 PM
 */
public class TumlGraphCreator {

    public static TumlGraphCreator INSTANCE = new TumlGraphCreator();
    private static final Logger logger = Logger.getLogger(TumlGraphCreator.class.getPackage().getName());

    private TumlGraphCreator() {

    }

    public TumlGraph startupGraph() {
        if (GraphDb.getDb() == null) {
            try {
                String dbUrl = TumlProperties.INSTANCE.getTumlDbLocation();
                TinkerImplementation tinkerImplementation = TinkerImplementation.fromName(TumlProperties.INSTANCE.getTinkerImplementation());
                @SuppressWarnings("unchecked")
                Class<TumlGraphFactory> factory = (Class<TumlGraphFactory>) Class.forName(tinkerImplementation.getTumlGraphFactory());
                Method m = factory.getDeclaredMethod("getInstance", new Class[0]);
                TumlGraphFactory nakedGraphFactory = (TumlGraphFactory) m.invoke(null);
                TumlGraph tumlGraph = nakedGraphFactory.getTumlGraph(dbUrl);
                GraphDb.setDb(tumlGraph);
                return tumlGraph;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException("TumlGraphCreator.INSTANCE.startupGraph() may only be called once at application startup.");
        }
    }

}
