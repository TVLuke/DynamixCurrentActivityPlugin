package org.ambientdynamix.contextplugins.currentactivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ambientdynamix.api.application.IContextInfo;
import org.ambientdynamix.contextplugins.context.info.device.IDeviceScreenContextInfo;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CurrentActivityContextInfo implements IContextInfo
{

	private final String TAG = "SCREENSTATUS";
	
	List<Application> frontactivitys = new ArrayList<Application>();
	
	public static Parcelable.Creator<CurrentActivityContextInfo> CREATOR = new Parcelable.Creator<CurrentActivityContextInfo>() 
			{
			public CurrentActivityContextInfo createFromParcel(Parcel in) 
			{
				return new CurrentActivityContextInfo(in);
			}

			public CurrentActivityContextInfo[] newArray(int size) 
			{
				return new CurrentActivityContextInfo[size];
			}
		};
		
	CurrentActivityContextInfo()
	{
		Log.d(TAG, "create new Context Info Object");
		ConcurrentHashMap<String, Application> aps = CurrentActivityPluginRuntime.getRunningApplications();
		Set<String> keys = aps.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext())
		{
			String key = it.next();
			Application a = aps.get(key);
			if(a.getImportance()==RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
			{
				
				Log.d(TAG, "put "+a.getAppName()+" in the List");
				
				frontactivitys.add(a);
			}
		}
	}
	
	public CurrentActivityContextInfo(Parcel in) 
	{
		in.readList(frontactivitys, getClass().getClassLoader());
	}

	@Override
	public String toString() 
	{
		return this.getClass().getSimpleName();
	}
	
	@Override
	public int describeContents() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		out.writeList(frontactivitys);
	}

	@Override
	public String getContextType() 
	{
		return "org.ambientdynamix.contextplugins.context.info.device.frontapplications";
	}

	@Override
	public String getImplementingClassname() 
	{
		return this.getClass().getName();
	}

	@Override
	public String getStringRepresentation(String format) 
	{
		String result="";
		if (format.equalsIgnoreCase("text/plain"))
		{
			for(int i=0; i<frontactivitys.size(); i++)
			{
				Application a = frontactivitys.get(i);
				result=result+a.getAppName()+"\n";
			}
		}
		else if (format.equalsIgnoreCase("XML"))
		{
			result=result+"<data>\n";
			for(int i=0; i<frontactivitys.size(); i++)
			{
				Application a = frontactivitys.get(i);
				result=result+" <application>\n";
				result=result+"  <name>"+a.getAppName()+"</name>\n";
				result=result+"  <processName>"+a.getProcessName()+"</processName>\n";
				result=result+"  <description>"+a.getAppDescription()+"</description>\n";
				result=result+"  <runtime>"+a.getRunntime()+"</runtime>\n";
				result=result+" </application>";
			}	
			result=result+"</data>\n";
		}
		else if (format.equalsIgnoreCase("JSON"))
		{
			for(int i=0; i<frontactivitys.size(); i++)
			{
				
			}
		}
		return result;
	}

	@Override
	public Set<String> getStringRepresentationFormats() 
	{
		Set<String> formats = new HashSet<String>();
		formats.add("text/plain");
		formats.add("XML");
		formats.add("JSON");
		return formats;
	}
}