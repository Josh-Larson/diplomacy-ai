package csci4511.algorithms.heuristic;

import csci4511.algorithms.Algorithm;
import csci4511.engine.ActionUtilities;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleHeuristic implements Algorithm {
	
	private static final int INPUT_DATA = 14;
	private static final int IMPLEMENTATIONS = 6;
	
	private final ActionSelectionNetwork network;
	private final SimpleHeuristicImplementation [] impls;
	private final AtomicInteger index;
	
	public SimpleHeuristic() {
		this.network = new ActionSelectionNetwork();
		this.impls = new SimpleHeuristicImplementation[IMPLEMENTATIONS];
		this.index = new AtomicInteger(0);
		for (int i = 0; i < IMPLEMENTATIONS; i++)
			impls[i] = new SimpleHeuristicImplementation(network);
	}
	
	public SimpleHeuristic(SimpleHeuristic copy) {
		this.network = new ActionSelectionNetwork(copy.network);
		this.impls = new SimpleHeuristicImplementation[IMPLEMENTATIONS];
		this.index = new AtomicInteger(0);
		for (int i = 0; i < IMPLEMENTATIONS; i++)
			impls[i] = new SimpleHeuristicImplementation(network);
	}
	
	public SimpleHeuristic(double [] weights) {
		this();
		network.setWeights(weights);
	}
	
	public SimpleHeuristic(SimpleHeuristic dad, SimpleHeuristic mom) {
		this.network = new ActionSelectionNetwork(dad.network, mom.network);
		this.impls = new SimpleHeuristicImplementation[IMPLEMENTATIONS];
		this.index = new AtomicInteger(0);
		for (int i = 0; i < IMPLEMENTATIONS; i++)
			impls[i] = new SimpleHeuristicImplementation(network);
	}
	
	public List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances) {
		return impls[index.getAndUpdate(i -> (i+1)%IMPLEMENTATIONS)].determineActions(board, country, alliances);
	}
	
	public double [] getWeights() {
		return network.getWeights();
	}
	
	@Override
	public String toString() {
		return network.toString();
	}
	
	private static class SimpleHeuristicImplementation {
		
		private final ActionSelectionNetwork network;
		private final double [] inputData;
		
		private Board board;
		private Country country;
		private EnumSet<Country> alliances;
		private List<Unit> units;
		private List<Action> actions;
		private Set<Node> nodes;
		
		public SimpleHeuristicImplementation(ActionSelectionNetwork network) {
			this.network = network;
			this.inputData = new double[INPUT_DATA];
		}
		
		public synchronized List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances) {
			init(board, country, alliances);
			
			List<List<Action>> possibleActions = ActionUtilities.getActions(board, alliances);
			possibleActions.sort(Comparator.comparingDouble(this::evaluatePossibility).reversed());
			Random random = new Random();
			double currentLikelihood = 0.4;
			for (int i = 0; i < 10 && !units.isEmpty(); i++) {
				actionSequenceLoop:
				for (List<Action> actionSequence : possibleActions) {
					for (Action action : actionSequence) {
						if (!units.contains(action.getUnit()) || !nodes.contains(action.getDestination()))
							continue actionSequenceLoop;
					}
					if (random.nextDouble() <= 1 - currentLikelihood) {
						nodes.remove(actionSequence.get(0).getDestination());
						actions.addAll(actionSequence);
						for (Action action : actionSequence) {
							units.remove(action.getUnit());
						}
						break;
					}
					currentLikelihood *= currentLikelihood;
				}
			}
			return actions;
		}
		
		private void init(Board board, Country country, EnumSet<Country> alliances) {
			this.board = board;
			this.country = country;
			this.alliances = alliances;
			this.units = board.getUnits(alliances);
			this.nodes = ActionUtilities.getMovementNodes(units);
			this.actions = new ArrayList<>();
		}
		
		private double evaluatePossibility(List<Action> possibility) {
			Action coreAction = possibility.get(0);
			Node source = coreAction.getStart();
			Node destination = coreAction.getDestination();
			boolean fallTurn = board.getTurn() % 2 == 0;
			boolean interruptable = false;
			for (int i = 1; i < possibility.size(); i++) {
				Action p = possibility.get(i);
				switch (p.getType()) {
					case CONVOY:
						if (ActionUtilities.getEnemyNearby(p.getStart(), alliances) > 1)
							interruptable = true;
						break;
					case SUPPORT:
						if (ActionUtilities.getEnemyNearby(p.getStart(), alliances) > 0)
							interruptable = true;
						break;
				}
			}
			inputData[0] = binary(destination.isSupply());
			inputData[1] = binary(source.isSupply());
			inputData[2] = compare(possibility.size(), ActionUtilities.getEnemyNearby(destination, alliances));
			inputData[3] = compare(destination.getMovements().size(), source.getMovements().size());
			inputData[4] = compare(countNearbySupplyCenters(destination), countNearbySupplyCenters(source));
			inputData[5] = binary(interruptable);
			inputData[6] = binary(destination.getGarissoned() != null);
			inputData[7] = binary(destination.getCountry() != country);
			inputData[8] = binary(destination.getCountry() != country && fallTurn);
			inputData[9] = binary(source.getCountry() != country);
			inputData[10] = binary(source.getCountry() != country && fallTurn);
			inputData[11] = binary(source == destination);
			inputData[12] = binary(destination.isSupply() && destination.getCountry() != country && fallTurn);
			inputData[13] = binary(source.isSupply() && source.getCountry() != country && fallTurn);
			return network.calculate(inputData);
		}
		
		private static double binary(boolean b) {
			return b ? 1 : -1;
		}
		
		private static double compare(int x, int y) {
			return Integer.compare(x, y);
		}
		
		private static int countNearbySupplyCenters(Node n) {
			int count = 0;
			for (Node move : n.getMovements()) {
				if (move.isSupply())
					count++;
			}
			return count;
		}
		
	}
	
}
