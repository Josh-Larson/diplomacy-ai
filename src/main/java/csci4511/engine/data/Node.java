package csci4511.engine.data;

import csci4511.engine.data.action.Action;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private final boolean supply;
	private final Country homeCountry;
	private final List<Node> armyMovements;
	private final List<Node> fleetMovements;
	private final List<Action> resolvingActions;
	
	private Country country;
	private Unit garissoned;
	
	public Node(boolean supply, Country homeCountry) {
		this.supply = supply;
		this.homeCountry = homeCountry;
		this.armyMovements = new ArrayList<>();
		this.fleetMovements = new ArrayList<>();;
		this.resolvingActions = new ArrayList<>();
		this.country = homeCountry;
		this.garissoned = null;
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
	public List<Action> getResolvingActions() {
		return resolvingActions;
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
	
	public void setGarissoned(Unit garissoned) {
		this.garissoned = garissoned;
	}
	
	public void addArmyMovement(@Nonnull Node node) {
		armyMovements.add(node);
	}
	
	public void addFleetMovement(@Nonnull Node node) {
		fleetMovements.add(node);
	}
	
	public void clearResolvingActions() {
		resolvingActions.clear();
	}
	
	public void addAction(@Nonnull Action action) {
		resolvingActions.add(action);
	}
}
