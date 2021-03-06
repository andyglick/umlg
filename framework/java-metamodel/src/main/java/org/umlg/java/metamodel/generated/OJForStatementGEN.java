/*
 * File generated by Grasland Grammar Generator on Dec 23, 2006 7:43:22 PM
 */
package org.umlg.java.metamodel.generated;

import java.util.ArrayList;
import java.util.List;

import org.umlg.java.metamodel.OJBlock;
import org.umlg.java.metamodel.OJElement;
import org.umlg.java.metamodel.OJForStatement;
import org.umlg.java.metamodel.OJPathName;
import org.umlg.java.metamodel.OJStatement;
import org.umlg.java.metamodel.utilities.InvariantError;


/** Class ...
 */
abstract public class OJForStatementGEN extends OJStatement {
	private String f_elemName = "";
	private String f_collection = "";
	private OJPathName f_elemType = null;
	private OJBlock f_body = null;
	static protected boolean usesAllInstances = false;
	static protected List<OJForStatement> allInstances = new ArrayList<OJForStatement>();

	/** Default constructor for OJForStatement
	 */
	protected OJForStatementGEN() {
		super();
		if ( usesAllInstances ) {
			allInstances.add(((OJForStatement)this));
		}
	}
	
	/** Constructor for OJForStatementGEN
	 * 
	 * @param name 
	 * @param comment 
	 * @param elemName 
	 * @param collection 
	 */
	protected OJForStatementGEN(String name, String comment, String elemName, String collection) {
		super();
		super.setName(name);
		super.setComment(comment);
		this.setElemName(elemName);
		this.setCollection(collection);
		if ( usesAllInstances ) {
			allInstances.add(((OJForStatement)this));
		}
	}

	/** Implements the getter for feature '+ elemName : String'
	 */
	public String getElemName() {
		return f_elemName;
	}
	
	/** Implements the setter for feature '+ elemName : String'
	 * 
	 * @param element 
	 */
	public void setElemName(String element) {
		if ( f_elemName != element ) {
			f_elemName = element;
		}
	}
	
	/** Implements the getter for feature '+ collection : String'
	 */
	public String getCollection() {
		return f_collection;
	}
	
	/** Implements the setter for feature '+ collection : String'
	 * 
	 * @param element 
	 */
	public void setCollection(String element) {
		if ( f_collection != element ) {
			f_collection = element;
		}
	}
	
	/** Implements the getter for feature '+ elemType : OJPathName'
	 */
	public OJPathName getElemType() {
		return f_elemType;
	}
	
	/** Implements the setter for feature '+ elemType : OJPathName'
	 * 
	 * @param element 
	 */
	public void setElemType(OJPathName element) {
		if ( f_elemType != element ) {
			f_elemType = element;
		}
	}
	
	/** Implements the getter for feature '+ body : OJBlock'
	 */
	public OJBlock getBody() {
		return f_body;
	}
	
	/** Implements the setter for feature '+ body : OJBlock'
	 * 
	 * @param element 
	 */
	public void setBody(OJBlock element) {
		if ( f_body != element ) {
			f_body = element;
		}
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
		if ( getElemType() == null ) {
			String message = "Mandatory feature 'elemType' in object '";
			message = message + this.getIdString();
			message = message + "' of type '" + this.getClass().getName() + "' has no value.";
			result.add(new InvariantError(((OJForStatement)this), message));
		}
		if ( getBody() == null ) {
			String message = "Mandatory feature 'body' in object '";
			message = message + this.getIdString();
			message = message + "' of type '" + this.getClass().getName() + "' has no value.";
			result.add(new InvariantError(((OJForStatement)this), message));
		}
		return result;
	}
	
	/** Default toString implementation for OJForStatement
	 */
	public String toString() {
		String result = "";
		result = super.toString();
		if ( this.getElemName() != null ) {
			result = result + " elemName:" + this.getElemName() + ", ";
		}
		if ( this.getCollection() != null ) {
			result = result + " collection:" + this.getCollection();
		}
		return result;
	}
	
	/** Returns the default identifier for OJForStatement
	 */
	public String getIdString() {
		String result = "";
		if ( this.getElemName() != null ) {
			result = result + this.getElemName();
		}
		return result;
	}
	
	/** Implements the OCL allInstances operation
	 */
	static public List allInstances() {
		if ( !usesAllInstances ) {
			throw new RuntimeException("allInstances is not implemented for ((OJForStatement)this) class. Set usesAllInstances to true, if you want allInstances() implemented.");
		}
		return allInstances;
	}
	
	/** Returns a copy of this instance. True parts, i.e. associations marked
			'aggregate' or 'composite', and attributes, are copied as well. References to
			other objects, i.e. associations not marked 'aggregate' or 'composite', will not
			be copied. The returned copy will refer to the same objects as the original (this)
			instance.
	 */
	public OJElement getCopy() {
		OJForStatement result = new OJForStatement();
		this.copyInfoInto(result);
		return result;
	}
	
	/** Copies all attributes and associations of this instance into 'copy'.
			True parts, i.e. associations marked 'aggregate' or 'composite', and attributes, 
			are copied as well. References to other objects, i.e. associations not marked 
			'aggregate' or 'composite', will not be copied. The 'copy' will refer 
			to the same objects as the original (this) instance.
	 * 
	 * @param copy 
	 */
	public void copyInfoInto(OJForStatement copy) {
		super.copyInfoInto(copy);
		copy.setElemName(getElemName());
		copy.setCollection(getCollection());
		if ( getElemType() != null ) {
			copy.setElemType(getElemType());
		}
		if ( getBody() != null ) {
			copy.setBody(getBody());
		}
	}

}