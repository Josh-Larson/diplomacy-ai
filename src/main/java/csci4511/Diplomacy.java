package csci4511;

import me.joshlarson.jlcommon.control.SafeMain;
import me.joshlarson.jlcommon.log.Log;
import me.joshlarson.jlcommon.log.log_wrapper.ConsoleLogWrapper;

public class Diplomacy {
	
	public static void main(String [] args) {
		SafeMain.main("diplomacy", Diplomacy::run);
	}
	
	private static void run() {
		Log.addWrapper(new ConsoleLogWrapper());
		Log.i("Hello World");
	}
	
}
