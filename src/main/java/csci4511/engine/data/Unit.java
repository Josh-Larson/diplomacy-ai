package csci4511.engine.data;

import csci4511.engine.data.node.StandardNode;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Unit {
	
	private static final AtomicInteger GLOBAL_UNIT_ID = new AtomicInteger();
	
	private final int id;
	private final UnitType type;
	private final Country country;
	private Node node;
	
	public Unit(@Nonnull UnitType type, @Nonnull Country country) {
		this.id = GLOBAL_UNIT_ID.getAndIncrement();
		this.type = type;
		this.country = country;
		this.node = null;
	}
	
	public Unit(Unit copy) {
		this.id = GLOBAL_UNIT_ID.getAndIncrement();
		this.type = copy.type;
		this.country = copy.country;
		this.node = null;
	}
	
	@Nonnull
	public UnitType getType() {
		return type;
	}
	
	@Nonnull
	public Country getCountry() {
		return country;
	}
	
	@Nonnull
	public Node getNode() {
		return node;
	}
	
	public void setNode(@Nonnull Node node) {
		if (type == UnitType.FLEET && node instanceof StandardNode)
			node = node.getCoast("");
		Node prev = this.node;
		if (prev != null && prev.getGarissoned() == this)
			prev.setGarissoned(null);
		this.node = node;
		node.setGarissoned(this);
	}
	
	@Nonnull
	public List<? extends Node> getMovementLocations() {
		return type == UnitType.ARMY ? node.getArmyMovements() : node.getFleetMovements();
	}
	
	public boolean canMoveTo(Node n) {
		return type == UnitType.ARMY ? node.getArmyMovements().contains(n) : node.getFleetMovements().contains(n);
	}
	
	@Override
	public String toString() {
		return "Unit[" + country + "@" + node.getName() + "]";
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		return o == this;
	}
	
}
