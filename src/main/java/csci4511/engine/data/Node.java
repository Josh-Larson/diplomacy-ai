package csci4511.engine.data;

import csci4511.engine.data.node.CoastalNode;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface Node {
	
	@Nonnull
	String getName();
	
	boolean isSupply();
	
	@CheckForNull
	Country getHomeCountry();
	
	@Nonnull
	List<Node> getArmyMovements();
	
	@Nonnull
	List<CoastalNode> getFleetMovements(String coast);
	
	@Nonnull
	List<CoastalNode> getFleetMovements();
	
	@Nonnull
	List<Node> getMovements();
	
	@CheckForNull
	Country getCountry();
	
	@CheckForNull
	Unit getGarissoned();
	
	void setCountry(@Nonnull Country country);
	
	void addArmyMovement(@Nonnull Node node);
	
	@Nonnull
	CoastalNode getCoast(@Nonnull String coast);
	
	@Nonnull
	Collection<CoastalNode> getCoasts();
	
	@Nonnull
	Node getCoreNode();
	
	boolean equals(Object o);
	
	void setGarissoned(Unit garissoned);
}
