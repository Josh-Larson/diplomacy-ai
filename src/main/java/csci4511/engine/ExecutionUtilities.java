package csci4511.engine;

import csci4511.algorithms.Algorithm;
import csci4511.algorithms.random.RandomAlgorithm;
import csci4511.engine.data.*;
import csci4511.engine.data.action.Action;
import csci4511.engine.resolve.ResolutionEngine;

import java.util.*;
import java.util.function.Function;

public class ExecutionUtilities {
	
	private static final RandomAlgorithm RANDOM_ALGORITHM = new RandomAlgorithm();
	private static final Country [] COUNTRIES = Country.values();
	@SuppressWarnings("unchecked")
	private static final EnumSet<Country> [] ALLIANCES = new EnumSet[7];
	
	static {
		for (int i = 0; i < 7; i++)
			ALLIANCES[i] = EnumSet.of(COUNTRIES[i]);
	}
	
	public static double playRandom(Board board, List<Action> initialActions, Country country, int maxTurns, int games) {
		ResolutionEngine engine = new ResolutionEngine();
		double score = 0;
		RandomAlgorithm initialRandom = new RandomAlgorithm(initialActions);
		for (int g = 0; g < games; g++) {
			Board currentBoard = new Board(board);
			for (int t = 0; t < maxTurns; t++) {
				if (currentBoard.hasWinner() != null)
					break;
				if (t == 0)
					playIteration(engine, currentBoard, c -> c == country ? initialRandom : RANDOM_ALGORITHM);
				else
					playIteration(engine, currentBoard, c -> RANDOM_ALGORITHM);
			}
			score += currentBoard.getSupplyCount(country) / 18.0;
		}
		return score / games;
	}
	
	public static double play(Board board, Function<Country, Algorithm> algorithms, Country country, int maxTurns, int games) {
		ResolutionEngine engine = new ResolutionEngine();
		double score = 0;
		for (int g = 0; g < games; g++) {
			Board currentBoard = new Board(board);
			for (int t = 0; t < maxTurns; t++) {
				if (currentBoard.hasWinner() != null)
					break;
				playIteration(engine, currentBoard, algorithms);
			}
			score += currentBoard.getSupplyCount(country) / 18.0;
		}
		return score / games;
	}
	
	public static EnumMap<Country, Integer> play(Board board, Function<Country, Algorithm> algorithms, int maxTurns) {
		EnumMap<Country, Integer> results = new EnumMap<>(Country.class);
		ResolutionEngine engine = new ResolutionEngine();
		while (board.getTurn() < maxTurns) {
			playIteration(engine, board, algorithms);
			if (board.getTurn() % 2 != 0)
				continue;
			
			boolean winner = false;
			for (Country country : COUNTRIES) {
				if (board.getSupplyCount(country) >= 18)
					winner = true;
			}
			if (winner) {
				for (Country country : COUNTRIES) {
					results.put(country, board.getSupplyCount(country));
				}
				return results;
			}
		}
		return results;
	}
	
	public static void playIteration(Board board, Function<Country, Algorithm> algorithms) {
		playIteration(new ResolutionEngine(), board, algorithms);
	}
	
	public static void playIteration(ResolutionEngine engine, Board board, Function<Country, Algorithm> algorithms) {
		board.incrementTurn();
		List<Action> actions = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			actions.addAll(algorithms.apply(COUNTRIES[i]).determineActions(board, COUNTRIES[i], ALLIANCES[i]));
		}
		engine.resolve(board, actions);
		if (board.getTurn() % 2 == 0) {
			addUnits(board);
		}
	}
	
	private static void addUnits(Board board) {
		board.updateSupply();
		for (Country country : COUNTRIES) {
			int supplyCenters = board.getSupplyCount(country);
			int unitCount = board.getUnitCount(country);
			
			int add = supplyCenters - unitCount;
			if (add < 0) {
				for (Unit u : new ArrayList<>(board.getUnits(country))) {
					if (u.getCountry() == country && add < 0) {
						board.removeUnit(u);
						add++;
					}
				}
			} else {
				for (Node n : board.getHomeNodes(country)) {
					if (add <= 0)
						break;
					if (n.isSupply() && n.getCountry() == country && n.getGarissoned() == null) {
						int armyMovements = n.getArmyMovements().size();
						int fleetMovements = n.getFleetMovements().size();
						Unit u;
						if (armyMovements > fleetMovements)
							u = new Unit(UnitType.ARMY, country);
						else
							u = new Unit(UnitType.FLEET, country);
						u.setNode(n);
						board.addUnit(u);
						add--;
					}
				}
			}
		}
	}
	
}
