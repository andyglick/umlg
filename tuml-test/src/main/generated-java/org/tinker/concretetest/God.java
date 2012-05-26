package org.tinker.concretetest;

import com.tinkerpop.blueprints.pgm.Vertex;

import org.tuml.nakeduml.tinker.runtime.TinkerIdUtil;
import org.tuml.runtime.domain.BaseTinker;
import org.tuml.runtime.domain.TinkerCompositionNode;
import org.tuml.tinker.runtime.TransactionThreadEntityVar;

public class God extends BaseTinker implements TinkerCompositionNode {


	/** Constructor for God
	 * 
	 * @param vertex 
	 */
	public God(Vertex vertex) {
		this.vertex=vertex;
	}
	
	/** Constructor for God
	 * 
	 * @param persistent 
	 */
	public God(Boolean persistent) {
		this.vertex = GraphDb.getDb().addVertex("dribble");
		TransactionThreadEntityVar.setNewEntity(this);
		defaultCreate();
	}

	@Override
	public void clearCache() {
		this.name = null;
		this.universe = null;
		this.angel = null;
		this.spirit = null;
		this.being = null;
		this.abstractSpecies = null;
		this.iMany = null;
		this.embeddedString = null;
		this.embeddedInteger = null;
		this.realRootFolder = null;
		this.fakeRootFolder = null;
		this.reason = null;
		this.pet = null;
		this.animalFarm = null;
		this.nature =  new TinkerQualifiedSetImpl<Nature>(this, "A_<god>_<nature>", getUid(), true, false, true);
		this.hand = null;
		this.foot =  new TinkerQualifiedSequenceImpl<Foot>(this, "A_<god>_<foot>", getUid(), true, false, true);
		this.world = null;
		this.fantasy =  new TinkerQualifiedOrderedSetImpl<Fantasy>(this, "A_<god>_<fantasy>", getUid(), true, false, true);
		this.many1 = null;
		this.many2 = null;
		this.dream = null;
		this.nightmare =  new TinkerQualifiedBagImpl<Nightmare>(this, "A_<god>_<nightmare>", getUid(), true, false, true);
		this.demon = null;
		this.oneOne = null;
		this.oneTwo = null;
		this.nonNavigableOne = null;
		this.nonNavigableMany = null;
	}
	
	@Override
	public Long getId() {
		return TinkerIdUtil.getId(this.vertex);
	}
	
	@Override
	public int getObjectVersion() {
		return TinkerIdUtil.getVersion(this.vertex);
	}
	
	@Override
	public void setId(Long id) {
		TinkerIdUtil.setId(this.vertex, id);
	}

}