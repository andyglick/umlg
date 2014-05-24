package org.umlg.runtime.domain.activity.interf;

import com.tinkerpop.blueprints.Vertex;
import org.umlg.runtime.domain.BaseTinkerBehavioredClassifier;
import org.umlg.runtime.domain.CompositionNode;
import org.umlg.runtime.domain.activity.NodeStat;
import org.umlg.runtime.domain.activity.NodeStatus;
import org.umlg.runtime.domain.activity.Token;

import java.util.List;
import java.util.NoSuchElementException;

public interface IActivityNode<IN extends Token, OUT extends Token> extends CompositionNode, INamedElement {
	boolean mayContinue();

	Boolean processNextStart() throws NoSuchElementException;

	List<? extends IActivityEdge<? extends IN>> getIncoming();

	List<? extends IActivityEdge<OUT>> getOutgoing();

	boolean isEnabled();

	NodeStatus getNodeStatus();

	boolean isComplete();

	NodeStat getNodeStat();

	List<? extends Token> getInTokens();

	List<?> getInTokens(String inFlowName);

	List<?> getOutTokens();

	List<?> getOutTokens(String outFlowName);

	Vertex getVertex();

	BaseTinkerBehavioredClassifier getContextObject();

}