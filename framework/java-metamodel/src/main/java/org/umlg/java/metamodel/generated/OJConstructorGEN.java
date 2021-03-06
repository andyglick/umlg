/*
 * File generated by Grasland Grammar Generator on Dec 23, 2006 7:26:03 PM
 */
package org.umlg.java.metamodel.generated;

import java.util.ArrayList;
import java.util.List;

import org.umlg.java.metamodel.OJClass;
import org.umlg.java.metamodel.OJConstructor;
import org.umlg.java.metamodel.OJOperation;
import org.umlg.java.metamodel.utilities.InvariantError;


/** Class ...
 */
abstract public class OJConstructorGEN extends OJOperation {
	private OJClass f_owningClass = null;
	static protected boolean usesAllInstances = false;
	static protected List<OJConstructor> allInstances = new ArrayList<OJConstructor>();

	/** Default constructor for OJConstructor
	 */
	protected OJConstructorGEN() {
		super();
		if ( usesAllInstances ) {
			allInstances.add(((OJConstructor)this));
		}
	}
	
	/** Constructor for OJConstructorGEN
	 * 
	 * @param name 
	 * @param comment 
	 * @param isStatic 
	 * @param isFinal 
	 * @param isVolatile 
	 * @param isAbstract 
	 */
	protected OJConstructorGEN(String name, String comment, boolean isStatic, boolean isFinal, boolean isVolatile, boolean isAbstract) {
		super();
		super.setName(name);
		super.setComment(comment);
		super.setStatic(isStatic);
		super.setFinal(isFinal);
		super.setVolatile(isVolatile);
		super.setAbstract(isAbstract);
		if ( usesAllInstances ) {
			allInstances.add(((OJConstructor)this));
		}
	}

	/** Implements the setter of association end owningClass
	 * 
	 * @param element 
	 */
	public void setOwningClass(OJClass element) {
		if ( this.f_owningClass != element ) {
			if ( this.f_owningClass != null ) {
				this.f_owningClass.z_internalRemoveFromConstructors( (OJConstructor)((OJConstructor)this) );
			}
			this.f_owningClass = element;
			if ( element != null ) {
				element.z_internalAddToConstructors( (OJConstructor)((OJConstructor)this) );
			}
		}
	}
	
	/** Implements the getter for owningClass
	 */
	public OJClass getOwningClass() {
		return f_owningClass;
	}
	
	/** Should NOT be used by clients! Implements the correct setting of the link for + owningClass : OJClass 
						when a single element is added to it.
	 * 
	 * @param element 
	 */
	public void z_internalAddToOwningClass(OJClass element) {
		this.f_owningClass = element;
	}
	
	/** Should NOT be used by clients! Implements the correct setting of the link for + owningClass : OJClass 
						when a single element is removed to it.
	 * 
	 * @param element 
	 */
	public void z_internalRemoveFromOwningClass(OJClass element) {
		this.f_owningClass = null;
	}
	
	/** Checks all invariants of this object and returns a list of messages about broken invariants
	 */
	public List<InvariantError> checkAllInvariants() {
		List<InvariantError> result = new ArrayList<InvariantError>();
		return result;
	}
	
	/** Implements a check on the multiplicities of all attributes and association ends
	 */
	public List<InvariantError> checkMultiplicities() {
		List<InvariantError> result = new ArrayList<InvariantError>();
		if ( getOwningClass() == null ) {
			String message = "Mandatory feature 'owningClass' in object '";
			message = message + this.getIdString();
			message = message + "' of type '" + this.getClass().getName() + "' has no value.";
			result.add(new InvariantError(((OJConstructor)this), message));
		}
		return result;
	}
	
	/** Default toString implementation for OJConstructor
	 */
	public String toString() {
		String result = "";
		result = super.toString();
		return result;
	}
	
	/** Returns the default identifier for OJConstructor
	 */
	public String getIdString() {
		String result = "";
		result = super.getIdString();
		return result;
	}
	
	/** Implements the OCL allInstances operation
	 */
	static public List allInstances() {
		if ( !usesAllInstances ) {
			throw new RuntimeException("allInstances is not implemented for ((OJConstructor)this) class. Set usesAllInstances to true, if you want allInstances() implemented.");
		}
		return allInstances;
	}
	
//	/** Returns a copy of this instance. True parts, i.e. associations marked
//			'aggregate' or 'composite', and attributes, are copied as well. References to
//			other objects, i.e. associations not marked 'aggregate' or 'composite', will not
//			be copied. The returned copy will refer to the same objects as the original (this)
//			instance.
//	 */
//	public OJElement getCopy() {
//		OJConstructor result = new OJConstructor();
//		this.copyInfoInto(result);
//		return result;
//	}
//	
//	/** Copies all attributes and associations of this instance into 'copy'.
//			True parts, i.e. associations marked 'aggregate' or 'composite', and attributes, 
//			are copied as well. References to other objects, i.e. associations not marked 
//			'aggregate' or 'composite', will not be copied. The 'copy' will refer 
//			to the same objects as the original (this) instance.
//	 * 
//	 * @param copy 
//	 */
//	public void copyInfoInto(OJConstructor copy) {
//		super.copyInfoInto(copy);
//		if ( getOwningClass() != null ) {
//			copy.setOwningClass(getOwningClass());
//		}
//	}
//
}