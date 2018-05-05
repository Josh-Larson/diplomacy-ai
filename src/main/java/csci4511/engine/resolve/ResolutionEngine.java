package csci4511.engine.resolve;

import csci4511.engine.data.Board;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResolutionEngine {
	
	private final Map<Node, List<Action>> resolvingActions;
	private final Map<Unit, List<Node>> retreatPossibilities;
	private final Map<Unit, Action> unitActions;
	
	public ResolutionEngine() {
		this.resolvingActions = new HashMap<>();
		this.retreatPossibilities = new HashMap<>();
		this.unitActions = new HashMap<>();
	}
	
	public void resolve(Board board, Collection<Action> actions) {
		this.resolvingActions.clear();
		this.retreatPossibilities.clear();
		this.unitActions.clear();
		for (Action a : actions) {
			Node dst = a.getDestination();
			this.resolvingActions.computeIfAbsent(dst, n -> new CopyOnWriteArrayList<>()).add(a);
			this.unitActions.put(a.getUnit(), a);
		}
		for (Unit u : board.getUnits()) {
			unitActions.computeIfAbsent(u, ActionHold::new);
		}
		resolveInvalid(board);
		for (int i = 0; i < 5; i++) {
			resolve(board);
			retreatPossibilities.keySet().forEach(board::removeUnit);
			resolveRetreats(board);
			unitActions.clear();
		}
	}
	
	private void resolveSameEdge(Node src) {
		if (src.getGarissoned() == null)
			return;
		Action srcAction = getUnitAction(src.getGarissoned());
		if (srcAction == null || srcAction.getType() != ActionType.ATTACK)
			return;
		
		Node dst = srcAction.getDestination();
		if (dst.getGarissoned() == null)
			return;
		Action dstAction = getUnitAction(dst.getGarissoned());
		if (dstAction == null || dstAction.getType() != ActionType.ATTACK)
			return;
		
		if (dstAction.getDestination() == src) {
			int srcStrength = 0, dstStrength = 0;
			List<Action> actions = new ArrayList<>();
			actions.add(srcAction);
			actions.add(dstAction);
			for (Action srcSupportAction : getResolvingActions(dst)) {
				if (srcSupportAction instanceof ActionSupport && ((ActionSupport) srcSupportAction).getAction() == srcAction) {
					srcStrength++;
					actions.add(srcSupportAction);
				}
			}
			for (Action dstSupportAction : getResolvingActions(src)) {
				if (dstSupportAction instanceof ActionSupport && ((ActionSupport) dstSupportAction).getAction() == dstAction) {
					dstStrength++;
					actions.add(dstSupportAction);
				}
			}
			boolean validStandoff = src.getGarissoned().getCountry() != dst.getGarissoned().getCountry();
			if (validStandoff) {
				if (srcStrength > dstStrength) {
					executeAction(srcAction, dst);
				} else if (srcStrength < dstStrength) {
					executeAction(dstAction, src);
				}
			}
			resolvingActions.get(src).removeAll(actions);
			resolvingActions.get(dst).removeAll(actions);
			unitActions.values().removeAll(actions);
		}
	}
	
	private Action getWinningAction(List<Action> actions) {
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
	
	private void resolve(Board board) {
		for (Node n : board.getNodes()) {
			List<Action> actions = getResolvingActions(n);
			
			resolveSameEdge(n);
			switch (actions.size()) {
				case 0:
					break;
				case 1:
					resolveUndisputed(actions.get(0), n);
					break;
				default:
					resolveDisputed(actions, n);
					break;
			}
		}
	}
	
	private void resolveUndisputed(Action a, Node n) {
		switch (a.getType()) {
			case ATTACK: {
				Unit garissoned = n.getGarissoned();
				if (garissoned != null) {
					if (!unitActions.containsKey(garissoned)) { // Final move
						setActionHold(a.getUnit());
					}
					break;
				}
			}
			case HOLD:
				executeAction(a, n);
				break;
		}
	}
	
	private void resolveDisputed(List<Action> actions, Node n) {
		Action winner = getWinningAction(actions);
		if (winner != null) {
			Unit garissoned = n.getGarissoned();
			boolean validDisplacement = garissoned == null || garissoned.getCountry() != winner.getUnit().getCountry();
			if (validDisplacement) {
				if (garissoned != null) {
					List<Node> possibilities = new ArrayList<>(garissoned.getMovementLocations());
					possibilities.remove(n);
					possibilities.remove(winner.getStart());
					retreatPossibilities.put(garissoned, possibilities);
				}
				executeAction(winner, n);
			}
		}
		unitActions.values().removeAll(actions);
		resolvingActions.remove(n);
	}
	
	private void resolveInvalid(Board board) {
		// Support/Convoy based
		for (Node n : board.getNodes()) {
			for (Action a : getResolvingActions(n)) {
				if (a instanceof ActionAttack)
					resolveInvalidAttack((ActionAttack) a);
			}
		}
	}
	
	private void resolveRetreats(Board board) {
		for (Entry<Unit, List<Node>> retreat : retreatPossibilities.entrySet()) {
			List<Node> possibilities = retreat.getValue();
			possibilities.removeIf(p -> p.getGarissoned() != null);
			switch (possibilities.size()) {
				case 1:
					board.addUnit(retreat.getKey());
					retreat.getKey().setNode(possibilities.get(0));
					break;
			}
		}
		retreatPossibilities.entrySet().removeIf(e -> e.getValue().size() < 2);
	}
	
	private void resolveInvalidAttack(ActionAttack a) {
		Unit garissoned = a.getDestination().getGarissoned();
		if (garissoned == null)
			return;
		Action garissonedAction = getUnitAction(garissoned);
		if (garissonedAction == null)
			return;
		switch (garissonedAction.getType()) {
			case SUPPORT:
			case CONVOY:
				if (garissonedAction.getDestination() == a.getUnit().getNode()) { // My action is invalid
					setActionHold(a.getUnit());
				} else { // Their action is invalid
					setActionHold(garissoned);
				}
				break;
		}
	}
	
	private void executeAction(Action a, Node destination) {
		Unit u = a.getUnit();
		u.setNode(destination);
		unitActions.remove(u);
	}
	
	private List<Action> getResolvingActions(Node n) {
		return resolvingActions.getOrDefault(n, Collections.emptyList());
	}
	
	private Action getUnitAction(Unit u) {
		return unitActions.get(u);
	}
	
	private void setActionHold(Unit u) {
		Action prevAction = unitActions.put(u, new ActionHold(u));
		if (prevAction != null)
			resolvingActions.getOrDefault(prevAction.getDestination(), Collections.emptyList()).remove(prevAction);
	}
	
}
