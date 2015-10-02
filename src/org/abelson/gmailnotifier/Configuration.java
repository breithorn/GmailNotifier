 
package org.abelson.gmailnotifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Contains various constants for use in the Gmail notification application.
 */
public class Configuration
{
	// Log4j
	private static Logger logger = Logger.getLogger(Configuration.class);

	/**
	 * The user name for your Gmail account.
	 */
	public static String gmailUsername;

	/**
	 * The password for your Gmail account.
	 */
	public static String gmailPassword;

	/**
	 * How often to check for new Gmail messages (in seconds).
	 */
	public static int checkInterval;

	/**
	 * Whether notification SMS should be sent or not.
	 */
	public static boolean sendSMS;

	/**
	 * Max number of notification SMS to send per <i>day</i>.
	 * Use this to limit your Skype phone bill.
	 * For unlimited amount, use Integer.MAX_VALUE. 
	 */
	public static int maxSMSPerDay;

	/**
	 * Max number of notification SMS to send per <i>hour</i>.
	 * Use this to limit your phone bill.
	 * For unlimited amount, use Integer.MAX_VALUE. 
	 */
	public static int maxSMSPerHour;

	/**
	 * Phone number to send the notification SMS to. Needs to be a number
	 * reachable from Skype.
	 */
	public static String mobileNumber;

	public static void dumpValues()
	{
		logger.info("gmailUsername = [" + gmailUsername + "]");
		logger.info("gmailPassword = [" + gmailPassword + "]");
		logger.info("checkInterval = [" + checkInterval + "]");
		logger.info("sendSMS = [" + sendSMS + "]");
		logger.info("maxSMSPerDay = [" + maxSMSPerDay + "]");
		logger.info("maxSMSPerHour = [" + maxSMSPerHour + "]");
		logger.info("mobileNumber = [" + mobileNumber + "]");
	}

	/**
	 * Reads configuration values from file and stores them in this class. 
	 * @return <tt>true</tt> if successsfully read the configuration file.
	 */
	public static boolean initFromFile()
	{
		logger.info("Reading application configuration file...");
		boolean result = true;
		try {
			String configPath = "config/config.txt";
			FileReader input = null;
			try {
				input = new FileReader(configPath);
			} catch (FileNotFoundException e) {
				configPath = "export/config/config.txt";
				input = new FileReader(configPath);
			}
			BufferedReader bufRead = new BufferedReader(input);
			String line = "";
			String section = "";
			int linenumber = 1;
			do {
				line = bufRead.readLine();
				if (line != null) {
					line = line.trim();
					if (!line.startsWith(";") && !line.equals("")) {
						if (line.startsWith("[")) {
							section = line.substring(1, line.length() - 1);
							section = section.toLowerCase();
						} else {
							int pos = line.indexOf('=');
							if (pos > 0) {
								String key = line.substring(0, pos).trim().toLowerCase();
								String value = line.substring(pos+1);
								if (value.startsWith("\"") || value.startsWith("\'")) {
									value = value.substring(1);
								}
								if (value.endsWith("\"") || value.endsWith("\'")) {
									value = value.substring(0, value.length() - 1);
								}
								if (section.equals("gmail")) {
									if (key.equals("username")) {
										gmailUsername = value;
									} else if (key.equals("password")) {
										gmailPassword = value;
									} else if (key.equals("checkinterval")) {
										int n = -1;
										try {
											n = Integer.parseInt(value);
										} catch (NumberFormatException e) {
											n = -1;
										}
										if (n > 0) {
											checkInterval = n;
										}
									} else {
										logger.error("Skipping invalid data at line " + linenumber);
										logger.error("Not a valid entry.");
									}
								} else if (section.equals("sms")) {
									if (key.equals("enabled")) {
										sendSMS = value.equals("yes");
									} else if (key.equals("maxperday")) {
										int n = -1;
										try {
											n = Integer.parseInt(value);
										} catch (NumberFormatException e) {
											n = -1;
										}
										if (n > 0) {
											maxSMSPerDay = n;
										}
									} else if (key.equals("maxperhour")) {
										int n = -1;
										try {
											n = Integer.parseInt(value);
										} catch (NumberFormatException e) {
											n = -1;
										}
										if (n > 0) {
											maxSMSPerHour = n;
										}
									} else if (key.equals("phonenumber")) {
										mobileNumber = value;
									} else {
										logger.error("Skipping invalid data at line " + linenumber);
										logger.error("Not a valid entry.");
									}
								}
							} else {
								logger.error("Skipping bad data at line " + linenumber);
								logger.error("Not a valid entry.");
							}
						}
					}
				}
				linenumber++;
			} while (line != null);
			bufRead.close();
			dumpValues();
		} catch (IOException e) {
			logger.error("Error when reading configuration file: " + e);
			result = false;
		}
		return result;
	}
}
