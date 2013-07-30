package org.ambientdynamix.contextplugins.currentactivity;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.ambientdynamix.api.contextplugin.*;
import org.ambientdynamix.api.contextplugin.security.PrivacyRiskLevel;
import org.ambientdynamix.api.contextplugin.security.SecuredContextInfo;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

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
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.frontapplication"))
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
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.frontapplication"))
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
			activityname="";
			while(i.hasNext()) 
			{
			  Log.d(TAG, "i.hasNext()");
			  ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
			  try
			  {
				Log.d(TAG, "try");
				Log.d(TAG, "process name "+info.processName);
				Log.d(TAG, ""+info.importance);
				if(info.importance==info.IMPORTANCE_FOREGROUND && !info.processName.equals("com.android.phasebeam"))
				{
					foundone=true;
					Log.d(TAG, "This sound like foreground");
					String y= info.processName;
					StringTokenizer tk = new StringTokenizer(y, ".");
					String x =info.processName;
					final String playurl ="https://play.google.com/store/apps/details?id="+info.processName;
					//TODO: get dateiled info from the play store
					final SAXBuilder builder = new SAXBuilder();
					new Thread(new Runnable() 
	                {
	                    public void run() 
	                    {
	                                    // command line should offer URIs or file names
	                                    try 
	                                    {
	                                        Document doc = builder.build(playurl);
	                                        Element root = doc.getRootElement();
	                                        exploreChildren(root);
	                                      // If there are no well-formedness errors, 
	                                      // then no exception is thrown
	                                    }
	                                    // indicates a well-formedness error
	                                    catch (JDOMException e) 
	                                    { 
	                                      Log.d(TAG,playurl + " is not well-formed.");
	                                      Log.d(TAG,e.getMessage());
	                                      Log.d(TAG, e.getMessage());
	                                    }  
	                                    catch (IOException e) 
	                                    { 
	                                      Log.d(TAG,"Could not check " + playurl);
	                                      Log.d(TAG," because " + e.getMessage());
	                                      Log.d(TAG, e.getMessage());
	                                    } 
	                    	}
	        
	                }).start();
					
					while(tk.hasMoreTokens())
					{
						x=tk.nextToken();
					}
					if(info.processName.equals("org.ambientdynamix.core"))
					{
						x="ambientdynamix";
					}
					if(x.equals("gm"))
					{
						x="google mail";
					}
					activityname=activityname+" "+x;
					if(activityname.equals(" system"))
					{
						activityname.replace(" system", "");
					}
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
	
	private static void exploreChildren(Element element)
	{
		List<Element> children = element.getChildren();
		Iterator<Element> childrenIterator = children.iterator();
        while(childrenIterator.hasNext())
        {
        	Element child = childrenIterator.next(); 
        	String name = child.getName();
        	Log.d(TAG, "name="+name);
        	List<Attribute> attributes = child.getAttributes();
        	Iterator<Attribute> attributeIterator = attributes.iterator();
        	while(attributeIterator.hasNext())
        	{
        		Attribute attribute = attributeIterator.next();
        		Log.d(TAG, "attributes="+attribute.getName()+ " "+attribute.getValue());
        	}
        	exploreChildren(child);
        }
	}

}