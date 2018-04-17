package csci4511.engine.resolve;

import csci4511.engine.data.*;
import csci4511.engine.data.action.ActionHold;
import org.junit.Assert;
import org.junit.Test;

public class TestUndisputedConflictResolution {
	
	@Test
	public void testUndisputed() {
		Board board = new Board();
		Node start = new Node("start", false, null);
		Node end = new Node("end", false, null);
		Unit unit = new Unit(UnitType.ARMY, Country.ENGLAND);
		
		board.addNode(start);
		board.addNode(end);
		board.addUnit(unit);
		
		unit.setNode(start);
		
		unit.setActionAttack(end);
		assert unit.getAction() != null : "attack action not set";
		ResolutionEngine.resolveUndisputed(unit.getAction(), end);
		Assert.assertEquals(end, unit.getNode());
	}
	
	@Test
	public void testGarissonedFinalUndisputed() {
		Board board = new Board();
		Node start = new Node("start", false, null);
		Node end = new Node("end", false, null);
		Unit unit1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit unit2 = new Unit(UnitType.ARMY, Country.ENGLAND);
		
		board.addNode(start);
		board.addNode(end);
		board.addUnit(unit1);
		board.addUnit(unit2);
		
		unit1.setNode(start);
		unit2.setNode(end);
		
		unit1.setActionAttack(end);
		assert unit1.getAction() != null : "attack action not set";
		ResolutionEngine.resolveUndisputed(unit1.getAction(), end);
		Assert.assertEquals(start, unit1.getNode());
		Assert.assertEquals(end, unit2.getNode());
	}
	
	@Test
	public void testGarissonedInProgressUndisputed() {
		Board board = new Board();
		Node start = new Node("start", false, null);
		Node end = new Node("end", false, null);
		Unit unit1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit unit2 = new Unit(UnitType.ARMY, Country.ENGLAND);
		
		board.addNode(start);
		board.addNode(end);
		board.addUnit(unit1);
		board.addUnit(unit2);
		
		unit1.setNode(start);
		unit2.setNode(end);
		
		unit1.setActionAttack(end);
		unit2.setActionHold();
		
		assert unit1.getAction() != null : "attack action not set";
		ResolutionEngine.resolveUndisputed(unit1.getAction(), end);
		Assert.assertEquals(start, unit1.getNode());
		Assert.assertEquals(end, unit2.getNode());
		Assert.assertNotNull(unit1.getAction());
		Assert.assertNotNull(unit2.getAction());
		
		unit2.clearAction();
		ResolutionEngine.resolveUndisputed(unit1.getAction(), end);
		Assert.assertEquals(start, unit1.getNode());
		Assert.assertEquals(end, unit2.getNode());
		Assert.assertTrue(unit1.getAction() instanceof ActionHold);
		Assert.assertNull(unit2.getAction());
	}
	
}
