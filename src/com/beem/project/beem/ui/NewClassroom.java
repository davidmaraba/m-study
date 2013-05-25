
package com.beem.project.beem.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beem.project.beem.BeemService;
import com.beem.project.beem.R;
import com.beem.project.beem.service.aidl.IXmppFacade;
import com.beem.project.beem.utils.BeemBroadcastReceiver;

/**
 * This activity is used to add a contact.
 * @author nikita
 */
public class NewClassroom extends Activity {

    private static final Intent SERVICE_INTENT = new Intent();
    private static final String TAG = "NewClassroom";
    private final List<String> mGroup = new ArrayList<String>();
    private IXmppFacade mXmppFacade;
    private final ServiceConnection mServConn = new BeemServiceConnection();
    private final BeemBroadcastReceiver mReceiver = new BeemBroadcastReceiver();
    private final OkListener mOkListener = new OkListener();
    private Connector conn;

    static {
	SERVICE_INTENT.setComponent(new ComponentName("com.beem.project.beem", "com.beem.project.beem.BeemService"));
    }

    /**
     * Constructor.
     */
    public NewClassroom() {
    	conn=new Connector(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.newclassroom);
	Button ok = (Button) findViewById(R.id.newclass_ok);
	ok.setOnClickListener(mOkListener);
	this.registerReceiver(mReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
	super.onStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
	super.onResume();
	bindService(new Intent(this, BeemService.class), mServConn, BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
	super.onPause();
	unbindService(mServConn);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
	super.onDestroy();
	this.unregisterReceiver(mReceiver);
    }

    /**
     * The ServiceConnection used to connect to the Beem service.
     */
    private class BeemServiceConnection implements ServiceConnection {

	/**
	 * Constructor.
	 */
	public BeemServiceConnection() {
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    mXmppFacade = IXmppFacade.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	    mXmppFacade = null;
	}
    }

    /**
     * Get the text of a widget.
     * @param id the id of the widget.
     * @return the text of the widget.
     */
    private String getWidgetText(int id) {
	EditText widget = (EditText) this.findViewById(id);
	return widget.getText().toString();
    }

    /**
     * Listener.
     */
    private class OkListener implements OnClickListener {

	/**
	 * Constructor.
	 */
	public OkListener() { }

	@Override
	public void onClick(View v) {
	    String classname;
	    classname = getWidgetText(R.id.class_name);
	    if (classname.length() == 0) {
		Toast.makeText(NewClassroom.this, getString(R.string.AddCBadForm), Toast.LENGTH_SHORT).show();
		return;
	    }
	   
	    String alias;
	    alias = getWidgetText(R.id.newclass_alias);
	 
	  //create a Multiuser connection using connection for a room
	  		MultiUserChat muc = null;
			
				muc = new MultiUserChat(conn.connectToServer(), classname+"@conference.localhost");
			

	  		Log.d("Nickname",classname);
	  		//create room
	  		try {
	  			
	  			muc.create("Home");
	  			muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
	  			muc.join("Home");

	  		} catch (XMPPException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}

	}
    };
}
