package csci4511.engine.data.node;

import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.*;

public class StandardNode implements Node {
	
	private final String name;
	private final boolean supply;
	private final Country homeCountry;
	private final List<Node> armyMovements;
	private final Map<String, CoastalNode> fleetMovements;
	private final List<Node> movements;
	
	private Country country;
	private Unit garissoned;
	
	public StandardNode(@Nonnull String name, boolean supply, Country homeCountry) {
		this.name = name;
		this.supply = supply;
		this.homeCountry = homeCountry;
		this.armyMovements = new ArrayList<>();
		this.fleetMovements = new HashMap<>();
		this.movements = new ArrayList<>();
		this.country = homeCountry;
		this.garissoned = null;
		
		fleetMovements.put("", new CoastalNode(this, ""));
	}
	
	public StandardNode(StandardNode copy) {
		this(copy.name, copy.supply, copy.homeCountry);
		country = copy.country;
	}
	
	@Nonnull
	public String getName() {
		return name;
	}
	
	public boolean isSupply() {
		return supply;
	}
	
	@CheckForNull
	public Country getHomeCountry() {
		return homeCountry;
	}
	
	@Nonnull
	public List<Node> getArmyMovements() {
		return armyMovements;
	}
	
	@Nonnull
	public List<CoastalNode> getFleetMovements(String coast) {
		return fleetMovements.get(coast).getFleetMovements();
	}
	
	@Nonnull
	public List<CoastalNode> getFleetMovements() {
		return getFleetMovements("");
	}
	
	@Nonnull
	public List<Node> getMovements() {
		return movements;
	}
	
	@CheckForNull
	public Country getCountry() {
		return country;
	}
	
	@CheckForNull
	public Unit getGarissoned() {
		return garissoned;
	}
	
	public void setCountry(@Nonnull Country country) {
		this.country = country;
	}
	
	public void addArmyMovement(@Nonnull Node node) {
		if (armyMovements.contains(node) || node == this)
			return;
		armyMovements.add(node);
		if (!movements.contains(node))
			movements.add(node);
		node.addArmyMovement(this);
	}
	
	@Nonnull
	public CoastalNode getCoast(@Nonnull String coast) {
		return fleetMovements.computeIfAbsent(coast, c -> new CoastalNode(this, c));
	}
	
	@Nonnull
	public Collection<CoastalNode> getCoasts() {
		return fleetMovements.values();
	}
	
	@Override
	public String toString() {
		if (garissoned != null)
			return "Node[" + name + "  GAR="+garissoned.getCountry()+"]";
		return "Node["+ name +"]";
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o == this;
	}
	
	@Override
	public void setGarissoned(Unit garissoned) {
		this.garissoned = garissoned;
	}
	
	@Nonnull
	@Override
	public Node getCoreNode() {
		return this;
	}
	
}
