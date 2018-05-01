package csci4511.engine.resolve;

import csci4511.engine.data.Board;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;
import csci4511.engine.data.action.ActionAttack;
import csci4511.engine.data.action.ActionSupport;
import csci4511.engine.data.action.ActionType;
import me.joshlarson.jlcommon.log.Log;

import java.util.*;
import java.util.Map.Entry;

public class ResolutionEngine {
	
	public static Map<Unit, List<Node>> resolve(Board board) {
		Map<Unit, List<Node>> retreats = new HashMap<>();
		verifyActions(board);
		resolveInvalid(board);
		int loop = 0;
		while (!isResolved(board)) {
			resolve(retreats, board);
			retreats.keySet().forEach(board::removeUnit);
			for (Unit unit : board.getUnits()) {
				if (unit.getAction() != null) {
					unit.clearAction();
					break;
				}
			}
			if (loop++ >= 100) {
				Log.d("Unresolved:");
				for (Unit u : board.getUnits()) {
					Action a = u.getAction();
					if (a == null)
						continue;
					Log.d("    Action: %s  SRC=%s  DST=%s", a, a.getStart(), a.getDestination());
				}
				System.exit(0);
			}
		}
		resolveRetreats(board, retreats);
		return retreats;
	}
	
	static void resolveSameEdge(Node src) {
		if (src.getGarissoned() == null)
			return;
		Action srcAction = src.getGarissoned().getAction();
		if (srcAction == null || srcAction.getType() != ActionType.ATTACK)
			return;
		
		Node dst = srcAction.getDestination();
		if (dst.getGarissoned() == null)
			return;
		Action dstAction = dst.getGarissoned().getAction();
		if (dstAction == null || dstAction.getType() != ActionType.ATTACK)
			return;
		
		if (dstAction.getDestination() == src) {
			int srcStrength = 0, dstStrength = 0;
			List<Action> actions = new ArrayList<>();
			actions.add(srcAction);
			actions.add(dstAction);
			for (Action srcSupportAction : dst.getResolvingActions()) {
				if (srcSupportAction instanceof ActionSupport && ((ActionSupport) srcSupportAction).getAction() == srcAction) {
					srcStrength++;
					actions.add(srcSupportAction);
				}
			}
			for (Action dstSupportAction : src.getResolvingActions()) {
				if (dstSupportAction instanceof ActionSupport && ((ActionSupport) dstSupportAction).getAction() == dstAction) {
					dstStrength++;
					actions.add(dstSupportAction);
				}
			}
			boolean validStandoff = src.getGarissoned().getCountry() != dst.getGarissoned().getCountry();
			Action winner = null;
			if (validStandoff) {
				if (srcStrength > dstStrength) {
					executeAction(srcAction, dst);
					winner = srcAction;
				} else if (srcStrength < dstStrength) {
					executeAction(dstAction, src);
					winner = dstAction;
				}
			}
			for (Action action : actions) {
				if (action != winner)
					action.getUnit().clearAction();
			}
		}
	}
	
	static void resolveUndisputed(Action a, Node n) {
		switch (a.getType()) {
			case ATTACK:
				if (n.getGarissoned() != null) {
					if (n.getGarissoned().getAction() == null) { // Final move
						a.getUnit().setActionHold();
					}
					break;
				}
			case HOLD:
				executeAction(a, n);
				break;
		}
	}
	
	static void resolveDisputed(Map<Unit, List<Node>> retreats, List<Action> actions, Node n) {
		Action winner = getWinningAction(actions);
		if (winner != null) {
			Unit garissoned = n.getGarissoned();
			boolean validDisplacement = garissoned == null || garissoned.getCountry() != winner.getUnit().getCountry();
			if (validDisplacement) {
				if (garissoned != null) {
					List<Node> possibilities = new ArrayList<>(garissoned.getMovementLocations());
					possibilities.remove(n);
					possibilities.remove(winner.getStart());
					retreats.put(garissoned, possibilities);
				}
				executeAction(winner, n);
			}
		}
		for (Action a : actions) {
			a.getUnit().clearAction();
		}
	}
	
	static Action getWinningAction(List<Action> actions) {
		Map<Action, Integer> actionStrengths = new HashMap<>();
		int max = Integer.MIN_VALUE;
		for (Action a : actions) {
			switch (a.getType()) {
				case SUPPORT:
					a = ((ActionSupport) a).getAction();
				case HOLD:
				case ATTACK: {
					int strength = actionStrengths.getOrDefault(a, 0) + 1;
					actionStrengths.put(a, strength);
					if (strength > max)
						max = strength;
					break;
				}
			}
		}
		actionStrengths.values().retainAll(Collections.singletonList(max));
		return actionStrengths.size() != 1 ? null : actionStrengths.keySet().iterator().next();
	}
	
	private static void resolve(Map<Unit, List<Node>> retreats, Board board) {
		for (Node n : board.getNodes()) {
			List<Action> actions = n.getResolvingActions();
			
			resolveSameEdge(n);
			switch (actions.size()) {
				case 0:
					break;
				case 1:
					resolveUndisputed(actions.get(0), n);
					break;
				default:
					resolveDisputed(retreats, actions, n);
					break;
			}
		}
	}
	
	/** Ensures all units have an action */
	private static void verifyActions(Board board) {
		for (Unit u : board.getUnits()) {
			if (u.getAction() == null) {
				u.setActionHold();
			}
		}
	}
	
	private static void resolveInvalid(Board board) {
		// Support/Convoy based
		for (Node n : board.getNodes()) {
			for (Action a : n.getResolvingActions()) {
				if (a instanceof ActionAttack)
					resolveInvalidAttack((ActionAttack) a);
			}
		}
	}
	
	private static void resolveRetreats(Board board, Map<Unit, List<Node>> retreats) {
		for (Entry<Unit, List<Node>> retreat : retreats.entrySet()) {
			List<Node> possibilities = retreat.getValue();
			possibilities.removeIf(p -> p.getGarissoned() != null);
			switch (possibilities.size()) {
				case 1:
					board.addUnit(retreat.getKey());
					retreat.getKey().setNode(possibilities.get(0));
					break;
			}
		}
		retreats.entrySet().removeIf(e -> e.getValue().size() < 2);
	}
	
	private static void resolveInvalidAttack(ActionAttack a) {
		Unit garissoned = a.getDestination().getGarissoned();
		if (garissoned == null || garissoned.getAction() == null)
			return;
		switch (garissoned.getAction().getType()) {
			case SUPPORT:
			case CONVOY:
				if (garissoned.getAction().getDestination() == a.getUnit().getNode()) { // My action is invalid
					a.getUnit().setActionHold();
				} else { // Their action is invalid
					garissoned.setActionHold();
				}
				break;
		}
	}
	
	private static boolean isResolved(Board board) {
		for (Node n : board.getNodes()) {
			if (!n.isResolved()) {
				return false;
			}
		}
		return true;
	}
	
	private static void executeAction(Action a, Node destination) {
		Unit u = a.getUnit();
		u.clearAction();
		u.setNode(destination);
	}
	
}
