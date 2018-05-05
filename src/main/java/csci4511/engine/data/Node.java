package csci4511.engine.data;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private final String name;
	private final boolean supply;
	private final Country homeCountry;
	private final List<Node> armyMovements;
	private final List<Node> fleetMovements;
	private final List<Node> movements;
	
	private Country country;
	private Unit garissoned;
	
	public Node(@Nonnull String name, boolean supply, Country homeCountry) {
		this.name = name;
		this.supply = supply;
		this.homeCountry = homeCountry;
		this.armyMovements = new ArrayList<>();
		this.fleetMovements = new ArrayList<>();
		this.movements = new ArrayList<>();
		this.country = homeCountry;
		this.garissoned = null;
	}
	
	public Node(Node copy) {
		this.name = copy.name;
		this.supply = copy.supply;
		this.homeCountry = copy.homeCountry;
		this.armyMovements = new ArrayList<>();
		this.fleetMovements = new ArrayList<>();
		this.movements = new ArrayList<>();
		this.country = copy.homeCountry;
		this.garissoned = null;
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
	public List<Node> getFleetMovements() {
		return fleetMovements;
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
	
	public void addFleetMovement(@Nonnull Node node) {
		if (fleetMovements.contains(node) || node == this)
			return;
		fleetMovements.add(node);
		if (!movements.contains(node))
			movements.add(node);
		node.addFleetMovement(this);
	}
	
	@Override
	public String toString() {
		if (garissoned != null)
			return "Node[" + name + "  GAR="+garissoned.getCountry()+"]";
		return "Node["+name+"]";
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o == this;
	}
	
	void setGarissoned(Unit garissoned) {
		this.garissoned = garissoned;
	}
}
