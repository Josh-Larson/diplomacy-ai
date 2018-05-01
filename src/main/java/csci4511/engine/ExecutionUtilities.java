package csci4511.engine;

import csci4511.algorithms.Algorithm;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;
import csci4511.engine.resolve.ResolutionEngine;

import java.util.EnumMap;
import java.util.EnumSet;

public class ExecutionUtilities {
	
	private static final Country [] COUNTRIES = Country.values();
	@SuppressWarnings("unchecked")
	private static final EnumSet<Country> [] ALLIANCES = new EnumSet[7];
	
	static {
		for (int i = 0; i < 7; i++)
			ALLIANCES[i] = EnumSet.of(COUNTRIES[i]);
	}
	
	public static EnumMap<Country, Integer> play(Board board, EnumMap<Country, Algorithm> algorithms, int maxTurns) {
		EnumMap<Country, Integer> results = new EnumMap<>(Country.class);
		while (board.getTurn() < maxTurns) {
			board.incrementTurn();
			for (int i = 0; i < 7; i++) {
				for (Action action : algorithms.get(COUNTRIES[i]).determineActions(board, COUNTRIES[i], ALLIANCES[i])) {
					action.getUnit().setAction(action);
				}
			}
			ResolutionEngine.resolve(board);
			if (board.getTurn() % 2 == 0) {
				results.clear();
				boolean completed = false;
				for (Node n : board.getNodes()) {
					if (!n.isSupply())
						continue;
					Unit unit = n.getGarissoned();
					if (unit != null)
						n.setCountry(unit.getCountry());
					Country country = n.getCountry();
					if (country != null) {
						int cur = results.compute(country, (c, prev) -> prev == null ? 1 : prev + 1);
						if (cur >= 18)
							completed = true;
					}
				}
				if (completed)
					break;
			}
		}
		return results;
	}
	
}
