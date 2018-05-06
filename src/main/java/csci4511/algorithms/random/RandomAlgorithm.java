package csci4511.algorithms.random;

import csci4511.algorithms.Algorithm;
import csci4511.engine.ActionUtilities;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;

import java.util.*;

public class RandomAlgorithm implements Algorithm {
	
	private final List<Action> predefinedActions;
	
	public RandomAlgorithm() {
		this.predefinedActions = Collections.emptyList();
	}
	
	public RandomAlgorithm(List<Action> predefinedActions) {
		this.predefinedActions = predefinedActions;
	}
	
	@Override
	public List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances) {
		List<List<Action>> possibleActions = ActionUtilities.getActions(board, alliances);
		Random random = new Random();
		
		Set<Unit> units = new HashSet<>();
		List<Action> results = new ArrayList<>(predefinedActions);
		for (Action predefined : predefinedActions) {
			units.add(predefined.getUnit());
		}
		
		actionSelectionLoop:
		while (!possibleActions.isEmpty()) {
			List<Action> possible = possibleActions.remove(random.nextInt(possibleActions.size()));
			for (Action p : possible) {
				if (units.contains(p.getUnit()))
					continue actionSelectionLoop;
			}
			
			for (Action p : possible) {
				units.add(p.getUnit());
				results.add(p);
			}
		}
		return results;
	}
}
