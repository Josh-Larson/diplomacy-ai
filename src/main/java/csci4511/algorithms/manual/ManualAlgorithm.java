package csci4511.algorithms.manual;

import csci4511.algorithms.Algorithm;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.engine.data.Node;
import csci4511.engine.data.Unit;
import csci4511.engine.data.action.*;

import java.util.*;

public class ManualAlgorithm implements Algorithm {
	
	private final Scanner scanner;
	
	public ManualAlgorithm() {
		this.scanner = new Scanner(System.in);
	}
	
	@Override
	public List<Action> determineActions(Board board, Country country, EnumSet<Country> alliances) {
		System.out.println("You are " + country + ". Moves: (format <node> <action> <node>)");
		String line;
		Map<Unit, Action> actions = new HashMap<>();
		while (!(line = scanner.nextLine()).isEmpty()) {
			try {
				String[] parts = line.split(" ", 3);
				if (parts.length == 2 && parts[1].equalsIgnoreCase("hold")) {
					String [] newParts = new String[3];
					newParts[0] = parts[0];
					newParts[1] = parts[1];
					newParts[2] = parts[0];
					parts = newParts;
				}
				if (parts.length != 3) {
					System.err.println("Invalid command! Expected 3 parts");
					continue;
				}
				for (int i = 0; i < 3; i++)
					parts[i] = parts[i].toUpperCase(Locale.US);
				Node src = board.getNode(parts[0]);
				Node dst = board.getNode(parts[2]);
				if (src == null) {
					System.err.println("Invalid source node.");
					continue;
				}
				if (dst == null) {
					System.err.println("Invalid destination node.");
					continue;
				}
				Unit srcUnit = src.getGarissoned();
				if (srcUnit == null || srcUnit.getCountry() != country) {
					System.err.println("Invalid source unit");
					continue;
				}
				switch (parts[1]) {
					case "ATTACK":
					case "TO":
						actions.put(srcUnit, new ActionAttack(srcUnit, dst));
						break;
					case "SUPPORT": {
						Unit dstUnit = dst.getGarissoned();
						if (dstUnit == null) {
							System.err.println("Invalid destination unit");
							break;
						}
						actions.put(srcUnit, new ActionSupport(srcUnit, actions.get(dstUnit)));
						break;
					}
					case "CONVOY": {
						Unit dstUnit = dst.getGarissoned();
						if (dstUnit == null) {
							System.err.println("Invalid destination unit");
							break;
						}
						actions.put(srcUnit, new ActionConvoy(srcUnit, actions.get(dstUnit)));
						break;
					}
					case "HOLD":
						actions.put(srcUnit, new ActionHold(srcUnit));
						break;
					default:
						System.err.println("Invalid action: " + parts[1]);
						break;
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return new ArrayList<>(actions.values());
	}
}
