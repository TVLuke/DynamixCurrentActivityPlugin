package org.ambientdynamix.contextplugins.currentactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.ambientdynamix.api.contextplugin.*;
import org.ambientdynamix.api.contextplugin.security.PrivacyRiskLevel;
import org.ambientdynamix.api.contextplugin.security.SecuredContextInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class CurrentActivityPluginRuntime extends AutoReactiveContextPluginRuntime
{
	private final static String TAG = "CURRENTACTIVITY";
	private static CurrentActivityPluginRuntime context;
	private BroadcastReceiver receiver;
	static ArrayList<String> currentApplications = new ArrayList<String>();
	static ConcurrentHashMap<String, Application> runningApplications = new ConcurrentHashMap<String, Application>();
	Timer timer;
	private static boolean checkrunning=false;
	
	@Override
	public void start() 
	{
		/*
		 * Nothing to do, since this is a pull plug-in... we're now waiting for context scan requests.
		 */
		context=this;
		timer=new Timer();
		Log.i(TAG, "Started!");
	}

	@Override
	public void stop() 
	{
		/*
		 * At this point, the plug-in should cancel any ongoing context scans, if there are any.
		 */
		Log.i(TAG, "Stopped!");
		Timer.stop();
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
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.frontapplications"))
		{
			Log.d(TAG, "ok, send stuff");
			SecuredContextInfo aci= new SecuredContextInfo(new CurrentActivityContextInfo(), PrivacyRiskLevel.HIGH);
			sendContextEvent(requestId, aci, 1000);
		}
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.runningapplications"))
		{
			Log.d(TAG, "ok, send stuff");
			SecuredContextInfo aci= new SecuredContextInfo(new RunningActivitiesContextInfo(), PrivacyRiskLevel.HIGH);
			sendContextEvent(requestId, aci, 30000);
		}
		context=this;
	}

	@Override
	public void handleConfiguredContextRequest(UUID requestId, String contextInfoType, Bundle scanConfig) 
	{
		Log.d(TAG, "configured context request");
		checkForActivity();
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.frontapplications"))
		{
			handleContextRequest(requestId, contextInfoType);
		}
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.runningapplications"))
		{
			handleContextRequest(requestId, contextInfoType);
		}
		context=this;
	}

	@Override
	public void init(PowerScheme arg0, ContextPluginSettings arg1) throws Exception 
	{
		Log.d(TAG, "init");
		timer=new Timer();
		context=this;
		checkForActivity();
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
		checkForActivity();
	}
	
	public static void sendUpdate()
	{
		SecuredContextInfo aci= new SecuredContextInfo(new CurrentActivityContextInfo(), PrivacyRiskLevel.LOW);
		if(context!=null)
		{
			context.sendBroadcastContextEvent(aci, 1000);
		}
	}
	
	public static void checkForActivity()
	{
		if(!checkrunning)
		{
			checkrunning=true;
		
		new Thread(new Runnable() 
        {
            public void run() 
            {
				Log.d(TAG, "check");
				if(context!=null)
				{
					//Set the importance to 0 for all apps...
					if(runningApplications.size()>0)
					{
						Set<String> keys = runningApplications.keySet();
						Iterator<String> it = keys.iterator();
						while(it.hasNext())
						{
							String key = it.next();
							Application a = runningApplications.get(key);
							a.setStillRunningFlag(false);
						}
					}
					//Log.d(TAG, "context!=null");
					ActivityManager am = (ActivityManager)context.getSecuredContext().getSystemService(Context.ACTIVITY_SERVICE);
					List l = am.getRunningAppProcesses();
					Iterator i = l.iterator();
					PackageManager pm = context.getSecuredContext().getPackageManager();
					boolean foundone=false;
					ArrayList<String> names = new ArrayList<String>();
					while(i.hasNext()) 
					{
						  //Log.d(TAG, "i.hasNext()");
						  ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
						  try
						  {
							//Log.d(TAG, "try");
							//Log.d(TAG, "process name "+info.processName);
							names.add(info.processName);
							String playurl ="https://play.google.com/store/apps/details?id="+info.processName;
							Application a=null;
							if(!info.processName.startsWith("com.google.process") 
									&& !info.processName.startsWith("com.google.android.core") 
									&& !info.processName.startsWith("com.google.android.deskclock") 
									&& !info.processName.equals("system") 
									&& !info.processName.startsWith("com.android.phone") 
									&& !info.processName.startsWith("com.google.android.inputmethod") 
									&& !info.processName.startsWith("com.android.location")
									&& !info.processName.startsWith("com.android.bluetooth")
									&& !info.processName.startsWith("com.android.phasebeam")
									&& !info.processName.startsWith("com.android.launcher")
									&& !info.processName.startsWith("com.android.systemui")
									&& !info.processName.startsWith("com.google.android.gsf")
									&& !info.processName.startsWith("com.android.settings")
									&& !info.processName.startsWith("android.process")
									&& !info.processName.startsWith("org.ambientdynamix.core")
									&& !info.processName.startsWith("org.google.android.calendar")
									&& !info.processName.startsWith("com.android.providers.calendar")
									&& !info.processName.startsWith("com.google.android.browser")
									&& !info.processName.startsWith("com.android.nfc")
									&& !info.processName.startsWith("com.google.android.apps.maps:FriendService")
									&& !info.processName.startsWith("com.google.android.apps.maps:LocationFriendService")
									&& !info.processName.startsWith("com.google.android.apps.maps:GoogleLocationService")
									&& !info.processName.startsWith("com.google.android.apps.genie.geniewidget")
									&& !info.processName.startsWith("com.google.android.gallery3d")
									&& !info.processName.startsWith("com.google.android.apps.uploader")
									&& !info.processName.startsWith("de.uniluebeck.itm.dynamixsspbridge")
									&& !info.processName.startsWith("com.android.defcontainer")
									&& !info.processName.startsWith("com.android.vending")
									&& !info.processName.startsWith("com.android.musicfx")
									&& !info.processName.startsWith("com.google.android.partnersetup")
									)
							{
								if(!runningApplications.containsKey(info.processName))
								{
									a = getApplication(playurl);
									a.setImportance(info.importance);
									a.setStillRunningFlag(true);
								}
								else
								{
									a=runningApplications.get(info.processName);
									a.setImportance(info.importance);
									a.setStillRunningFlag(true);
								}
							}
							//Log.d(TAG, ""+info.importance);
							if(a!=null)
							{
								
							}
							else
							{
								String x =info.processName;
								//some manual rules...
								if(info.processName.equals("org.ambientdynamix.core"))
								{
									x="ambientdynamix";
								}
								if(x.contains("com.google.android."))
								{
									x=x.replace("com.google.android.", "");
								}
								if(x.contains("com.android."))
								{
									x=x.replace("com.android.", "");
		
								}
								if(x.contains("com.android.providers."))
								{
									x=x.replace("com.android.providers.", "");
								}
								if(x.contains("android."))
								{
									x=x.replace("android.", "");
								}
								if(x.contains("aps.maps:"))
								{
									x=x.replace("aps.maps:", "");
								}
								if(!runningApplications.containsKey(info.processName))
								{
									a = new Application("", "", x, "no category", "no description", 0, info.processName);
									a.setImportance(info.importance);
									a.setStillRunningFlag(true);
								}
								else
								{
									a=runningApplications.get(info.processName);
									a.setImportance(info.importance);
									a.setStillRunningFlag(true);
								}
							}
							a.setImportance(info.importance);
							a.setStillRunningFlag(true);
							runningApplications.put(info.processName, a);
						  }
						  catch(Exception e) 
						  {
							  Log.e(TAG, "Exception");
						    //Name Not FOund Exception
						  }
					}
					if(runningApplications.size()>0)
					{
						Set<String> keys = runningApplications.keySet();
						Iterator<String> it = keys.iterator();
						while(it.hasNext())
						{
							String key = it.next();
							Application a = runningApplications.get(key);
							if(a.getImportance()==RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
							{
								//Log.d(TAG, a.getProcessName());
								//Log.d(TAG, a.getAppName());
								//check for all if they are in the arrayList of Names of currently running
								if(!currentApplications.contains(a.getAppName()) && currentApplications.size()>0)
								{
									
									//if not, send update for new front activity
									sendUpdate();
								}
								//write all the currently running ones in.
							}
						}
						
						//clear the list
						currentApplications.clear();
						//renew the list
						Iterator<String> it2 = keys.iterator();
						while(it2.hasNext())
						{
							String key = it2.next();
							Application a = runningApplications.get(key);
							if(a.getImportance()==RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
							{
								Log.d(TAG, "->"+a.getAppName());
								currentApplications.add(a.getAppName());
							}
							if(!a.getStillRunningFlag())
							{
								Log.d(TAG, "remove "+a.getAppName());
								runningApplications.remove(key);
							}
						}
							
					}
				    //Log.d(TAG, "still working");
				}
				checkrunning=false;
            }
        }).start();
		}
	}


	private static Application getApplication(String xx)
	{
		Application a = null;
		try 
		{
			//Log.d(TAG, "try "+xx);
			String playurl = xx;
			String picurl = "";
			String appName="";
			String appCategory="";
			String appDescription="";
			int appRating=0;
			Document doc = Jsoup.connect(playurl).get();
			List<Node> nodes = doc.childNodes();
			String htmlpage = doc.toString();
			if(htmlpage.contains("<body>"))
			{
				//Log.d(TAG, "does contain body");
				//Log.d(TAG, ""+htmlpage.indexOf("<body>"));
				int start =htmlpage.indexOf("<body>");
				int end = htmlpage.indexOf("</body>");
				int length = htmlpage.length();
				//Log.d(TAG, "The page has "+length+"symbols. Start should be at "+start+" and the end at "+end);
				//Log.d(TAG, ""+(end-start));
				htmlpage = htmlpage.substring(start, end);
				//Log.d(TAG, "new length "+htmlpage.length());
				//get the cover url
				htmlpage = htmlpage.replace("\"", "");			
				String covercontainer = htmlpage.substring(htmlpage.indexOf("<img class=cover-image"));
				//Log.d(TAG, ""+covercontainer.length());
				covercontainer  = covercontainer.substring(0, covercontainer.indexOf("</div>"));
				covercontainer = covercontainer.replace("<img class=cover-image src=", "");
				covercontainer = covercontainer.replace(" alt=Cover art itemprop=image />", "");
				picurl=covercontainer;
				//get the name
				int x = htmlpage.indexOf("info-container");
				//Log.d(TAG, ""+x);
				String infocontainer = htmlpage.substring(x);
				int y = infocontainer.indexOf("details-actions");
				//Log.d(TAG, ""+y);
				infocontainer = infocontainer.substring(0, y);
				//Tag.d(TAG, infocontainer);
				String name = infocontainer.substring(infocontainer.indexOf("<div>"), infocontainer.indexOf("</div>"));
				name=name.replace("<div>", "");
				name=name.trim();
				appName=name;
				//get categorie
				String cat = infocontainer.substring(infocontainer.indexOf("<a class=document-subtitle category"));
				//Tag.d(TAG, cat);
				cat = cat.substring(cat.indexOf(">"), cat.indexOf("</a>"));
				cat=cat.replace(">", "");
				cat=cat.replace("&amp;", "&");
				//Tag.d(TAG, cat);
				appCategory = cat;

				String descr = htmlpage.substring(htmlpage.indexOf("itemprop=description>"));
				descr = descr.substring(0, descr.indexOf("</div>"));
				descr=descr.replace("itemprop=description>", "");
				descr=descr.replace("<div>", "");
				descr=descr.trim();
				descr=descr.replace("<br />", " ");
				descr=descr.replace("<br>", " ");
				descr=descr.replace("&uuml;", "ü");
				descr=descr.replace("&auml;", "ä");
				descr=descr.replace("&ouml;", "ö");
				descr=descr.replace("<p>", " ");
				descr=descr.replace("</p>", " ");
				descr=descr.replace("•", "");
				descr=descr.replace("<", "");
				descr=descr.replace(">", "");
				descr=descr.replace("a href=", "");
				descr.replace("            ", " ");
				descr.replace("           ", " ");
				descr.replace("          ", " ");
				descr.replace("         ", " ");
				descr.replace("        ", " ");
				descr.replace("       ", " ");
				descr.replace("      ", " ");
				descr.replace("     ", " ");
				descr.replace("    ", " ");
				descr.replace("   ", " ");
				descr.replace("  ", " ");
				descr=descr.replace("\t", "");
				descr=descr.replace("\n", " ");
				descr=descr.replace("&quot;", "\"");
				
				//Tag.d(TAG, descr);
				appDescription=descr;
				//Tag.d(TAG, htmlpage.substring(10000, 15000));
				//get rating
				String rating = htmlpage.substring(htmlpage.indexOf("itemprop=contentRating>"));
				rating = rating.substring(0, rating.indexOf("</div>"));
				rating=rating.replace("itemprop=contentRating>", "");
				
				if(rating.contains("3"))
				{
					appRating=3;
				}
				else if(rating.contains("2"))
				{
					appRating=2;
				}
				else if(rating.contains("1"))
				{
					appRating=1;
				}
			}
			String processname=xx.substring(xx.indexOf("?id="));
			processname=processname.replace("?id=", "");
			a = new Application(playurl, picurl, appName, appCategory, appDescription, appRating, processname);
			return a;
		} 
		catch (IOException e) 
		{
			Log.d(TAG, "oh noes exception");
			Log.e(TAG, xx);
			Log.e(TAG, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}
	
	public static ConcurrentHashMap<String, Application> getRunningApplications()
	{
		return runningApplications;
	}
}