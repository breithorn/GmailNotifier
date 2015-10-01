
package org.abelson.gmailnotifier.test;

import static org.junit.Assert.assertEquals;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.JUnit4TestAdapter;
import org.abelson.gmailnotifier.GmailMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GmailMessageTest
{
	private static Document xml;

	@Before
	public void setUp() throws Exception
	{
		String inputFile = "tests/inputdata/atom_example_message.xml";
		FileInputStream input = new FileInputStream(inputFile);
		DocumentBuilder documentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xml = documentbuilder.parse(input);
	}

	@After
	public void tearDown() throws Exception
	{
		xml = null;
	}

	@Test
	public void testExtractInfo1()
	{
		Element root = xml.getDocumentElement();
		NodeList children = root.getElementsByTagName("entry");
		Element entry = (Element)children.item(0);
		GmailMessage message = new GmailMessage(entry);

		String name = message.getFromName();
		assertEquals("Jan Banan", name);

		String email = message.getFromMail();
		assertEquals("jan@ban.an", email);

		String title = message.getTitle();
		assertEquals("test", title);

		String summary = message.getSummary();
		assertEquals("This is a test message", summary);
	}
 
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(GmailMessageTest.class);
	}
}
