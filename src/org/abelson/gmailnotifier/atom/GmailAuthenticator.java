
package org.abelson.gmailnotifier.atom;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import org.abelson.gmailnotifier.Configuration;

/**
 * This class provides the authentication necessary for accessing 
 * a Gmail Atom feed.  
 */
public class GmailAuthenticator extends Authenticator
{
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(Configuration.gmailUsername, Configuration.gmailPassword.toCharArray());
    }
}
