package csci4511.engine.data.node;

import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CoastalNode implements Node {
	
	private final List<CoastalNode> fleetMovements;
	private final StandardNode node;
	private final String coastName;
	
	CoastalNode(@Nonnull StandardNode node, @Nonnull String coastName) {
		this.fleetMovements = new ArrayList<>();
		this.node = node;
		this.coastName = coastName;
	}
	
	public void addFleetMovement(@Nonnull CoastalNode node) {
		if (!fleetMovements.contains(node)) {
			fleetMovements.add(node);
			node.addFleetMovement(this);
		}
	}
	
	public String getCoastName() {
		return coastName;
	}
	
	@Override
	@Nonnull
	public List<CoastalNode> getFleetMovements() {
		return fleetMovements;
	}
	
	@Override
	@Nonnull
	public List<Node> getMovements() {
		return node.getMovements();
	}
	
	@Override
	@CheckForNull
	public Country getCountry() {
		return node.getCountry();
	}
	
	@Override
	@CheckForNull
	public Unit getGarissoned() {
		return node.getGarissoned();
	}
	
	@Override
	public void setCountry(@Nonnull Country country) {
		node.setCountry(country);
	}
	
	@Override
	public void addArmyMovement(@Nonnull Node node) {
		this.node.addArmyMovement(node);
	}
	
	@Override
	@Nonnull
	public CoastalNode getCoast(@Nonnull String coast) {
		return node.getCoast(coast);
	}
	
	@Override
	@Nonnull
	public Collection<CoastalNode> getCoasts() {
		return node.getCoasts();
	}
	
	@Override
	public String toString() {
		Unit garissoned = getGarissoned();
		String name = getName();
		if (garissoned != null)
			return "Node[" + name + "  GAR="+garissoned.getCountry()+"]";
		return "Node["+ name +"]";
	}
	
	@Override
	public int hashCode() {
		return node.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o == this;
	}
	
	@Override
	public void setGarissoned(Unit garissoned) {
		node.setGarissoned(garissoned);
	}
	
	@Override
	@Nonnull
	public String getName() {
		return node.getName() + "-" + coastName;
	}
	
	@Override
	public boolean isSupply() {
		return node.isSupply();
	}
	
	@Override
	@CheckForNull
	public Country getHomeCountry() {
		return node.getHomeCountry();
	}
	
	@Override
	@Nonnull
	public List<Node> getArmyMovements() {
		return node.getArmyMovements();
	}
	
	@Override
	@Nonnull
	public List<CoastalNode> getFleetMovements(String coast) {
		return node.getFleetMovements(coast);
	}
	
	@Nonnull
	@Override
	public Node getCoreNode() {
		return node;
	}
}
