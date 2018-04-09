package csci4511.engine.data;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private final List<Node> nodes;
	private final List<Unit> units;
	
	public Board() {
		this.nodes = new ArrayList<>();
		this.units = new ArrayList<>();
	}
	
	@Nonnull
	public List<Node> getNodes() {
		return nodes;
	}
	
	@Nonnull
	public List<Unit> getUnits() {
		return units;
	}
	
	public void addNode(@Nonnull Node node) {
		this.nodes.add(node);
	}
	
	public void addUnit(@Nonnull Unit unit) {
		this.units.add(unit);
	}
	
}
