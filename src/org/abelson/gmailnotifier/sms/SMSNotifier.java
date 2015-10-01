
package org.abelson.gmailnotifier.sms;

import java.util.Iterator;
import java.util.Vector;
import org.abelson.gmailnotifier.Configuration;
import org.abelson.gmailnotifier.GmailMessage;
import org.apache.log4j.Logger;

/**
 * This class sends a notification SMS for a new Gmail message.
 * @author Carl Fredrik Abelson
 */
public abstract class SMSNotifier
{
	// Log4j
	private static Logger logger = Logger.getLogger(SMSNotifier.class);

	/**
	 * The maximum length of an SMS. The maximum single text message size 
	 * is either 160 7-bit characters, 140 8-bit characters, or 70 16-bit 
	 * characters.
	 */
	private static final int maxLengthSMS = 160;

	private class MetaInfoSMS
	{
		public long timestamp;
		public MetaInfoSMS()
		{
			timestamp = System.currentTimeMillis();
		}
	}

	private Vector<MetaInfoSMS> sentSMS;

	/**
	 * Constructs a new SMS notifier.
	 */
	public SMSNotifier()
	{
		sentSMS = new Vector<MetaInfoSMS>();
	}

	private int getNumberSMSOfLastDay()
	{
		int count = 0;
		long now = System.currentTimeMillis();
		Iterator<MetaInfoSMS> it = sentSMS.iterator();
		while (it.hasNext()) {
			MetaInfoSMS info = it.next();
			if (now - info.timestamp <= 24*60*60*1000) {
				count++;
			}
		}
		return count;
	}

	private int getNumberSMSOfLastHour()
	{
		int count = 0;
		long now = System.currentTimeMillis();
		Iterator<MetaInfoSMS> it = sentSMS.iterator();
		while (it.hasNext()) {
			MetaInfoSMS info = it.next();
			if (now - info.timestamp <= 60*60*1000) {
				count++;
			}
		}
		return count;
	}

	protected String getSMSContent(GmailMessage gmailMessage, boolean includeSummary)
	{
		String content = "";
		content += gmailMessage.getFromName();
		content += " (" + gmailMessage.getFromMail() + ")";

		if (includeSummary) {
			content += " \"" + gmailMessage.getTitle() + "\": ";
			content += gmailMessage.getSummary();
		} else {
			content += ": " + gmailMessage.getTitle();
		}

		if (content.length() > maxLengthSMS) {
			content = content.substring(0, maxLengthSMS);
		}

		return content;
	}

	protected boolean allowSMS()
	{
		boolean allow = true;
		int numberLastDay = getNumberSMSOfLastDay();
		int numberLastHour = getNumberSMSOfLastHour();
		logger.info("#SMS in last day = " + numberLastDay);
		logger.info("#SMS in last hour = " + numberLastHour);
		logger.info("May we send an SMS?");
		if ((numberLastDay < Configuration.maxSMSPerDay) && (numberLastHour < Configuration.maxSMSPerHour)) {
			logger.info("Go ahead!");
			allow = true;
		} else {
			logger.info("Nope.");
			allow = false;
		}
		return allow;
	}

	protected void logSentSMS()
	{
		sentSMS.add(new MetaInfoSMS());
	}

	/**
	 * Sends a notification SMS.
	 * @param gmailMessage The Gmail message for which we should 
	 * send an SMS notification about.
	 * @return <tt>true</tt> if the SMS was successfully sent.
	 * @param includeSummary If a summary of the message body should be included.
	 */
	public abstract boolean sendSMS(GmailMessage gmailMessage, boolean includeSummary);
}