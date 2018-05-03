package csci4511;

import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Unit;
import csci4511.engine.data.UnitType;
import me.joshlarson.jlcommon.concurrency.ThreadPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BoardFactory {
	
	private static final byte [] DEFAULT_BOARD;
	
	static {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (InputStream is = Diplomacy.class.getResourceAsStream("/diplomacy.txt")) {
			byte [] buffer = new byte[4096];
			int n;
			while ((n = is.read(buffer)) > 0) {
				baos.write(buffer, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		DEFAULT_BOARD = baos.toByteArray();
	}
	
	private final ThreadPool threadPool;
	private final BlockingQueue<Board> boards;
	
	public BoardFactory() {
		this.threadPool = new ThreadPool(3, "board-factory-%d");
		this.boards = new ArrayBlockingQueue<>(150);
	}
	
	public void start() {
		threadPool.start();
		threadPool.execute(this::run);
		threadPool.execute(this::run);
		threadPool.execute(this::run);
	}
	
	public void stop() {
		threadPool.stop(true);
	}
	
	public Board getBoard() {
		try {
			return boards.take();
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	private void run() {
		while (threadPool.isRunning()) {
			try {
				boards.put(createDefaultBoard());
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public static Board createDefaultBoard() {
		Board board = Board.loadFromStream(new ByteArrayInputStream(DEFAULT_BOARD));
		setupDefaultBoard(board);
		return board;
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
	
}
