package csci4511.engine.data;

import javax.annotation.Nonnull;
import java.util.*;

public class Board implements Cloneable {
	
	private final Map<String, Node> nodes;
	private final List<Unit> units;
	
	public Board() {
		this.nodes = new HashMap<>();
		this.units = new ArrayList<>();
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
	
	public void addNode(@Nonnull Node node) {
		this.nodes.put(node.getName(), node);
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
			return (Board) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
