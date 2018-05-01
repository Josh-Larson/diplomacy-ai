package csci4511;

import csci4511.algorithms.SimpleHeuristic;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Unit;
import csci4511.engine.data.UnitType;
import csci4511.ui.DiplomacyUI;
import me.joshlarson.jlcommon.control.SafeMain;
import me.joshlarson.jlcommon.log.Log;
import me.joshlarson.jlcommon.log.log_wrapper.ConsoleLogWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.List;

public class Diplomacy {
	
	public static void main(String [] args) {
		SafeMain.main("diplomacy", Diplomacy::run);
	}
	
	private static void run() {
		Log.addWrapper(new ConsoleLogWrapper());
		Board test;
		{
//			test = Board.loadFromStream(Diplomacy.class.getResourceAsStream("/diplomacy.txt"));
//			setupNodes(test);
//			setupSupplyCenters(test);
		}
		{
			test = Board.loadFromStream(Diplomacy.class.getResourceAsStream("/5-node-board.txt"));
			createArmy(test, Country.ENGLAND, "1");
			test.getNode("1").setCountry(Country.ENGLAND);
		}
		new SimpleHeuristic().determineActions(test, Country.ENGLAND, EnumSet.of(Country.ENGLAND));
//		DiplomacyUI.showBoardAndWait(test, new Dimension(1920, 1080));
	}
	
	private static void setupNodes(Board b) {
		createArmy(b, Country.ENGLAND, "MUN");
		createArmy(b, Country.ENGLAND, "KIE");
		createArmy(b, Country.ENGLAND, "SPA");
		createFleet(b, Country.ENGLAND, "FIN");
		createFleet(b, Country.ENGLAND, "BOT");
		createFleet(b, Country.ENGLAND, "BAL");
		createFleet(b, Country.ENGLAND, "BER");
		createFleet(b, Country.ENGLAND, "NTH");
		createFleet(b, Country.ENGLAND, "BIG");
		createFleet(b, Country.ENGLAND, "BEL");
		createFleet(b, Country.ENGLAND, "ENG");
		createFleet(b, Country.ENGLAND, "MAT");
		createFleet(b, Country.ENGLAND, "TYH");
		
		createArmy(b, Country.TURKEY, "MOS");
		createArmy(b, Country.TURKEY, "UKR");
		createArmy(b, Country.TURKEY, "GAL");
		createArmy(b, Country.TURKEY, "ROM");
		createArmy(b, Country.TURKEY, "BUL");
		createArmy(b, Country.TURKEY, "SER");
		createArmy(b, Country.TURKEY, "TRI");
		createArmy(b, Country.TURKEY, "VIE");
		createFleet(b, Country.TURKEY, "BLA");
		createFleet(b, Country.TURKEY, "AEG");
		createFleet(b, Country.TURKEY, "APU");
		createFleet(b, Country.TURKEY, "ION");
		createFleet(b, Country.TURKEY, "TUS");
		createFleet(b, Country.TURKEY, "WMD");
		
		createArmy(b, Country.AUSTRIA, "VEN");
		createFleet(b, Country.AUSTRIA, "ADR");
		createArmy(b, Country.RUSSIA, "WAR");
		createFleet(b, Country.RUSSIA, "STP");
		createArmy(b, Country.ITALY, "PIE");
		createArmy(b, Country.FRANCE, "TYR");
		createArmy(b, Country.GERMANY, "SIL");
	}
	
	private static void setupSupplyCenters(Board b) {
		b.getNode("NWY").setCountry(Country.ENGLAND);
		b.getNode("SWE").setCountry(Country.ENGLAND);
		b.getNode("DEN").setCountry(Country.ENGLAND);
		b.getNode("KIE").setCountry(Country.ENGLAND);
		b.getNode("MUN").setCountry(Country.ENGLAND);
		b.getNode("HOL").setCountry(Country.ENGLAND);
		b.getNode("BEL").setCountry(Country.ENGLAND);
		b.getNode("LDN").setCountry(Country.ENGLAND);
		b.getNode("LVP").setCountry(Country.ENGLAND);
		b.getNode("EDN").setCountry(Country.ENGLAND);
		b.getNode("PAR").setCountry(Country.ENGLAND);
		b.getNode("BRE").setCountry(Country.ENGLAND);
		b.getNode("POR").setCountry(Country.ENGLAND);
		
		b.getNode("MOS").setCountry(Country.TURKEY);
		b.getNode("STE").setCountry(Country.TURKEY);
		b.getNode("ANK").setCountry(Country.TURKEY);
		b.getNode("CON").setCountry(Country.TURKEY);
		b.getNode("BUL").setCountry(Country.TURKEY);
		b.getNode("ROM").setCountry(Country.TURKEY);
		b.getNode("SER").setCountry(Country.TURKEY);
		b.getNode("GRE").setCountry(Country.TURKEY);
		b.getNode("SMY").setCountry(Country.TURKEY);
		b.getNode("TRI").setCountry(Country.TURKEY);
		b.getNode("VIE").setCountry(Country.TURKEY);
		b.getNode("NAP").setCountry(Country.TURKEY);
		b.getNode("RME").setCountry(Country.TURKEY);
		b.getNode("TUN").setCountry(Country.TURKEY);
		
		b.getNode("STP").setCountry(Country.RUSSIA);
		b.getNode("WAR").setCountry(Country.RUSSIA);
		b.getNode("BER").setCountry(Country.GERMANY);
		b.getNode("BUD").setCountry(Country.AUSTRIA);
		b.getNode("VEN").setCountry(Country.AUSTRIA);
		b.getNode("MAR").setCountry(Country.ITALY);
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
