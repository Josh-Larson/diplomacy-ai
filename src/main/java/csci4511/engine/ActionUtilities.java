package csci4511.engine;

import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;
import csci4511.engine.data.action.ActionAttack;
import csci4511.engine.data.action.ActionHold;
import csci4511.engine.data.action.ActionSupport;

import java.util.*;
import java.util.stream.Collectors;

public class ActionUtilities {
	
	public static Set<Node> getMovementNodes(List<Unit> units) {
		Set<Node> nodes = new HashSet<>();
		for (Unit u : units) {
			nodes.addAll(u.getMovementLocations());
			nodes.add(u.getNode());
		}
		return nodes;
	}
	
	public static List<List<Action>> getActions(Unit unit, EnumSet<Country> alliances) {
		List<List<Action>> actions = new ArrayList<>(createActionsSupportable(new ActionHold(unit), alliances));
		for (Node moveNode : unit.getMovementLocations()) {
			actions.addAll(createActionsSupportable(new ActionAttack(unit, moveNode), alliances));
		}
		return actions;
	}
	
	public static int getFriendlyNearby(Node node, EnumSet<Country> alliances) {
		return (int) node.getMovements().stream().map(Node::getGarissoned).filter(Objects::nonNull).map(Unit::getCountry).filter(alliances::contains).count();
	}
	
	public static int getEnemyNearby(Node node, EnumSet<Country> alliances) {
		return (int) node.getMovements().stream().map(Node::getGarissoned).filter(Objects::nonNull).map(Unit::getCountry).filter(c -> !alliances.contains(c)).count();
	}
	
	public static List<List<Action>> createActionsSupportable(Action action, EnumSet<Country> alliances) {
		List<Unit> supportable = getFriendlySupportable(action.getUnit(), action.getDestination(), alliances);
		List<List<Action>> actions = new ArrayList<>();
		createRecursiveSupport(action, actions, Collections.singletonList(action), supportable, supportable.size(), 0);
		return actions;
	}
	
	static List<Unit> getFriendlySupportable(Unit core, Node node, EnumSet<Country> alliances) {
		return node.getMovements().stream().map(Node::getGarissoned).filter(Objects::nonNull).filter(u -> u != core).filter(u -> alliances.contains(u.getCountry())).collect(Collectors.toList());
	}
	
	private static void createRecursiveSupport(Action action, List<List<Action>> actions, List<Action> currentActions, List<Unit> supportable, int remainingDepth, int startIndex) {
		if (remainingDepth <= 0)
			return;
		supportLoop:
		for (int i = startIndex; i < supportable.size(); i++) {
			Unit unit = supportable.get(i);
			for (Action supportAction : currentActions) {
				if (supportAction.getUnit() == unit)
					continue supportLoop;
			}
			List<Action> recurseActions = new ArrayList<>(currentActions);
			recurseActions.add(new ActionSupport(unit, action));
			actions.add(recurseActions);
			createRecursiveSupport(action, actions, recurseActions, supportable, remainingDepth - 1, i + 1);
		}
	}
	
}
