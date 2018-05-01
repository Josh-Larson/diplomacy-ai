package csci4511;

import csci4511.algorithms.Algorithm;
import csci4511.algorithms.SimpleHeuristic;
import csci4511.engine.data.*;
import csci4511.engine.data.action.Action;
import csci4511.engine.resolve.ResolutionEngine;
import csci4511.ui.DiplomacyUI;
import me.joshlarson.jlcommon.concurrency.Delay;
import me.joshlarson.jlcommon.control.SafeMain;
import me.joshlarson.jlcommon.log.Log;
import me.joshlarson.jlcommon.log.log_wrapper.ConsoleLogWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.List;

public class Diplomacy {
	
	public static void main(String[] args) {
		SafeMain.main("diplomacy", Diplomacy::run);
	}
	
	private static void run() {
		Log.addWrapper(new ConsoleLogWrapper());
		Board test;
		Algorithm algorithm = new SimpleHeuristic();
		{
			test = Board.loadFromStream(Diplomacy.class.getResourceAsStream("/diplomacy.txt"));
//			setupNodes(test);
//			setupSupplyCenters(test);
			setupDefaultBoard(test);
		}
		{
//			test = Board.loadFromStream(Diplomacy.class.getResourceAsStream("/5-node-board.txt"));
//			createArmy(test, Country.ENGLAND, "1");
//			createArmy(test, Country.GERMANY, "4");
//			test.getNode("1").setCountry(Country.ENGLAND);
//			test.getNode("4").setCountry(Country.GERMANY);
//			actions = new SimpleHeuristic().determineActions(test, Country.ENGLAND, EnumSet.of(Country.ENGLAND));
		}
		JFrame frame = DiplomacyUI.showBoard(test, new Dimension(1920, 1080));
		long turn = 0;
		while (frame.isShowing()) {
			if (!Delay.sleepMilli(1000))
				break;
			runAlgorithm(test, algorithm);
			frame.repaint();
			if (++turn % 2 == 0) {
				for (Node n : test.getNodes()) {
					Unit unit = n.getGarissoned();
					if (unit != null)
						n.setCountry(unit.getCountry());
				}
			}
		}
	}
	
	private static void runAlgorithm(Board board, Algorithm algorithm) {
		for (Country country : Country.values()) {
			Log.d("Deciding actions for %s...", country);
			for (Action action : algorithm.determineActions(board, country, EnumSet.of(country))) {
				action.getUnit().setAction(action);
			}
		}
		Log.d("Resolving...");
		ResolutionEngine.resolve(board);
		Log.d("Resolved.");
	}
	
	private static void setupDefaultBoard(Board b) {
		createArmy(b, Country.ENGLAND, "LVP");
		createFleet(b, Country.ENGLAND, "EDN");
		createFleet(b, Country.ENGLAND, "LDN");
		b.getNode("LVP").setCountry(Country.ENGLAND);
		b.getNode("EDN").setCountry(Country.ENGLAND);
		b.getNode("LDN").setCountry(Country.ENGLAND);
		
		createArmy(b, Country.FRANCE, "PAR");
		createArmy(b, Country.FRANCE, "MAR");
		createFleet(b, Country.FRANCE, "BRE");
		b.getNode("PAR").setCountry(Country.FRANCE);
		b.getNode("MAR").setCountry(Country.FRANCE);
		b.getNode("BRE").setCountry(Country.FRANCE);
		
		createArmy(b, Country.GERMANY, "BER");
		createArmy(b, Country.GERMANY, "MUN");
		createFleet(b, Country.GERMANY, "KIE");
		b.getNode("BER").setCountry(Country.GERMANY);
		b.getNode("MUN").setCountry(Country.GERMANY);
		b.getNode("KIE").setCountry(Country.GERMANY);
		
		createArmy(b, Country.RUSSIA, "WAR");
		createArmy(b, Country.RUSSIA, "MOS");
		createFleet(b, Country.RUSSIA, "STP");
		createFleet(b, Country.RUSSIA, "STE");
		b.getNode("WAR").setCountry(Country.RUSSIA);
		b.getNode("MOS").setCountry(Country.RUSSIA);
		b.getNode("STP").setCountry(Country.RUSSIA);
		b.getNode("STE").setCountry(Country.RUSSIA);
		
		createArmy(b, Country.AUSTRIA, "VIE");
		createArmy(b, Country.AUSTRIA, "BUD");
		createFleet(b, Country.AUSTRIA, "TRI");
		b.getNode("VIE").setCountry(Country.AUSTRIA);
		b.getNode("BUD").setCountry(Country.AUSTRIA);
		b.getNode("TRI").setCountry(Country.AUSTRIA);
		
		createArmy(b, Country.ITALY, "VEN");
		createArmy(b, Country.ITALY, "RME");
		createFleet(b, Country.ITALY, "NAP");
		b.getNode("VEN").setCountry(Country.ITALY);
		b.getNode("RME").setCountry(Country.ITALY);
		b.getNode("NAP").setCountry(Country.ITALY);
		
		createArmy(b, Country.TURKEY, "CON");
		createArmy(b, Country.TURKEY, "SMY");
		createFleet(b, Country.TURKEY, "ANK");
		b.getNode("CON").setCountry(Country.TURKEY);
		b.getNode("SMY").setCountry(Country.TURKEY);
		b.getNode("ANK").setCountry(Country.TURKEY);
	}
	
