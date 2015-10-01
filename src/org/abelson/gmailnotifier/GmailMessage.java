
package org.abelson.gmailnotifier;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class contains information about one Gmail message.
 * @author Carl Fredrik Abelson
 */
public class GmailMessage
{
	private Element entry;

	/**
	 * Constructs a GmailMessage object from a Gmail Atom feed entry.
	 * @param atomFeedEntry XML element containing the information
	 * about the Gmail message.
	 */
	public GmailMessage(Element atomFeedEntry)
	{
		this.entry = atomFeedEntry;
	}

	/**
	 * Returns the name of the person who sent the Gmail message.
	 * @return The name of the person who sent the Gmail message.
	 */
	public String getFromName()
	{
		return getInfoWithName("name");
	}

	/**
	 * Returns the e-mail address of the person who sent the Gmail message.
	 * @return The e-mail address of the person who sent the Gmail message.
	 */
	public String getFromMail()
	{
		return getInfoWithName("email");
	}

	/**
	 * Returns the subject of the Gmail message.
	 * @return The subject of the Gmail message.
	 */
	public String getTitle()
	{
		return getInfoWithName("title");
	}

	/**
	 * Returns a summary of the body of Gmail message.
	 * @return A summary of the body of Gmail message.
	 */
	public String getSummary()
	{
		return getInfoWithName("summary");
	}

	private String getInfoWithName(String name)
	{
		String value = "";
		NodeList elements = entry.getElementsByTagName(name);
		if (elements.getLength() > 0) {
			Node element = elements.item(0);
			if (element != null) {
				value = element.getTextContent();
			}
		}
		return value;
	}
}
