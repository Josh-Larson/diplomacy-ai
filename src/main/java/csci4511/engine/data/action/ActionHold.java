package csci4511.engine.data.action;

import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;

import javax.annotation.Nonnull;

public class ActionHold extends Action {
	
	public ActionHold(@Nonnull Unit unit) {
		super(ActionType.HOLD, unit, unit.getNode());
	}
	
	@Nonnull
	@Override
	public Node getDestination() {
		return getStart();
	}
	
	@Override
	public String toString() {
		return "ActionHold["+getUnit()+"]";
	}
	
}
