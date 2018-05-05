package csci4511.engine;

import csci4511.engine.data.*;
import csci4511.engine.data.action.Action;
import csci4511.engine.data.action.ActionHold;
import csci4511.engine.data.action.ActionSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@RunWith(JUnit4.class)
public class TestActionUtilities {
	
	private Board board;
	private Node n1;
	private Node n2;
	private Node n3;
	
	@Before
	public void init() {
		board = new Board();
		n1 = new Node("n1", false, null);
		n2 = new Node("n2", false, null);
		n3 = new Node("n3", false, null);
		
		n1.addArmyMovement(n2);
		n1.addArmyMovement(n3);
		n2.addArmyMovement(n1);
		n2.addArmyMovement(n3);
		n3.addArmyMovement(n1);
		n3.addArmyMovement(n2);
		
		board.addNode(n1);
		board.addNode(n2);
		board.addNode(n3);
	}
	
	@Test
	public void testGetFriendlyUnitCount() {
		Unit u1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit u2 = new Unit(UnitType.ARMY, Country.ENGLAND);
		u1.setNode(n1);
		u2.setNode(n3);
		board.addUnit(u1);
		board.addUnit(u2);
		
		Assert.assertEquals(2, ActionUtilities.getFriendlyNearby(n2, EnumSet.of(Country.ENGLAND)));
		Assert.assertEquals(0, ActionUtilities.getEnemyNearby(n2, EnumSet.of(Country.ENGLAND)));
	}
	
	@Test
	public void testGetEnemyUnitCount() {
		Unit u1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit u2 = new Unit(UnitType.ARMY, Country.GERMANY);
		u1.setNode(n1);
		u2.setNode(n3);
		board.addUnit(u1);
		board.addUnit(u2);
		
		Assert.assertEquals(1, ActionUtilities.getFriendlyNearby(n2, EnumSet.of(Country.ENGLAND)));
		Assert.assertEquals(1, ActionUtilities.getEnemyNearby(n2, EnumSet.of(Country.ENGLAND)));
	}
	
	@Test
	public void testGetFriendlySupportable() {
		Unit u1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit u2 = new Unit(UnitType.ARMY, Country.ENGLAND);
		u1.setNode(n1);
		u2.setNode(n3);
		board.addUnit(u1);
		board.addUnit(u2);
		
		Assert.assertEquals(Collections.singletonList(u2), ActionUtilities.getFriendlySupportable(u1, n2, EnumSet.of(Country.ENGLAND)));
		Assert.assertEquals(Collections.singletonList(u1), ActionUtilities.getFriendlySupportable(u2, n2, EnumSet.of(Country.ENGLAND)));
	}
	
	@Test
	public void testGetPossibleActions() {
		Unit u1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit u2 = new Unit(UnitType.ARMY, Country.ENGLAND);
		u1.setNode(n1);
		u2.setNode(n3);
		board.addUnit(u1);
		board.addUnit(u2);
		
		Action action = new ActionHold(u1);
		List<List<Action>> possibleActions = ActionUtilities.createActionsSupportable(action, EnumSet.of(Country.ENGLAND));
		System.out.println(possibleActions);
		Assert.assertEquals(2, possibleActions.size());
		{
			List<Action> chain = possibleActions.get(0);
			Assert.assertEquals(1, chain.size());
			Assert.assertSame(action, chain.get(0));
		}
		{
			List<Action> chain = possibleActions.get(1);
			Assert.assertEquals(2, chain.size());
			Assert.assertSame(action, chain.get(0));
			Assert.assertTrue(chain.get(1) instanceof ActionSupport);
			Assert.assertSame(action, ((ActionSupport) chain.get(1)).getAction());
		}
	}
	
	@Test
	public void testGetPossibleActionsDepth2() {
		Unit u1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit u2 = new Unit(UnitType.ARMY, Country.ENGLAND);
		Unit u3 = new Unit(UnitType.ARMY, Country.ENGLAND);
		u1.setNode(n1);
		u2.setNode(n2);
		u3.setNode(n3);
		board.addUnit(u1);
		board.addUnit(u2);
		board.addUnit(u3);
		
		Action action = new ActionHold(u1);
		List<List<Action>> possibleActions = ActionUtilities.createActionsSupportable(action, EnumSet.of(Country.ENGLAND));
		Assert.assertEquals(4, possibleActions.size());
		{
			List<Action> chain = possibleActions.get(0);
			Assert.assertEquals(1, chain.size());
			Assert.assertSame(action, chain.get(0));
		}
		{
			List<Action> chain = possibleActions.get(1);
			Assert.assertEquals(2, chain.size());
			Assert.assertSame(u2, chain.get(1).getUnit());
			Assert.assertSame(action, ((ActionSupport) chain.get(1)).getAction());
		}
		{
			List<Action> chain = possibleActions.get(2);
			Assert.assertEquals(3, chain.size());
			Assert.assertSame(u2, chain.get(1).getUnit());
			Assert.assertSame(u3, chain.get(2).getUnit());
			Assert.assertSame(action, ((ActionSupport) chain.get(1)).getAction());
			Assert.assertSame(action, ((ActionSupport) chain.get(2)).getAction());
		}
		{
			List<Action> chain = possibleActions.get(3);
			Assert.assertEquals(2, chain.size());
			Assert.assertSame(u3, chain.get(1).getUnit());
			Assert.assertSame(action, ((ActionSupport) chain.get(1)).getAction());
		}
	}
	
}