	private static void setupNodes(Board b) {
		createArmy(b, Country.RUSSIA, "NWY");
		createArmy(b, Country.RUSSIA, "PRU");
		createArmy(b, Country.RUSSIA, "WAR");
		createArmy(b, Country.RUSSIA, "BOH");
		createArmy(b, Country.RUSSIA, "UKR");
		createArmy(b, Country.RUSSIA, "GAL");
		createFleet(b, Country.RUSSIA, "STE");
		createFleet(b, Country.RUSSIA, "BAL");
		createFleet(b, Country.RUSSIA, "NTH");
		createFleet(b, Country.RUSSIA, "CLY");
		
		// Relevant opposing actors
		createArmy(b, Country.ENGLAND, "EDN");
		createFleet(b, Country.FRANCE, "IRI");
		createFleet(b, Country.FRANCE, "WAL");
		createFleet(b, Country.FRANCE, "LDN");
		createArmy(b, Country.FRANCE, "KIE");
		createArmy(b, Country.FRANCE, "RUH");
		createArmy(b, Country.FRANCE, "BUR");
		createArmy(b, Country.FRANCE, "PAR");
		createFleet(b, Country.GERMANY, "DEN");
		createArmy(b, Country.GERMANY, "BER");
		createArmy(b, Country.GERMANY, "SIL");
	}
	
	private static void setupSupplyCenters(Board b) {
		b.getNode("NWY").setCountry(Country.RUSSIA);
		b.getNode("SWE").setCountry(Country.RUSSIA);
		b.getNode("DEN").setCountry(Country.GERMANY);
		b.getNode("KIE").setCountry(Country.FRANCE);
		b.getNode("MUN").setCountry(Country.GERMANY);
		b.getNode("HOL").setCountry(Country.FRANCE);
		b.getNode("BEL").setCountry(Country.FRANCE);
		b.getNode("LDN").setCountry(Country.FRANCE);
		b.getNode("LVP").setCountry(Country.RUSSIA);
		b.getNode("EDN").setCountry(Country.ENGLAND);
		b.getNode("PAR").setCountry(Country.FRANCE);
		b.getNode("BRE").setCountry(Country.FRANCE);
		b.getNode("POR").setCountry(Country.FRANCE);
		
		b.getNode("MOS").setCountry(Country.RUSSIA);
		b.getNode("STE").setCountry(Country.RUSSIA);
		b.getNode("ANK").setCountry(Country.TURKEY);
		b.getNode("CON").setCountry(Country.TURKEY);
		b.getNode("BUL").setCountry(Country.TURKEY);
		b.getNode("ROM").setCountry(Country.RUSSIA);
		b.getNode("SER").setCountry(Country.TURKEY);
		b.getNode("GRE").setCountry(Country.TURKEY);
		b.getNode("SMY").setCountry(Country.TURKEY);
		b.getNode("TRI").setCountry(Country.TURKEY);
		b.getNode("VIE").setCountry(Country.RUSSIA);
		b.getNode("NAP").setCountry(Country.ITALY);
		b.getNode("RME").setCountry(Country.ITALY);
		b.getNode("TUN").setCountry(Country.ITALY);
		
		b.getNode("STP").setCountry(Country.RUSSIA);
		b.getNode("WAR").setCountry(Country.RUSSIA);
		b.getNode("BER").setCountry(Country.GERMANY);
		b.getNode("BUD").setCountry(Country.RUSSIA);
		b.getNode("VEN").setCountry(Country.AUSTRIA);
		b.getNode("MAR").setCountry(Country.FRANCE);
		b.getNode("SPA").setCountry(Country.FRANCE);
	}
	
	private static void createArmy(Board b, Country country, String node) {
		Unit u = new Unit(UnitType.ARMY, country);
		u.setNode(b.getNode(node));
		b.addUnit(u);
	}
	
	private static void createFleet(Board b, Country country, String node) {
		Unit u = new Unit(UnitType.FLEET, country);
		u.setNode(b.getNode(node));
		b.addUnit(u);
	}
	
	private static class Result {
		
		private final List<Unit> units;
		private final double score;
		
		public Result(List<Unit> units, double score) {
			this.units = units;
			this.score = score;
		}
		
		public List<Unit> getUnits() {
			return units;
		}
		
		public double getScore() {
			return score;
		}
	}
	
}
