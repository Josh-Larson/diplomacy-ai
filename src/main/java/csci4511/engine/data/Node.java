package csci4511.engine.data;

import csci4511.engine.data.action.Action;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Node {
	
	private final String name;
	private final boolean supply;
	private final Country homeCountry;
	private final List<Node> armyMovements;
	private final List<Node> fleetMovements;
	private final List<Action> resolvingActions;
	
	private Country country;
	private Unit garissoned;
	
	public Node(@Nonnull String name, boolean supply, Country homeCountry) {
		this.name = name;
		this.supply = supply;
		this.homeCountry = homeCountry;
		this.armyMovements = new ArrayList<>();
		this.fleetMovements = new ArrayList<>();
		this.resolvingActions = new CopyOnWriteArrayList<>();
		this.country = homeCountry;
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
		List<Node> movements = new ArrayList<>(armyMovements);
		movements.addAll(fleetMovements);
		return movements;
	}
	
	@Nonnull
	public List<Action> getResolvingActions() {
		return resolvingActions;
	}
	
	public boolean isResolved() {
		return resolvingActions.isEmpty();
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
		node.addArmyMovement(this);
	}
	
	public void addFleetMovement(@Nonnull Node node) {
		if (fleetMovements.contains(node) || node == this)
			return;
		fleetMovements.add(node);
		node.addFleetMovement(node);
	}
	
	@Override
	public String toString() {
		if (garissoned != null)
			return "Node[" + name + "  GAR="+garissoned.getCountry()+"]";
		return "Node["+name+"]";
	}
	
	void addAction(@Nonnull Action action) {
		resolvingActions.add(action);
	}
	
	void removeAction(@Nonnull Action action) {
		resolvingActions.remove(action);
	}
	
	void setGarissoned(Unit garissoned) {
		this.garissoned = garissoned;
	}
}
