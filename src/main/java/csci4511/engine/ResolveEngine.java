package csci4511.engine;

import csci4511.engine.data.Board;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.*;

import java.util.*;
import java.util.Map.Entry;

public class ResolveEngine {
	
	public static Map<Unit, List<Node>> resolve(Board board) {
		Map<Unit, List<Node>> retreats = new HashMap<>();
		verifyActions(board);
		while (!isResolved(board)) {
			resolveInvalid(board);
			resolveConflicts(retreats, board);
			resolveRetreats(board, retreats);
		}
		return retreats;
	}
	
	/** Ensures all units have an action */
	private static void verifyActions(Board board) {
		for (Unit u : board.getUnits()) {
			Action a = u.getAction();
			if (a == null) {
				assignHold(u);
			} else {
				getDestination(a).addAction(a);
			}
		}
	}
	
	private static Node getDestination(Action a) {
		Action original = getOriginalAction(a);
		switch (original.getType()) {
			default:
				assert false : "invalid original action";
			case HOLD:
				return original.getStart();
			case ATTACK:
				return ((ActionAttack) original).getDestination();
		}
	}
	
	private static Action getOriginalAction(Action a) {
		switch (a.getType()) {
			default:
				assert false : "unknown action type";
			case HOLD:		return a;
			case ATTACK:	return a;
			case SUPPORT:	return ((ActionSupport) a).getAction();
			case CONVOY:	return ((ActionConvoy) a).getAction();
		}
	}
	
	private static void resolveInvalid(Board board) {
		
	}
	
	private static void resolveRetreats(Board board, Map<Unit, List<Node>> retreats) {
		for (Entry<Unit, List<Node>> retreat : retreats.entrySet()) {
			List<Node> possibilities = retreat.getValue();
			switch (possibilities.size()) {
				case 0:
					board.removeUnit(retreat.getKey());
					break;
				case 1:
					retreat.getKey().setNode(possibilities.get(0));
					possibilities.get(0).setGarissoned(retreat.getKey());
					break;
			}
		}
		retreats.entrySet().removeIf(e -> e.getValue().size() < 2);
	}
	
	private static void resolveConflicts(Map<Unit, List<Node>> retreats, Board board) {
		for (Node n : board.getNodes()) {
			List<Action> actions = n.getResolvingActions();
			if (actions.isEmpty())
				continue;
			if (actions.size() == 1) {
				resolveUndisputed(actions.get(0), n);
			} else {
				resolveDisputed(retreats, new ArrayList<>(actions), n);
			}
		}
	}
	
	private static void resolveUndisputed(Action a, Node n) {
		switch (a.getType()) {
			case ATTACK:
				if (n.getGarissoned() != null) {
					if (n.getGarissoned().getAction() == null) { // Final move
						n.removeAction(a);
						assignHold(a.getUnit());
					}
					break;
				}
			case HOLD:
				executeAction(a, n);
				break;
		}
	}
	
	private static void resolveDisputed(Map<Unit, List<Node>> retreats, List<Action> actions, Node n) {
		Action winner = getWinningAction(actions);
		if (winner != null) {
			Unit garissoned = n.getGarissoned();
			if (garissoned != null) { // Displaced
				List<Node> possibilities = new ArrayList<>(garissoned.getMovementLocations());
				possibilities.removeIf(move -> actions.stream().anyMatch(a -> a.getStart() == move));
				possibilities.remove(n);
				retreats.put(garissoned, possibilities);
			}
			executeAction(winner, n);
		}
		for (Action a : actions) {
			n.removeAction(a);
			a.getUnit().setAction(null);
		}
	}
	
	private static Map<Action, Integer> getActionStrengths(List<Action> actions) {
		Map<Action, Integer> actionStrengths = new HashMap<>();
		for (Action a : actions) {
			switch (a.getType()) {
				case SUPPORT:
					a = ((ActionSupport) a).getAction();
				case HOLD:
				case ATTACK:
					actionStrengths.put(a, actionStrengths.getOrDefault(a, 0) + 1);
					break;
			}
		}
		return actionStrengths;
	}
	
	private static Action getWinningAction(List<Action> actions) {
		Map<Action, Integer> actionStrengths = getActionStrengths(actions);
		int max = actionStrengths.values().stream().max(Comparator.naturalOrder()).orElse(0);
		Action winner = null;
		for (Entry<Action,Integer> e : actionStrengths.entrySet()) {
			if (e.getValue() == max) {
				if (winner != null)
					return null; // Multiple "winners"
				winner = e.getKey();
			}
		}
		return winner;
	}
	
	private static void executeAction(Action a, Node destination) {
		Unit u = a.getUnit();
		// Clear old node
		u.getNode().setGarissoned(null);
		// Clear action
		u.setAction(null);
		// Set new node
		destination.removeAction(a);
		destination.setGarissoned(a.getUnit());
		u.setNode(destination);
	}
	
	private static void assignHold(Unit u) {
		Action a = new ActionHold(u);
		u.setAction(a);
		u.getNode().addAction(a);
	}
	
	private static boolean isResolved(Board board) {
		for (Unit u : board.getUnits()) {
			if (u.getAction() != null) {
				return false;
			}
		}
		for (Node n : board.getNodes()) {
			if (!n.getResolvingActions().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
}
