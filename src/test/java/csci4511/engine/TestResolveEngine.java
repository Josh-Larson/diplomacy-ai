package csci4511.engine;

import csci4511.engine.data.*;
import csci4511.engine.data.action.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class TestResolveEngine {
	
	@Test
	public void testDefaultAction() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(2, units.size());
		for (Unit u : units) {
			Assert.assertNull(u.getAction());
		}
		ResolveEngine.resolve(board);
		for (Unit u : units) {
			Assert.assertNotNull(u.getAction());
		}
	}
	
	@Test
	public void testBasicHold() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "H,1,1;H,4,4");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(2, units.size());
		for (Unit u : units) {
			Assert.assertNotNull(u.getAction());
		}
		ResolveEngine.resolve(board);
		for (Unit u : units) {
			Assert.assertNotNull(u.getAction());
		}
	}
	
	private static Board createBoard(String unitStr, String moveStr) {
		Board board = new Board();
		Map<String, Node> nodes = new HashMap<>();
		nodes.put("1", new Node(true, Country.ENGLAND));
		nodes.put("2", new Node(false, null));
		nodes.put("3", new Node(false, null));
		nodes.put("4", new Node(true, Country.GERMANY));
		nodes.put("5", new Node(false, null));
		nodes.get("1").addArmyMovement(nodes.get("2"));
		nodes.get("1").addArmyMovement(nodes.get("3"));
		nodes.get("1").addArmyMovement(nodes.get("5"));
		nodes.get("2").addArmyMovement(nodes.get("1"));
		nodes.get("2").addArmyMovement(nodes.get("4"));
		nodes.get("2").addArmyMovement(nodes.get("5"));
		nodes.get("3").addArmyMovement(nodes.get("1"));
		nodes.get("3").addArmyMovement(nodes.get("4"));
		nodes.get("3").addArmyMovement(nodes.get("5"));
		nodes.get("4").addArmyMovement(nodes.get("2"));
		nodes.get("4").addArmyMovement(nodes.get("3"));
		nodes.get("4").addArmyMovement(nodes.get("5"));
		nodes.get("5").addArmyMovement(nodes.get("1"));
		nodes.get("5").addArmyMovement(nodes.get("2"));
		nodes.get("5").addArmyMovement(nodes.get("3"));
		nodes.get("5").addArmyMovement(nodes.get("4"));
		nodes.values().forEach(board::addNode);
		for (String unit : unitStr.split(";")) {
			if (unit.isEmpty())
				continue;
			String [] parts = unit.split(",", 2);
			assert parts.length == 2 : "invalid unit string";
			Unit u = new Unit(UnitType.ARMY, Country.valueOf(parts[1]));
			u.setNode(nodes.get(parts[0]));
			nodes.get(parts[0]).setGarissoned(u);
			board.addUnit(u);
		}
		for (String move : moveStr.split(";")) {
			if (move.isEmpty())
				continue;
			String [] parts = move.split(",", 3);
			assert parts.length == 3 : "invalid move string";
			Node src = nodes.get(parts[1]);
			Node dst = nodes.get(parts[2]);
			assert src.getGarissoned() != null : "invalid src";
			Action a;
			switch (parts[0]) {
				case "H":
					a = new ActionHold(src.getGarissoned());
					break;
				case "A":
					a = new ActionAttack(src.getGarissoned(), dst);
					break;
				case "S":
					assert dst.getGarissoned() != null : "invalid dst";
					assert dst.getGarissoned().getAction() != null : "invalid dst action";
					a = new ActionSupport(src.getGarissoned(), dst.getGarissoned().getAction());
					break;
				case "C":
					assert dst.getGarissoned() != null : "invalid dst";
					assert dst.getGarissoned().getAction() != null : "invalid dst action";
					a = new ActionConvoy(src.getGarissoned(), dst.getGarissoned().getAction());
					break;
				default:
					a = null;
					assert false : "invalid operation " + parts[0];
			}
			src.getGarissoned().setAction(a);
		}
		return board;
	}
	
}
