package org.umlg.runtime.domain;

import org.umlg.runtime.adaptor.TransactionThreadVar;
import org.umlg.runtime.util.UmlgFormatter;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class BaseTinkerAuditable extends BaseUmlgAudit implements TinkerAuditableNode, Serializable{

	private static final long serialVersionUID = 3751023772087546585L;
	
	public BaseTinkerAuditable() {
		super();
	}
	
	public void setDeletedOn(LocalDateTime deletedOn) {
		super.setDeletedOn(deletedOn);
		if ( TransactionThreadVar.hasNoAuditEntry(getClass().getName() + getUid()) ) {
			createAuditVertex(false);
		}
		getAuditVertex().property("deletedOn", UmlgFormatter.format(deletedOn));

	}

}
