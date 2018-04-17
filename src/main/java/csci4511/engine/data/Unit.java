package csci4511.engine.data;

import csci4511.engine.data.action.*;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class Unit implements Cloneable {
	
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
		node.setGarissoned(this);
	}
	
	public void clearAction() {
		setAction(null);
	}
	
	public void setActionHold() {
		setAction(new ActionHold(this));
	}
	
	public void setActionAttack(@Nonnull Node node) {
		setAction(new ActionAttack(this, node));
	}
	
	public void setActionSupport(@Nonnull Unit unit) {
		Action action = unit.getAction();
		Objects.requireNonNull(action, "Invalid unit - does not have action");
		setAction(new ActionSupport(this, action));
	}
	
	public void setActionConvoy(@Nonnull Unit unit) {
		Action action = unit.getAction();
		Objects.requireNonNull(action, "Invalid unit - does not have action");
		setAction(new ActionConvoy(this, action));
	}
	
	@Nonnull
	public List<Node> getMovementLocations() {
		return type == UnitType.ARMY ? node.getArmyMovements() : node.getFleetMovements();
	}
	
	@Override
	public String toString() {
		return "Unit[" + country + "@" + node.getName() + "]";
	}
	
	@Override
	public Unit clone() {
		try {
			Unit u = (Unit) super.clone();
			u.action = u.action.clone();
			return u;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	private void setAction(Action action) {
		if (this.action != null)
			this.action.getDestination().removeAction(this.action);
		this.action = action;
		if (action != null)
			action.getDestination().addAction(action);
	}
}
