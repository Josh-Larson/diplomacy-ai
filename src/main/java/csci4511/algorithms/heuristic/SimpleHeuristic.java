package csci4511.algorithms.heuristic;

import csci4511.algorithms.Algorithm;
import csci4511.engine.ActionUtilities;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.Action;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleHeuristic implements Algorithm {
	
	private static final int INPUT_DATA = 16;
	private static final int IMPLEMENTATIONS = 3;
	
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
	
	private static double binary(boolean b) {
		return b ? 1 : -1;
	}
	
	private static int countNearbySupplyCenters(Node n) {
		int count = 0;
		for (Node move : n.getMovements()) {
			if (move.isSupply())
				count++;
		}
		return count;
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
			
			List<List<Action>> actionList = ActionUtilities.getActions(board, alliances);
			for (int i = 0; i < 10  && !units.isEmpty(); i++) {
				List<Action> selectedAction = null;
				double currentScore = Double.MIN_VALUE;
				actionSequenceLoop:
				for (List<Action> actionSequence : actionList) {
					for (Action action : actionSequence) {
						if (!units.contains(action.getUnit()) || !nodes.contains(action.getDestination()))
							continue actionSequenceLoop;
					}
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
		
		private void commitAction(List<Action> actions) {
			this.actions.addAll(actions);
			for (Action action : actions) {
				units.remove(action.getUnit());
			}
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
			inputData[2] = binary(possibility.size() >= ActionUtilities.getEnemyNearby(destination, alliances));
			inputData[3] = binary(destination.getMovements().size() >= source.getMovements().size());
			inputData[4] = binary(countNearbySupplyCenters(destination) >= countNearbySupplyCenters(source));
			inputData[5] = binary(fallTurn);
			inputData[6] = binary(destination.getGarissoned() != null);
			inputData[7] = binary(destination.getCountry() != country);
			inputData[8] = binary(destination.getCountry() != country && fallTurn);
			inputData[9] = binary(source.getCountry() != country);
			inputData[10] = binary(source.getCountry() != country && fallTurn);
			inputData[11] = binary(source == destination);
			inputData[12] = binary(destination.isSupply() && fallTurn);
			inputData[13] = binary(source.isSupply() && fallTurn);
			inputData[14] = binary(interruptable);
			inputData[15] = Math.random();
			return network.calculate(inputData);
		}
		
	}
	
}
