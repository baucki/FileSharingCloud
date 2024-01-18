package app;

import mutex.MutexType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class contains all the global application configuration stuff.
 * @author bmilojkovic
 *
 */
public class AppConfig {

	/**
	 * Convenience access for this servent's information
	 */
	public static ServentInfo myServentInfo;

	/**
	 * Print a message to stdout with a timestamp
	 * @param message message to print
	 */
	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();

		System.out.println(timeFormat.format(now) + " - " + message);
	}

	/**
	 * Print a message to stderr with a timestamp
	 * @param message message to print
	 */
	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();

		System.err.println(timeFormat.format(now) + " - " + message);
	}
	public static int BOOTSTRAP_PORT;
	public static int SERVENT_COUNT;
	public static int WEAK_GROUNDS_FOR_DISMISSAL;
	public static int STRONG_GROUNDS_FOR_DISMISSAL;
	public static MutexType MUTEX_TYPE = MutexType.NONE;

	public static CustomState customState;


	/**
	 * Reads a config file. Should be called once at start of app.
	 * The config file should be of the following format:
	 * <br/>
	 * <code><br/>
	 * servent_count=3 			- number of servents in the system <br/>
	 * chord_size=64			- maximum value for Chord keys <br/>
	 * bs.port=2000				- bootstrap server listener port <br/>
	 * servent0.port=1100 		- listener ports for each servent <br/>
	 * servent1.port=1200 <br/>
	 * servent2.port=1300 <br/>
	 *
	 * </code>
	 * <br/>
	 * So in this case, we would have three servents, listening on ports:
	 * 1100, 1200, and 1300. A bootstrap server listening on port 2000, and Chord system with
	 * max 64 keys and 64 nodes.<br/>
	 *
	 * @param configName name of configuration file
	 * @param serventId id of the servent, as used in the configuration file
	 */
	public static void readConfig(String configName, int serventId){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configName)));

		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}

		try {
			BOOTSTRAP_PORT = Integer.parseInt(properties.getProperty("bs.port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_port. Exiting...");
			System.exit(0);
		}


		try {
			WEAK_GROUNDS_FOR_DISMISSAL = Integer.parseInt(properties.getProperty("weak_grounds_for_dismissal"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading weak_grounds_for_dismissal. Exiting...");
			System.exit(0);
		}
		try {
			STRONG_GROUNDS_FOR_DISMISSAL = Integer.parseInt(properties.getProperty("strong_grounds_for_dismissal"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading strong_grounds_for_dismissal. Exiting...");
			System.exit(0);
		}

		MUTEX_TYPE = MutexType.NONE;
		String mutexTypeString = properties.getProperty("mutex_type");
		if (mutexTypeString != null) {
			switch (mutexTypeString) {
				case "token":
					MUTEX_TYPE = MutexType.TOKEN;
					break;
				default:
					MUTEX_TYPE = MutexType.NONE;
					AppConfig.timestampedErrorPrint("Couldn't parse mutex type: " + mutexTypeString);
					break;
			}
		}

		customState = new CustomState();


		String portProperty = "servent"+serventId+".port";

		int serventPort = -1;

		try {
			serventPort = Integer.parseInt(properties.getProperty(portProperty));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading " + portProperty + ". Exiting...");
			System.exit(0);
		}

		myServentInfo = new ServentInfo("localhost", serventPort);
		List<Integer> keys = new ArrayList<>();
		keys.add(myServentInfo.getCustomId());
		customState.addKeys(keys);
	}

}
