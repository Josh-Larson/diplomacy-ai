package csci4511.engine;

import csci4511.algorithms.Algorithm;
import csci4511.engine.data.*;
import csci4511.engine.data.action.Action;
import csci4511.engine.resolve.ResolutionEngine;
import me.joshlarson.jlcommon.log.Log;

import java.util.*;

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
		EnumMap<Country, List<Node>> homeSupply = new EnumMap<>(Country.class);
		for (Country country : COUNTRIES) {
			List<Node> home = new ArrayList<>();
			for (Node n : board.getNodes()) {
				if (n.isSupply() && n.getHomeCountry() == country)
					home.add(n);
			}
			homeSupply.put(country, home);
		}
		Random random = new Random();
		while (board.getTurn() < maxTurns) {
			playIteration(board, algorithms);
			if (board.getTurn() % 2 != 0)
				continue;
			
			results.clear();
			boolean completed = false;
			for (Node n : board.getNodes()) {
				if (!n.isSupply() || n.getCountry() == null)
					continue;
				int cur = results.compute(n.getCountry(), (c, prev) -> prev == null ? 1 : prev + 1);
				if (cur >= 18)
					completed = true;
			}
			if (completed)
				break;
		}
		return results;
	}
	
	public static void playIteration(Board board, EnumMap<Country, Algorithm> algorithms) {
		board.incrementTurn();
			for (int i = 0; i < 7; i++) {
				for (Action action : algorithms.get(COUNTRIES[i]).determineActions(board, COUNTRIES[i], ALLIANCES[i])) {
					action.getUnit().setAction(action);
				}
			}
			ResolutionEngine.resolve(board);
			if (board.getTurn() % 2 == 0) {
				addUnits(board);
			}
	}
	
	private static void addUnits(Board board) {
		Random random = new Random();
		board.updateSupply();
		for (Country country : COUNTRIES) {
			int supplyCenters = board.getSupplyCount(country);
			int unitCount = board.getUnitCount(country);
			
			int add = supplyCenters - unitCount;
			if (add < 0) {
				for (Iterator<Unit> it = board.getUnits().iterator(); it.hasNext() && add < 0; ) {
					Unit u = it.next();
					if (u.getCountry() == country) {
						it.remove();
						add++;
					}
				}
			} else {
				for (Node n : board.getHomeNodes(country)) {
					if (add <= 0)
						break;
					if (n.isSupply() && n.getCountry() == country && n.getGarissoned() == null) {
						Unit u = new Unit(random.nextBoolean() ? UnitType.ARMY : UnitType.FLEET, country);
						u.setNode(n);
						board.addUnit(u);
						add--;
					}
				}
			}
		}
	}
	
}
