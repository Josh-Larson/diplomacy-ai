package csci4511.engine.data;

import csci4511.engine.data.node.CoastalNode;
import csci4511.engine.data.node.StandardNode;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class Board {
	
	private final Map<String, StandardNode> nodes;
	private final EnumMap<Country, CountryState> countryState;
	private final List<Unit> units;
	private int turn;
	
	public Board() {
		this.nodes = new HashMap<>();
		this.countryState = new EnumMap<>(Country.class);
		this.units = new ArrayList<>();
		this.turn = 0;
		for (Country country : Country.values()) {
			countryState.put(country, new CountryState());
		}
	}
	
	public Board(Board copy) {
		this.nodes = new HashMap<>();
		this.countryState = new EnumMap<>(Country.class);
		this.units = new ArrayList<>();
		this.turn = copy.turn;
		for (Country country : Country.values()) {
			countryState.put(country, new CountryState());
		}
		for (StandardNode copyNode : copy.nodes.values()) {
			addNode(new StandardNode(copyNode));
		}
		for (Unit copyUnit : copy.units) {
			Unit myUnit = new Unit(copyUnit);
			myUnit.setNode(getNode(copyUnit.getNode().getName()));
			addUnit(myUnit);
		}
		for (Node copyNode : copy.nodes.values()) {
			Node myNode = getNode(copyNode.getName());
			for (Node army : copyNode.getArmyMovements()) {
				myNode.addArmyMovement(getNode(army.getName()));
			}
			for (CoastalNode copyCoast : copyNode.getCoasts()) {
				CoastalNode myCoast = myNode.getCoast(copyCoast.getCoastName());
				for (CoastalNode fleet : copyCoast.getFleetMovements()) {
					myCoast.addFleetMovement(getCoastalNode(fleet.getName()));
				}
			}
		}
	}
	
	public void incrementTurn() {
		turn++;
	}
	
	public int getTurn() {
		return turn;
	}
	
	@Nonnull
	public Collection<StandardNode> getNodes() {
		return nodes.values();
	}
	
	@Nonnull
	public Collection<StandardNode> getHomeNodes(Country country) {
		return countryState.get(country).getHomeNodes();
	}
	
	public int getUnitCount(Country country) {
		return countryState.get(country).getUnitCount();
	}
	
	public int getSupplyCount(Country country) {
		return countryState.get(country).getSupplyCount();
	}
	
	public Country hasWinner() {
		for (Entry<Country, CountryState> state : countryState.entrySet()) {
			if (state.getValue().getSupplyCount() >= 18)
				return state.getKey();
		}
		return null;
	}
	
	public Node getNode(String name) {
		if (name.indexOf('-') != -1)
			return getCoastalNode(name);
		return nodes.get(name);
	}
	
	public CoastalNode getCoastalNode(String name) {
		int dash = name.indexOf('-');
		if (dash == -1)
			return nodes.get(name).getCoast("");
		return nodes.get(name.substring(0, dash)).getCoast(name.substring(dash+1));
	}
	
	@Nonnull
	public List<Unit> getUnits() {
		return units;
	}
	
	public List<Unit> getUnits(Country country) {
		List<Unit> countryUnits = new ArrayList<>();
		for (Unit unit : units) {
			if (unit.getCountry() == country)
				countryUnits.add(unit);
		}
		return countryUnits;
	}
	
	@Nonnull
	public List<Unit> getUnits(EnumSet<Country> alliances) {
		List<Unit> allianceUnits = new ArrayList<>();
		for (Unit unit : units) {
			if (alliances.contains(unit.getCountry()))
				allianceUnits.add(unit);
		}
		return allianceUnits;
	}
	
	public void addNode(@Nonnull StandardNode node) {
		Node prev = this.nodes.putIfAbsent(node.getName(), node);
		if (prev != null)
			throw new IllegalArgumentException("Node already in board! " + prev);
		Country home = node.getHomeCountry();
		if (home != null) {
			CountryState state = countryState.get(home);
			state.addHomeNode(node);
			if (node.isSupply())
				state.incrementSupply();
		}
	}
	
	public void addUnit(@Nonnull Unit unit) {
		this.units.add(unit);
		countryState.get(unit.getCountry()).incrementUnit();
	}
	
	public void removeUnit(@Nonnull Unit unit) {
		this.units.remove(unit);
		countryState.get(unit.getCountry()).decrementUnit();
		unit.getNode().setGarissoned(null);
	}
	
	public void updateSupply() {
		for (Node node : nodes.values()) {
			Unit garissoned = node.getGarissoned();
			if (node.isSupply() && garissoned != null && garissoned.getCountry() != node.getCountry()) {
				countryState.get(garissoned.getCountry()).incrementSupply();
				if (node.getCountry() != null)
					countryState.get(node.getCountry()).decrementSupply();
				node.setCountry(garissoned.getCountry());
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Board && ((Board) o).nodes.equals(nodes) && ((Board) o).units.equals(units);
	}
	
	@Nonnull
	public static Board loadFromStream(InputStream is) {
		int lineNum = 1;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			Board board = new Board();
			String [] nodes = reader.readLine().replace(' ', ';').split(";");
			for (String node : nodes) {
				if (node.isEmpty())
					continue;
				String [] parts = node.split(",", 3);
				if (parts.length != 3)
					throw new RuntimeException("Invalid node definition line");
				board.addNode(new StandardNode(parts[0], parts[1].equals("*"), parts[2].isEmpty() ? null : Country.valueOf(parts[2])));
			}
			
			String line;
			boolean army = true;
			while ((line = reader.readLine()) != null) {
				lineNum++;
				if (line.isEmpty() || line.startsWith("#"))
					continue;
				line = line.replace(" ", "").replace("\t", "");
				if (line.equalsIgnoreCase("army")) {
					army = true;
					continue;
				} else if (line.equalsIgnoreCase("fleet")) {
					army = false;
					continue;
				}
				
				String [] parts = line.split(",", 2);
				if (parts.length != 2)
					throw new RuntimeException("Invalid connection line");
				
				if (army) {
					board.getNode(parts[0]).addArmyMovement(board.getNode(parts[1]));
				} else {
					board.getCoastalNode(parts[0]).addFleetMovement(board.getCoastalNode(parts[1]));
				}
			}
			return board;
		} catch (RuntimeException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Line: " + lineNum, e);
		}
	}
	
	private static class CountryState {
		
		private final List<StandardNode> homeNodes;
		private final AtomicInteger unitCount;
		private final AtomicInteger supplyCount;
		
		public CountryState() {
			this.homeNodes = new ArrayList<>();
			this.unitCount = new AtomicInteger(0);
			this.supplyCount = new AtomicInteger(0);
		}
		
		public void addHomeNode(StandardNode node) {
			homeNodes.add(node);
		}
		
		public void incrementUnit() {
			unitCount.incrementAndGet();
		}
		
		public void decrementUnit() {
			unitCount.decrementAndGet();
		}
		
		public void incrementSupply() {
			supplyCount.incrementAndGet();
		}
		
		public void decrementSupply() {
			supplyCount.decrementAndGet();
		}
		
		public Collection<StandardNode> getHomeNodes() {
			return homeNodes;
		}
		
		public int getUnitCount() {
			return unitCount.get();
		}
		
		public int getSupplyCount() {
			return supplyCount.get();
		}
		
	}
	
}
