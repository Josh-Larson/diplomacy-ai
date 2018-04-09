package csci4511.engine;

import csci4511.engine.data.Board;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.ActionHold;

public class ResolveEngine {
	
	public static void resolve(Board board) {
		passActionExists(board);
	}
	
	/** Ensures all units have an action */
	private static void passActionExists(Board board) {
		for (Unit u : board.getUnits()) {
			if (u.getAction() == null)
				u.setAction(new ActionHold(u));
		}
	}
	
}
