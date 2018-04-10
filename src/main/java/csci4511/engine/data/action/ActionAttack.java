package csci4511.engine.data.action;

import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;

import javax.annotation.Nonnull;

public class ActionAttack extends Action {
	
	private final Node destination;
	
	public ActionAttack(@Nonnull Unit unit, @Nonnull Node destination) {
		super(ActionType.ATTACK, unit, unit.getNode());
		this.destination = destination;
	}
	
	@Nonnull
	public Node getDestination() {
		return destination;
	}
	
	@Override
	public String toString() {
		return "ActionAttack["+getUnit()+" to "+destination+"]";
	}
	
}
