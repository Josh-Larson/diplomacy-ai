package csci4511.algorithms;

import csci4511.engine.ActionUtilities;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.action.Action;
import me.joshlarson.jlcommon.log.Log;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleHeuristic implements Algorithm {
	
	public SimpleHeuristic() {
		
	}
	
	public List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances) {
		Set<Node> possibilities = ActionUtilities.getMovementNodes(board.getUnits(alliances));
		Log.t("Possible Movements: %s", possibilities);
		Log.t("Preferred Nodes: %s", possibilities.stream().sorted((a, b) -> compareFallNodes(a, b, country, alliances)).collect(Collectors.toList()));
		return null;
	}
	
	private static int compareFallNodes(Node a, Node b, Country country, EnumSet<Country> alliances) {
		return Comparator.comparing(Node::isSupply)
				.thenComparingInt((Node n) -> n.getMovements().size())
				.thenComparing((Node n) -> ActionUtilities.getEnemyNearby(n, alliances))
				.thenComparing(n -> n.getCountry() == country)
				.reversed()
				.compare(a, b);
	}
	
}
