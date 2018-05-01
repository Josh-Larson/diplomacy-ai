package csci4511.engine.data;

import me.joshlarson.jlcommon.log.Log;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Board implements Cloneable {
	
	private final Map<String, Node> nodes;
	private final List<Unit> units;
	private int turn;
	
	public Board() {
		this.nodes = new HashMap<>();
		this.units = new ArrayList<>();
		this.turn = 0;
	}
	
	public void incrementTurn() {
		turn++;
	}
	
	public int getTurn() {
		return turn;
	}
	
	@Nonnull
	public Collection<Node> getNodes() {
		return nodes.values();
	}
	
	public Node getNode(String name) {
		return nodes.get(name);
	}
	
	@Nonnull
	public List<Unit> getUnits() {
		return units;
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
	
	public void addNode(@Nonnull Node node) {
		Node prev = this.nodes.putIfAbsent(node.getName(), node);
		if (prev != null)
			throw new IllegalArgumentException("Node already in board! " + prev);
	}
	
	public void addUnit(@Nonnull Unit unit) {
		this.units.add(unit);
	}
	
	public void removeUnit(@Nonnull Unit unit) {
		this.units.remove(unit);
	}
	
	@Override
	public Board clone() {
		try {
			Board b = (Board) super.clone();
			nodes.replaceAll((key, node) -> node.clone());
			units.replaceAll(Unit::clone);
			return b;
		} catch (CloneNotSupportedException e) {
			return null;
		}
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
				board.addNode(new Node(parts[0], parts[1].equals("*"), parts[2].isEmpty() ? null : Country.valueOf(parts[2])));
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
				
				Log.t("Connecting %s from %s to %s", army ? "A" : "F", parts[0], parts[1]);
				if (army)
					board.getNode(parts[0]).addArmyMovement(board.getNode(parts[1]));
				else
					board.getNode(parts[0]).addFleetMovement(board.getNode(parts[1]));
			}
			return board;
		} catch (RuntimeException | IOException e) {
			throw new RuntimeException("Line: " + lineNum, e);
		}
	}
	
}
