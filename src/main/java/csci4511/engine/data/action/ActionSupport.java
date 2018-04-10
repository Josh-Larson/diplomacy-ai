package csci4511.engine.data.action;

import csci4511.engine.data.Unit;

import javax.annotation.Nonnull;

public class ActionSupport extends Action {
	
	private final Action action;
	
	public ActionSupport(@Nonnull Unit unit, @Nonnull Action action) {
		super(ActionType.SUPPORT, unit, unit.getNode());
		this.action = action;
	}
	
	@Nonnull
	public Action getAction() {
		return action;
	}
	
	@Override
	public String toString() {
		return "ActionSupport["+getUnit()+" to "+action+"]";
	}
	
}
