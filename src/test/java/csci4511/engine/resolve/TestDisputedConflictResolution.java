package csci4511.engine.resolve;

import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.UnitType;
import csci4511.engine.data.action.Action;
import csci4511.engine.data.action.ActionAttack;
import csci4511.engine.data.action.ActionHold;
import csci4511.engine.data.action.ActionSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

@RunWith(JUnit4.class)
public class TestDisputedConflictResolution {
	
	private Node n1;
	private Node n2;
	private Node n3;
	private Unit u1;
	private Unit u2;
	private Unit u3;
	
	@Before
	public void init() {
		n1 = new Node("n1", false, null);
		n2 = new Node("n2", false, null);
		n3 = new Node("n3", false, null);
		
		u1 = new Unit(UnitType.ARMY, Country.ENGLAND);
		u2 = new Unit(UnitType.ARMY, Country.ENGLAND);
		u3 = new Unit(UnitType.ARMY, Country.ENGLAND);
		
		n1.addArmyMovement(n2);
		n1.addArmyMovement(n3);
		n2.addArmyMovement(n1);
		n2.addArmyMovement(n3);
		n3.addArmyMovement(n1);
		n3.addArmyMovement(n2);
	}
	
	@Test
	public void testBasicStrength() {
		assignPosition(u1, n1);
		Action a = new ActionHold(u1);
		Assert.assertSame(a, ResolutionEngine.getWinningAction(Collections.singletonList(a)));
	}
	
	@Test
	public void testBasicSupport() {
		assignPosition(u1, n1);
		assignPosition(u2, n2);
		
		Action hold = new ActionHold(u1);
		Action sup = new ActionSupport(u2, hold);
		Assert.assertSame(hold, ResolutionEngine.getWinningAction(Arrays.asList(hold, sup)));
	}
	
	@Test
	public void testBounce() {
		assignPosition(u1, n1);
		assignPosition(u2, n2);
		
		Action hold = new ActionHold(u1);
		Action attack = new ActionAttack(u2, n1);
		Assert.assertNull(ResolutionEngine.getWinningAction(Arrays.asList(hold, attack)));
	}
	
	@Test
	public void testWinner() {
		assignPosition(u1, n1);
		assignPosition(u2, n2);
		assignPosition(u3, n3);
		
		Action hold = new ActionHold(u1);
		Action sup = new ActionSupport(u3, hold);
		Action attack = new ActionAttack(u2, n1);
		Assert.assertEquals(hold, ResolutionEngine.getWinningAction(Arrays.asList(hold, attack, sup)));
	}
	
	@Test
	public void testDisplacedRemoval() {
		assignPosition(u1, n1);
		assignPosition(u2, n2);
		assignPosition(u3, n3);
		
		Action hold = new ActionHold(u1);
		Action attack = new ActionAttack(u2, n1);
		Action sup = new ActionSupport(u3, attack);
		Assert.assertEquals(attack, ResolutionEngine.getWinningAction(Arrays.asList(hold, attack, sup)));
		Map<Unit, List<Node>> retreats = new HashMap<>();
		
		ResolutionEngine.resolveDisputed(retreats, Arrays.asList(hold, attack, sup), n1);
		Assert.assertSame(n1, u2.getNode());
		Assert.assertSame(n3, u3.getNode());
		Assert.assertEquals(1, retreats.size());
		Assert.assertEquals(1, retreats.get(u1).size());
	}
	
	private static void assignPosition(Unit u, Node n) {
		u.setNode(n);
	}
	
}
