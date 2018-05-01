package csci4511.algorithms;

import csci4511.engine.ActionUtilities;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;
import csci4511.engine.data.action.ActionAttack;
import csci4511.engine.data.action.ActionHold;
import csci4511.engine.data.action.ActionType;
import me.joshlarson.jlcommon.log.Log;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleHeuristic implements Algorithm {
	
	private Board board;
	private Country country;
	private EnumSet<Country> alliances;
	private List<Unit> units;
	private List<Action> actions;
	
	public SimpleHeuristic() {
		this.board = null;
		this.country = null;
		this.alliances = null;
		this.units = null;
		this.actions = null;
	}
	
	public List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances) {
		init(board, country, alliances);
		units.sort(Comparator.comparingInt(u -> u.getMovementLocations().size()));
		Set<Node> nodes = ActionUtilities.getMovementNodes(units);
		Log.t("Units: %s", units);
		
		for (int i = 0; i < 10; i++) {
			List<Action> selectedAction = null;
			double currentScore = Double.MIN_VALUE;
			for (Node node : nodes) {
				List<Action> actionSequence = addNextAction(node);
				if (actionSequence == null)
					continue;
				double score = evaluatePossibility(actionSequence);
				if (score > currentScore) {
					selectedAction = actionSequence;
					currentScore = score;
				}
			}
			if (selectedAction != null) {
				nodes.remove(selectedAction.get(0).getDestination());
				commitAction(selectedAction);
			}
		}
		Log.t("Actions: %s", actions);
		return actions;
	}
	
	private void init(Board board, Country country, EnumSet<Country> alliances) {
		this.board = board;
		this.country = country;
		this.alliances = alliances;
		this.units = board.getUnits(alliances);
		this.actions = new ArrayList<>();
	}
	
	private void commitAction(List<Action> actions) {
		this.actions.addAll(actions);
		for (Action action : actions) {
			units.remove(action.getUnit());
		}
	}
	
	private List<Action> addNextAction(Node node) {
		List<Action> actionSequence = null;
		double currentScore = Double.MIN_VALUE;
		for (Unit unit : units) {
			List<Action> actions = addNextAction(node, unit);
			if (actions == null)
				continue;
			double score = evaluatePossibility(actions);
			if (score > currentScore) {
				currentScore = score;
				actionSequence = actions;
			}
		}
		return actionSequence;
	}
	
	private List<Action> addNextAction(Node node, Unit unit) {
		int strength = ActionUtilities.getEnemyNearby(node, alliances);
		if (strength <= 0)
			strength = 1; // Min strength
		if (!unit.getMovementLocations().contains(node))
			return null;
		boolean maintain = node == unit.getNode();
		List<List<Action>> possibilities = ActionUtilities.createActionsSupportable(maintain ? new ActionHold(unit) : new ActionAttack(unit, node), alliances, maintain ? strength : strength+1);
		List<Action> currentPossibility = null;
		double currentScore = Double.MIN_VALUE;
		possibilityLoop:
		for (List<Action> testPossibility : possibilities) {
			for (Action possAction : testPossibility) {
				if (!units.contains(possAction.getUnit()))
					continue possibilityLoop;
			}
			double score = evaluatePossibility(testPossibility);
			if (score > currentScore) {
				currentPossibility = testPossibility;
				currentScore = score;
			}
		}
		return currentPossibility;
	}
	
	private double evaluatePossibility(List<Action> possibility) {
		Action coreAction = possibility.get(0);
		Node destination = coreAction.getDestination();
		int enemies = ActionUtilities.getEnemyNearby(destination, alliances);
		int coreScore;
		if (board.getTurn() % 2 == 0) {
			if (destination.isSupply())
				coreScore = 5;
			else
				coreScore = destination.getMovements().size();
		} else {
			coreScore = countNearbySupplyCenters(destination);
		}
		if (coreScore > 10)
			coreScore = 10;
		if (possibility.size() > enemies)
			return coreScore;
		return ((possibility.size() + enemies) / (double) enemies) * coreScore;
	}
	
	private int countNearbySupplyCenters(Node n) {
		return (int) n.getMovements().stream().flatMap(near -> near.getMovements().stream()).filter(Node::isSupply).count();
	}
	
}
