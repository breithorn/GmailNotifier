
package org.abelson.gmailnotifier.atom;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * General class for handling Atom feeds.
 * @author Carl Fredrik Abelson
 */
public class AtomFeed
{
	// Log4j
	private static Logger logger = Logger.getLogger(AtomFeed.class);

	/**
	 * Network timeout in milliseconds. Default 15 minutes.
	 */
	private static final int atomFeedTimeout = 15 * 60 * 1000;

	/**
	 * The URL where the Atom feed can be retrieved.
	 */
	protected String feedURL;

	/**
	 * XML document containing the Atom feed.
	 */
	protected Document xml;

	/**
	 * Used when creating XML documents.
	 */
	private DocumentBuilder documentbuilder;

	/**
	 * Initializes the AtomFeed and retrieves a new feed from the server.
	 * @param feedURL The URL for the Atom feed.
	 * @throws ParserConfigurationException 
	 */
	public AtomFeed(String feedURL) throws ParserConfigurationException, IOException
	{
		this.feedURL = feedURL;
		xml = null;
		documentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		if (!refresh()) throw new IOException();
	}

	/**
	 * Retrieves a new Atom feed from the server.
	 * @return true if succeeded in getting a new feed.
	 */
	public boolean refresh()
	{
		boolean result = true;
		Document oldxml = xml;
		try {
			URL url = new URL(feedURL);
			URLConnection conn = url.openConnection();
			// setting these timeouts ensures the client does not deadlock indefinitely
			// when the server has problems.
			conn.setConnectTimeout(atomFeedTimeout);
			conn.setReadTimeout(atomFeedTimeout);
	        xml = documentbuilder.parse(conn.getInputStream());
		} catch (MalformedURLException e) {
			logger.error("Error [MalformedURLException]: " + e);
			xml = oldxml;
			result = false;
		} catch (IOException e) {
			logger.error("Error [IOException]: " + e);
			xml = oldxml;
			result = false;
		} catch (SAXException e) {
			logger.error("Error [SAXException]: " + e);
			xml = oldxml;
			result = false;
		}
		return result;
	}
}
