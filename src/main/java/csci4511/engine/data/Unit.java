package csci4511.engine.data;

import csci4511.engine.data.action.Action;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class Unit {
	
	private final UnitType type;
	private final Country country;
	private Node node;
	private Action action;
	
	public Unit(@Nonnull UnitType type, @Nonnull Country country) {
		this.type = type;
		this.country = country;
		this.node = null;
		this.action = null;
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
	
	@CheckForNull
	public Action getAction() {
		return action;
	}
	
	public void setNode(@Nonnull Node node) {
		this.node = node;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
}
