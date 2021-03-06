package org.umlg.javageneration.util;

import org.eclipse.uml2.uml.MultiplicityElement;

public class UmlgMultiplicityOperations {

	public static boolean isOne(MultiplicityElement multiplicityElement) {
		return !isMany(multiplicityElement);
	}

	public static boolean isMany(MultiplicityElement multiplicityElement) {
		return multiplicityElement.getUpper() == -1 || multiplicityElement.getUpper() > 1;
	}
	
}
