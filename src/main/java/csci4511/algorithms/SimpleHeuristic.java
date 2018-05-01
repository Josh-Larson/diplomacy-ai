package csci4511.algorithms;

import csci4511.engine.ActionUtilities;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;
import csci4511.engine.data.action.ActionAttack;
import csci4511.engine.data.action.ActionHold;
import me.joshlarson.jlcommon.log.Log;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleHeuristic implements Algorithm {
	
	public SimpleHeuristic() {
		
	}
	
	public List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances) {
		List<Unit> units = board.getUnits(alliances);
		units.sort(Comparator.comparingInt(u -> u.getMovementLocations().size()));
		Set<Node> possibilities = ActionUtilities.getMovementNodes(units);
		Queue<Node> nodes = possibilities.stream().sorted(Comparator.comparingInt(n -> -scoreNode(n, country, alliances))).collect(Collectors.toCollection(ArrayDeque::new));
		Log.t("Possible Movements: %s", possibilities);
		Log.t("Preferred Nodes: %s", nodes);
		
		List<Action> actions = new ArrayList<>();
		Log.t("Units: %s", units);
		while (!nodes.isEmpty() && !units.isEmpty()) {
			addNextAction(actions, units, nodes.poll(), country, alliances);
		}
		Log.t("Actions: %s", actions);
		return actions;
	}
	
	private void addNextAction(List<Action> actions, List<Unit> units, Node node, Country country, EnumSet<Country> alliances) {
		if (node.getGarissoned() != null && node.getGarissoned().getCountry() == country)
			if (!addNextAction(actions, units, node, node.getGarissoned(), country, alliances))
				return;
		for (Unit unit : units) {
			if (!addNextAction(actions, units, node, unit, country, alliances))
				break;
		}
	}
	
	private boolean addNextAction(List<Action> actions, List<Unit> units, Node node, Unit unit, Country country, EnumSet<Country> alliances) {
		int strength = ActionUtilities.getEnemyNearby(node, alliances);
		if (strength <= 0)
			strength = 1; // Min strength
		if (!unit.getMovementLocations().contains(node))
			return true;
		boolean maintain = node == unit.getNode();
		List<List<Action>> possibilities = ActionUtilities.createActionsSupportable(maintain ? new ActionHold(unit) : new ActionAttack(unit, node), alliances, maintain ? strength : strength+1);
		possibilityLoop:
		for (List<Action> possibility : possibilities) {
			for (Action possAction : possibility) {
				if (!units.contains(possAction.getUnit()))
					continue possibilityLoop;
			}
			// All units allowed to move
			actions.addAll(possibility);
			for (Action possAction : possibility) {
				units.remove(possAction.getUnit());
			}
			return false;
		}
		return true;
	}
	
	private static int scoreNode(Node a, Country country, EnumSet<Country> alliances) {
		int score = 0;
		int enemies = ActionUtilities.getEnemyNearby(a, alliances);
		int friendly = ActionUtilities.getFriendlyNearby(a, alliances);
		if (a.isSupply())
			score += 10;
		score += a.getMovements().size() * 2;
		if (friendly >= enemies && enemies > 0)
			score += 10;
//		score += (enemies - friendly) * 2;
//		if (a.getHomeCountry() == country && enemies > 0)
//			score += 10;
		return score;
	}
	
}
