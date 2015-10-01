
package org.abelson.gmailnotifier.sms.skype;

import com.skype.SMS;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.SMS.Status;
import org.abelson.gmailnotifier.Configuration;
import org.abelson.gmailnotifier.GmailMessage;
import org.abelson.gmailnotifier.sms.SMSNotifier;
import org.apache.log4j.Logger;

/**
 * This class sends a notification SMS for a new Gmail message using Skype.
 * @author Carl Fredrik Abelson
 */
public class SkypeSMSNotifier extends SMSNotifier
{
	// Log4j
	private static Logger logger = Logger.getLogger(SkypeSMSNotifier.class);

	/**
	 * Sends a notification SMS using SKype.
	 * @param gmailMessage The Gmail message for which we should 
	 * send an SMS notification about.
	 * @return <tt>true</tt> if the SMS was successfully sent.
	 * @param includeSummary If a summary of the message body should be included.
	 */
	public boolean sendSMS(GmailMessage gmailMessage, boolean includeSummary)
	{
		boolean result = true;
		if (allowSMS()) {
			try {
				String content = getSMSContent(gmailMessage, includeSummary);
				SMS sms = Skype.sendSMS(Configuration.mobileNumber, content);
				Status status = sms.getStatus();
				if (status == Status.FAILED || status == Status.SOME_TARGETS_FAILED || status == Status.UNKNOWN) {
					result = false;
				}
			} catch (SkypeException e) {
				logger.error("Skype exception: " + e);
				result = false;
			}
			logSentSMS();
		}
		return result;
	}
}
