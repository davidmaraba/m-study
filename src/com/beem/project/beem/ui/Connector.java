package com.beem.project.beem.ui;


import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import com.beem.project.beem.BeemService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class Connector {
	
	private SharedPreferences mSettings;//s=BeemService.mSettings;
	private String username,password;
	/** Preference key for account username. */
    public static final String ACCOUNT_USERNAME_KEY = "account_username";
    /** Preference key for account password. */
    public static final String ACCOUNT_PASSWORD_KEY = "account_password";
    Context _context;
	
	public Connector(Context context)
	{
		this._context=context;
		//mSettings=_context.getSharedPreferences(arg0, arg1)
		mSettings = PreferenceManager.getDefaultSharedPreferences(_context);
		username=mSettings.getString(Connector.ACCOUNT_USERNAME_KEY, "");
		password=mSettings.getString(Connector.ACCOUNT_PASSWORD_KEY, "");
		
	}
	public Connection connectToServer()
	{
		ConnectionConfiguration config = new ConnectionConfiguration("10.0.2.2", 5222);
		Connection conn2 = new XMPPConnection(config);
		try {
			conn2.connect();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		configure(ProviderManager.getInstance());
		try {
			conn2.login(username,password);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn2;
	}
	public void configure(ProviderManager pm) {

	    //  Private Data Storage
	    pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

	    //  Time
	    try {
	        pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
	    } catch (ClassNotFoundException e) {
	        Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
	    }

	    //  Roster Exchange
	    pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());

	    //  Message Events
	    pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());

	    //  Chat State
	    pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
	    pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider()); 
	    pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
	    pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
	    pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

	    //  XHTML
	    pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

	    //  Group Chat Invitations
	    pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());

	    //  Service Discovery # Items    
	    pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

	    //  Service Discovery # Info
	    pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

	    //  Data Forms
	    pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());

	    //  MUC User
	    pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());

	    //  MUC Admin    
	    pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

	    //  MUC Owner    
	    pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

	    //  Delayed Delivery
	    pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());

	    //  Version
	    try {
	        pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
	    } catch (ClassNotFoundException e) {
	        //  Not sure what's happening here.
	    }

	    //  VCard
	    pm.addIQProvider("vCard","vcard-temp", new VCardProvider());

	    //  Offline Message Requests
	    pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

	    //  Offline Message Indicator
	    pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

	    //  Last Activity
	    pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());

	    //  User Search
	    pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());

	    //  SharedGroupsInfo
	    pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

	    //  JEP-33: Extended Stanza Addressing
	    pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

	    //   FileTransfer
	    pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());

	    pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

	    //  Privacy
	    pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());
	    pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
	    pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
	    pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
	    pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
	    pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
	    pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
	}
	


}
