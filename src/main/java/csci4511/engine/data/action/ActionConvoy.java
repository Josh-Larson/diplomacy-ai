package csci4511.engine.data.action;

import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;

import javax.annotation.Nonnull;

public class ActionConvoy extends Action {
	
	private final Action action;
	
	public ActionConvoy(@Nonnull Unit unit, @Nonnull Action action) {
		super(ActionType.CONVOY, unit, unit.getNode());
		this.action = action;
	}
	
	@Nonnull
	@Override
	public Node getDestination() {
		return action.getDestination();
	}
	
	@Nonnull
	public Action getAction() {
		return action;
	}
	
	@Override
	public String toString() {
		return "ActionConvoy["+getUnit()+": "+action+"]";
	}
	
}
