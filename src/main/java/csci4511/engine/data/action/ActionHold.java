package csci4511.engine.data.action;

import csci4511.engine.data.Unit;

import javax.annotation.Nonnull;

public class ActionHold extends Action {
	
	public ActionHold(@Nonnull Unit unit) {
		super(ActionType.HOLD, unit, unit.getNode());
	}
	
}
