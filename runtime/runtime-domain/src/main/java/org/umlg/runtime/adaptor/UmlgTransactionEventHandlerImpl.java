package org.umlg.runtime.adaptor;

import org.umlg.runtime.domain.UmlgNode;
import org.umlg.runtime.validation.TumlConstraintViolation;
import org.umlg.runtime.validation.TumlConstraintViolationException;

import java.util.List;

/**
 * This class validates what is being committed.
 * On every UmlgNode is calls validateMultiplicities.
 * futher it validates that a non root class has one and only one composite parent
 *
 *
 */
public class UmlgTransactionEventHandlerImpl implements UmlgTransactionEventHandler {


    public UmlgTransactionEventHandlerImpl() {
        super();
    }

    @Override
    public void beforeCommit() {
        try {
            if (GraphDb.getDb() != null) {
                TransactionThreadVar.clear();
                GraphDb.incrementTransactionCount();
                List<UmlgNode> entities = TransactionThreadEntityVar.get();
                for (UmlgNode umlgNode : entities) {
                    List<TumlConstraintViolation> requiredConstraintViolations = umlgNode.validateMultiplicities();
                    requiredConstraintViolations.addAll(umlgNode.checkClassConstraints());
                    if (!requiredConstraintViolations.isEmpty()) {
                        throw new TumlConstraintViolationException(requiredConstraintViolations);
                    }

                    if (!umlgNode.isTinkerRoot() && /*tumlNode instanceof CompositionNode &&*/ (!umlgNode.hasOnlyOneCompositeParent() || umlgNode.getOwningObject() == null)) {
//                            if (entity instanceof BaseTinkerAuditable && ((BaseTinkerAuditable) entity).getDeletedOn().isBefore(new DateTime())) {
//                                return null;
//                            }
                        throw new IllegalStateException(String.format("Entity %s %s does not have a composite owner", umlgNode.getClass().getSimpleName(), umlgNode.getId()));
                    }
                }
            }
        } finally {
            TransactionThreadEntityVar.remove();
            TransactionThreadMetaNodeVar.remove();
        }
    }

}
