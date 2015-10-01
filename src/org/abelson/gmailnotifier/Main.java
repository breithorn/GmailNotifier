
package org.abelson.gmailnotifier;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import org.abelson.gmailnotifier.atom.GmailAtomFeed;
import org.abelson.gmailnotifier.sms.skype.SkypeSMSNotifier;
import org.apache.log4j.Logger;

/**
 * This class starts the Gmail notification service and runs its
 * main loop.
 * @author Carl Fredrik Abelson
 */
public class Main
{
	// Log4j
	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args)
	{
		// Initialize configuration values
		logger.info("Initializing configuration...");
		if (!Configuration.initFromFile()) {
			logger.fatal("Failed reading or finding configuration file");
			logger.fatal("Terminating application!");
			System.exit(1);
		}
		logger.info("Successfully initialized configuration.");

		// Initialize Atom feed reader
		logger.info("Initializing Atom feed reader...");
		GmailAtomFeed feed = null;
		try {
			feed = new GmailAtomFeed();
		} catch (ParserConfigurationException e) {
			logger.fatal("Failed to initialize Atom feed: " + e);
			logger.fatal("Terminating application!");
			System.exit(1);
		} catch (IOException e) {
			logger.fatal("Failed to retrieve Atom feed: " + e);
			logger.fatal("Terminating application!");
			System.exit(1);
		}
		logger.info("Successfully initialized Atom feed reader.");

		// Initialize SMS notifier
		logger.info("Initializing SMS notifier...");
		SkypeSMSNotifier notifier = null;
		if (Configuration.sendSMS) {
			notifier = new SkypeSMSNotifier();
		}
		logger.info("Successfully initialized SMS notifier.");

		logger.info("Starting Gmail message checking...");
		while (true) {
			logger.info("Looking for new Gmail messages...");
			feed.refresh();
			Vector<GmailMessage> messages = feed.getNewGmailMessages();
			if (messages.size() > 0) {
				logger.info("There is a new Gmail message!");
				Iterator<GmailMessage> it = messages.iterator();
				while (it.hasNext()) {
					GmailMessage message = it.next();
					if (message != null) {
						logger.info("Title = " + message.getTitle());
						logger.info("Summary = " + message.getSummary());
						logger.info("From = " + message.getFromName());
						logger.info("Email = " + message.getFromMail());
						if (Configuration.sendSMS) {
							boolean status = notifier.sendSMS(message, true);
							logger.info("Sending notification SMS...");
							if (status) {
								logger.info("Successfully sent SMS");
							} else {
								logger.error("Failed sending SMS!");
							}
						}
					}
				}
			}
			try {
				Thread.sleep(1000 * Configuration.checkInterval);
			} catch (InterruptedException e) {
			}
		}
	}

}
