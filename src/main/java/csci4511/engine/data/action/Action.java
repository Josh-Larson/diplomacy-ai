package csci4511.engine.data.action;

import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;

import javax.annotation.Nonnull;

public abstract class Action {
	
	private final ActionType type;
	private final Unit unit;
	private final Node start;
	
	Action(@Nonnull ActionType type, @Nonnull Unit unit, @Nonnull Node start) {
		this.type = type;
		this.unit = unit;
		this.start = start;
	}
	
	@Nonnull
	public ActionType getType() {
		return type;
	}
	
	@Nonnull
	public Unit getUnit() {
		return unit;
	}
	
	@Nonnull
	public Node getStart() {
		return start;
	}
	
	@Override
	public Action clone() {
		try {
			return (Action) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Nonnull
	public abstract Node getDestination();
	
}
