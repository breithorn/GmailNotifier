
package org.abelson.gmailnotifier.atom;

import java.io.IOException;
import java.net.Authenticator;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import org.abelson.gmailnotifier.GmailMessage;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles Atom feeds from Gmail.
 */
public class GmailAtomFeed extends AtomFeed
{
	// Log4j
	private static Logger logger = Logger.getLogger(GmailAtomFeed.class);

	/**
	 * The URL to the Gmail Atom feed.
	 */
	private static final String gmailAtomURL = "https://mail.google.com/mail/feed/atom";

	/**
	 * The number of new messages in the previous Atom feed.
	 * This is used to calculate a delta number, and thus find
	 * out if there are any messages that have not been sent
	 * notification about.
	 */
	protected int numberPreviousMessages;

	/**
	 * Initializes the AtomFeed and retrieves a new feed from the server.
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 */
	public GmailAtomFeed() throws IOException, ParserConfigurationException
	{
		super(gmailAtomURL);
		numberPreviousMessages = 0;
	}

	/**
	 * Returns a vector with information about the new Gmail messages.
	 * Note that it does not contain all unread messages, only the ones 
	 * that have not been returned before.
	 * @return A vector containing information about the new Gmail messages
	 */
	public Vector<GmailMessage> getNewGmailMessages()
	{
		return getNewGmailMessages(true);
	}

	/**
	 * Retrieves a new Gmail Atom feed from the server.
	 * @return true if succeeded in getting a new feed.
	 */
	public boolean refresh()
	{
		Authenticator.setDefault(new GmailAuthenticator());
		return super.refresh();
	}

	/**
	 * Returns a vector with information about the new Gmail messages.
	 * @param onlyReallyNewMessages If only the messages that have not been 
	 * reported as new before should be returned. Default is <tt>true</tt>.
	 * @return A vector containing information about the new Gmail messages
	 */
	public Vector<GmailMessage> getNewGmailMessages(boolean onlyReallyNewMessages)
	{
		Vector<GmailMessage> messages = new Vector<GmailMessage>();
		int numberNewMessages = 0;
		Element root = xml.getDocumentElement();
		if (root != null) {
			NodeList children = root.getElementsByTagName("fullcount");
			if (children.getLength() > 0) {
				Node node = children.item(0);
				if (node != null) {
					String count = node.getTextContent();
					int n = 0;
					try {
						n = Integer.parseInt(count);
					} catch (NumberFormatException e) {
						n = 0;
					}
					numberNewMessages = n - numberPreviousMessages;
					if (numberNewMessages < 0) {
						numberNewMessages = 0;
					}
					numberPreviousMessages = n;
					logger.info(n + " messsages in total, with " + numberNewMessages + " new");
					if (!onlyReallyNewMessages) {
						numberNewMessages = n;
					}
				}
			}
			if (numberNewMessages > 0) {
				children = root.getElementsByTagName("entry");
				if (children.getLength() > 0) {
					for (int i = 0; i < numberNewMessages; i++) {
						Node node = children.item(i);
						if (node != null) {
					        Element element = (Element)xml.importNode(node, true);
					        messages.add(new GmailMessage(element));
						}
					}
				}
			}
		}
		return messages;
	}
}
