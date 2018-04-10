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
		Assert.assertEquals(Country.ENGLAND, units.get(0).getCountry());
		Assert.assertEquals(Country.GERMANY, units.get(1).getCountry());
		units.forEach(u -> Assert.assertNull(u.getAction()));
		resolve(board);
	}
	
	@Test
	public void testBasicHold() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "H,1,1;H,4,4");
		List<Unit> units = board.getUnits();
		Assert.assertEquals(2, units.size());
		units.forEach(u -> Assert.assertNotNull(u.getAction()));
		resolve(board);
	}
	
	@Test
	public void testBasicBounce() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "A,1,5;A,4,5");
		List<Unit> units = board.getUnits();
		resolve(board);
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("4"), units.get(1).getNode());
	}
	
	@Test
	public void testBasicMove() {
		Board board = createBoard("1,ENGLAND;4,GERMANY", "A,1,5;H,4,4");
		List<Unit> units = board.getUnits();
		resolve(board);
		Assert.assertSame(board.getNode("5"), units.get(0).getNode());
		Assert.assertSame(board.getNode("4"), units.get(1).getNode());
	}
	
	@Test
	public void testBasicSupport() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;4,GERMANY", "A,1,5;S,2,1;A,4,5");
		List<Unit> units = board.getUnits();
		resolve(board);
		Assert.assertSame(board.getNode("5"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("4"), units.get(2).getNode());
	}
	
	@Test
	public void testBasicSupportBounce() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;3,GERMANY;4,GERMANY", "A,1,5;S,2,1;A,4,5;S,3,4");
		List<Unit> units = board.getUnits();
		resolve(board);
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
		Assert.assertSame(board.getNode("4"), units.get(3).getNode());
	}
	
	@Test
	public void testDoubleBounce() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;3,GERMANY", "A,1,5;A,2,5;A,3,1");
		List<Unit> units = board.getUnits();
		resolve(board);
		Assert.assertSame(board.getNode("1"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
	}
	
	@Test
	public void testUnitDestroy() {
		Board board = createBoard("5,ENGLAND;2,ENGLAND;3,ENGLAND;4,GERMANY", "A,5,4;S,2,5;S,3,5;H,4,4");
		List<Unit> units = board.getUnits();
		resolve(board);
		Assert.assertEquals(3, units.size());
		Assert.assertSame(board.getNode("4"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
	}
	
	@Test
	public void testUnitDefaultRetreat() {
		Board board = createBoard("1,ENGLAND;2,ENGLAND;3,ENGLAND;5,GERMANY", "A,1,5;S,2,1;S,3,1;H,5,5");
		List<Unit> units = board.getUnits();
		resolve(board);
		Assert.assertEquals(4, units.size());
		Assert.assertSame(board.getNode("5"), units.get(0).getNode());
		Assert.assertSame(board.getNode("2"), units.get(1).getNode());
		Assert.assertSame(board.getNode("3"), units.get(2).getNode());
		Assert.assertSame(board.getNode("4"), units.get(3).getNode());
	}
	
	private static void resolve(Board board) {
//		long start, end, total = 0;
//		for (int i = 0; i < 1000000; i++) {
//			Board cloned = board.clone();
//			start = System.nanoTime();
//			ResolveEngine.resolve(cloned);
//			end = System.nanoTime();
//			total += end - start;
//		}
//		System.out.printf("%d nanoseconds%n", total/1000000);
		ResolveEngine.resolve(board);
		assertBoardResolved(board);
	}
	
	private static void assertBoardResolved(Board board) {
		for (Unit u : board.getUnits()) {
			Assert.assertNull(u.getAction());
		}
		for (Node n : board.getNodes()) {
			Assert.assertEquals(0, n.getResolvingActions().size());
		}
	}
	
	private static Board createBoard(String unitStr, String moveStr) {
		Board board = new Board();
		Map<String, Node> nodes = new HashMap<>();
		nodes.put("1", new Node("1", true, Country.ENGLAND));
		nodes.put("2", new Node("2", false, null));
		nodes.put("3", new Node("3", false, null));
		nodes.put("4", new Node("4", true, Country.GERMANY));
		nodes.put("5", new Node("5", false, null));
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
			Node n = nodes.get(parts[0]);
			u.setNode(n);
			n.setGarissoned(u);
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
