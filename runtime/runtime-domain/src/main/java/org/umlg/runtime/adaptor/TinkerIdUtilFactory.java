package org.umlg.runtime.adaptor;

import org.umlg.runtime.util.TinkerImplementation;
import org.umlg.runtime.util.TumlProperties;

import java.lang.reflect.Method;

public class TinkerIdUtilFactory {

	private static TinkerIdUtil tinkerIdUtil;

	@SuppressWarnings("unchecked")
	public static TinkerIdUtil getIdUtil() {
		if (tinkerIdUtil == null) {
			try {
                TinkerImplementation tinkerImplementation = TinkerImplementation.fromName(TumlProperties.INSTANCE.getTinkerImplementation());
				Class<TinkerIdUtil> factory = (Class<TinkerIdUtil>) Class.forName(tinkerImplementation.getTumlIdUtil());
				Method m = factory.getDeclaredMethod("getInstance", new Class[0]);
				TinkerIdUtil idUtil = (TinkerIdUtil) m.invoke(null);
				tinkerIdUtil = idUtil;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return tinkerIdUtil;
	}

}