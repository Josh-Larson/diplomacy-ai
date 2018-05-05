package csci4511.engine.resolve;

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
public class TestResolutionEngine {
	
	@Test
	public void testDefaultAction() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(2, units.size());
		Assert.assertEquals(Country.ENGLAND, units.get(0).getCountry());
		Assert.assertEquals(Country.GERMANY, units.get(1).getCountry());
	}
	
	@Test
	public void testBasicHold() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "H,1,1;H,4,4");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(2, units.size());
	}
	
	@Test
	public void testBasicBounce() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "A,1,5;A,4,5");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("4"), units.get(1).getNode());
	}
	
	@Test
	public void testBasicSwapBounce() {
		Board board = createBoard("1,ENGLAND;5,GERMANY", "A,1,5;A,5,1");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("5"), units.get(1).getNode());
	}
	
	@Test
	public void testTriSwapBounce() {
		Board board = createBoard("1,ENGLAND;5,GERMANY;2,ENGLAND", "A,1,2;A,5,1;A,2,5");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("5"), units.get(1).getNode());
		Assert.assertSame(board.getNode("2"), units.get(2).getNode());
	}
	
	@Test
	public void testBasicMove() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "A,1,5;H,4,4");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("5"), units.get(0).getNode());
		Assert.assertSame(board.getNode("4"), units.get(1).getNode());
	}
	
	@Test
	public void testBasicSupport() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;4,GERMANY", "A,1,5;S,2,1;A,4,5");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("5"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("4"), units.get(2).getNode());
	}
	
	@Test
	public void testBasicSupportBounce() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;3,GERMANY;4,GERMANY", "A,1,5;S,2,1;A,4,5;S,3,4");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
		Assert.assertSame(board.getNode("4"), units.get(3).getNode());
	}
	
	@Test
	public void testBasicSwapSupportBounce() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;5,GERMANY;3,FRANCE", "A,1,5;S,2,1;  A,5,3;A,3,1");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("5"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("1"), units.get(2).getNode());
		Assert.assertSame(board.getNode("3"), units.get(3).getNode()); // Germany was removed and added to the end for auto-retreat
	}
	
	@Test
	public void testTriSwapSupportBounce() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;3,GERMANY;5,GERMANY", "A,1,5;S,2,1;A,5,1;S,3,5");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
		Assert.assertSame(board.getNode("5"), units.get(3).getNode());
	}
	
	@Test
	public void testDoubleBounce() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;3,GERMANY", "A,1,5;A,2,5;A,3,1");
		List<Unit> units = board.getUnits();
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
	}
	
	@Test
	public void testUnitDestroy() {
		Board board = createBoard("5,ENGLAND;2,ENGLAND;3,ENGLAND;4,GERMANY", "A,5,4;S,2,5;S,3,5;H,4,4");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(3, units.size());
		Assert.assertSame(board.getNode("4"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
	}
	
	@Test
	public void testUnitDefaultRetreat() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;3,ENGLAND;5,GERMANY", "A,1,5;S,2,1;S,3,1;H,5,5");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(4, units.size());
		Assert.assertSame(board.getNode("5"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
		Assert.assertSame(board.getNode("4"), units.get(3).getNode());
	}
	
	@Test
	public void testUnitCutSupport() {
		Board board = createBoard("1,GERMANY;5,ENGLAND;2,ENGLAND;3,ENGLAND;4,GERMANY", "A,5,4 ; S,2,5 ; H,3,3 ; A,1,2 ; H,4,4");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(5, units.size());
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("5"), units.get(1).getNode());
		Assert.assertSame(board.getNode("2"), units.get(2).getNode());
		Assert.assertSame(board.getNode("3"), units.get(3).getNode());
		Assert.assertSame(board.getNode("4"), units.get(4).getNode());
	}
	
	@Test
	public void testUnitCutSupportInvalid() {
		Board board = createBoard("5,ENGLAND;2,ENGLAND;3,ENGLAND;4,GERMANY", "A,5,4 ; S,2,5 ; H,3,3 ; A,4,2");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(3, units.size());
		Assert.assertSame(board.getNode("4"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
	}
	
	private static Board createBoard(String unitStr, String moveStr) {
		unitStr = unitStr.replace(' ', ';');
		moveStr = moveStr.replace(' ', ';');
		Board board = Board.loadFromStream(TestResolutionEngine.class.getResourceAsStream("/5-node-board.txt"));
		Board copy = new Board(board);
		for (String unit : unitStr.split(";")) {
			if (unit.isEmpty())
				continue;
			String [] parts = unit.split(",", 2);
			assert parts.length == 2 : "invalid unit string";
			Unit u = new Unit(UnitType.ARMY, Country.valueOf(parts[1]));
			Node n = board.getNode(parts[0]);
			u.setNode(n);
			board.addUnit(u);
		}
		Map<Unit, Action> actions = new HashMap<>();
		for (String move : moveStr.split(";")) {
			if (move.isEmpty())
				continue;
			String [] parts = move.split(",", 3);
			assert parts.length == 3 : "invalid move string";
			Node src = board.getNode(parts[1]);
			Node dst = board.getNode(parts[2]);
			Unit unit = src.getGarissoned();
			assert unit != null : "invalid src";
			switch (parts[0]) {
				case "H":
					actions.put(unit, new ActionHold(unit));
					break;
				case "A":
					actions.put(unit, new ActionAttack(unit, dst));
					break;
				case "S":
					assert dst.getGarissoned() != null : "invalid dst";
					actions.put(unit, new ActionSupport(unit, actions.get(dst.getGarissoned())));
					break;
				case "C":
					assert dst.getGarissoned() != null : "invalid dst";
					actions.put(unit, new ActionConvoy(unit, actions.get(dst.getGarissoned())));
					break;
				default:
					assert false : "invalid operation " + parts[0];
			}
		}
		new ResolutionEngine().resolve(board, actions.values());
		Assert.assertTrue(copy.getUnits().isEmpty());
		for (Node n : copy.getNodes()) {
			Assert.assertNull(n.getGarissoned());
		}
		return board;
	}
	
}
