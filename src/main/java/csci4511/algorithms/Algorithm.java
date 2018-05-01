package csci4511.algorithms;

import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.action.Action;

import java.util.EnumSet;
import java.util.List;

public interface Algorithm {
	
	List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances);
	
}
