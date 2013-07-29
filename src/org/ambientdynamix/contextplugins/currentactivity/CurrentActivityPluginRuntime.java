package org.ambientdynamix.contextplugins.currentactivity;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.ambientdynamix.api.contextplugin.*;
import org.ambientdynamix.api.contextplugin.security.PrivacyRiskLevel;
import org.ambientdynamix.api.contextplugin.security.SecuredContextInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;


public class CurrentActivityPluginRuntime extends AutoReactiveContextPluginRuntime
{
	private final static String TAG = "CURRENTACTIVITY";
	private static CurrentActivityPluginRuntime context;
	private BroadcastReceiver receiver;
	static String activityname;

	@Override
	public void start() 
	{
		/*
		 * Nothing to do, since this is a pull plug-in... we're now waiting for context scan requests.
		 */
		context=this;
		Log.i(TAG, "Started!");
	}

	@Override
	public void stop() 
	{
		/*
		 * At this point, the plug-in should cancel any ongoing context scans, if there are any.
		 */
		Log.i(TAG, "Stopped!");
		//Timer.stop();
	}

	@Override
	public void destroy() 
	{
		/*
		 * At this point, the plug-in should release any resources.
		 */
		stop();
		getSecuredContext().unregisterReceiver(receiver);
		Log.i(TAG, "Destroyed!");
	}

	@Override
	public void updateSettings(ContextPluginSettings settings) 
	{
		// Not supported
	}

	@Override
	public void handleContextRequest(UUID requestId, String contextInfoType) 
	{
		Log.d(TAG, "normal context request");
		checkForActivity();
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.currentactivity"))
		{
			Log.d(TAG, "ok, send stuff");
			SecuredContextInfo aci= new SecuredContextInfo(new CurrentActivityContextInfo(activityname), PrivacyRiskLevel.LOW);
			sendContextEvent(requestId, aci, 1000);
		}
		context=this;
	}

	@Override
	public void handleConfiguredContextRequest(UUID requestId, String contextInfoType, Bundle scanConfig) 
	{
		Log.d(TAG, "configured context request");
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.currentactivity"))
		{
			handleContextRequest(requestId, contextInfoType);
		}
		context=this;
	}

	@Override
	public void init(PowerScheme arg0, ContextPluginSettings arg1) throws Exception 
	{
		Log.d(TAG, "init");
		//timer=new Timer();
		context=this;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPowerScheme(PowerScheme arg0) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doManualContextScan() 
	{
		// TODO Auto-generated method stub
		
	}
	
	public static void sendUpdate(String x)
	{
		SecuredContextInfo aci= new SecuredContextInfo(new CurrentActivityContextInfo(x), PrivacyRiskLevel.LOW);
		if(context!=null)
		{
			context.sendBroadcastContextEvent(aci, 1000);
		}
	}
	
	public static String checkForActivity()
	{
		Log.d(TAG, "check");
		if(context!=null)
		{
			Log.d(TAG, "context!=null");
			ActivityManager am = (ActivityManager)context.getSecuredContext().getSystemService(Context.ACTIVITY_SERVICE);
			List l = am.getRunningAppProcesses();
			Iterator i = l.iterator();
			PackageManager pm = context.getSecuredContext().getPackageManager();
			boolean foundone=false;
			while(i.hasNext()) 
			{
			  Log.d(TAG, "i.hasNext()");
			  ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
			  try
			  {
				Log.d(TAG, "try");
				Log.d(TAG, "process name "+info.processName);
				Log.d(TAG, ""+info.importance);
				if(info.importance==info.IMPORTANCE_FOREGROUND)
				{
					foundone=true;
					Log.d(TAG, "This sound like foreground");
					activityname=info.processName;
					StringTokenizer tk = new StringTokenizer(activityname, ".");
					String x =activityname;
					while(tk.hasMoreTokens())
					{
						x=tk.nextToken();
					}
					activityname=x;
				}
			    //CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
			    //Log.d(TAG, c.toString());
			    //activityname = c.toString();
			    Log.d(TAG, "still working");

			  }
			  catch(Exception e) 
			  {
				  Log.e(TAG, "Exception");
			    //Name Not FOund Exception
			  }
			}
			if(!foundone)
			{
				activityname="unknown";
				
			}
		}
		return "";
	}

}