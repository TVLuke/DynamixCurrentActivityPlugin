package org.ambientdynamix.contextplugins.currentactivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ambientdynamix.api.application.IContextInfo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RunningActivitiesContextInfo implements IContextInfo
{

	private final String TAG = "SCREENSTATUS";
	
	List<Application> frontactivitys = new ArrayList<Application>();
	
	public static Parcelable.Creator<RunningActivitiesContextInfo> CREATOR = new Parcelable.Creator<RunningActivitiesContextInfo>() 
			{
			public RunningActivitiesContextInfo createFromParcel(Parcel in) 
			{
				return new RunningActivitiesContextInfo(in);
			}

			public RunningActivitiesContextInfo[] newArray(int size) 
			{
				return new RunningActivitiesContextInfo[size];
			}
		};
		
		RunningActivitiesContextInfo()
	{
		Log.d(TAG, "create new Context Info Object");
		ConcurrentHashMap<String, Application> aps = CurrentActivityPluginRuntime.getRunningApplications();
		Set<String> keys = aps.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext())
		{
			String key = it.next();
			Application a = aps.get(key);
			Log.d(TAG, "put "+a.getAppName()+" in the List");
			frontactivitys.add(a);
		}
	}
	
	public RunningActivitiesContextInfo(Parcel in) 
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
		return "org.ambientdynamix.contextplugins.context.info.device.runningapplications";
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
			result=result+"";
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
			result=result+"";
		}
		else if (format.equalsIgnoreCase("JSON"))
		{
			result=result+"{\n";
			for(int i=0; i<frontactivitys.size(); i++)
			{
				Application a = frontactivitys.get(i);
				result=result+"  \"application\": {\n";
				result=result+"  \"name\": \""+a.getAppName()+"\"";
				result=result+"  \"processName\": \""+a.getProcessName()+"\"";
				result=result+"  \"description\": \""+a.getAppDescription()+"\"";
				result=result+"  \"runtime\": \""+a.getRunntime()+"\"";
				result=result+"  }\n";
			}	
			result=result+"}\n";
		}
		else if(format.equalsIgnoreCase("RDF/XML"))
		{
			result="<rdf:RDF\n" +
					"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
					"xmlns:z.0=\"http://dynamix.org/semmodel/org.ambientdynamix.contextplugins.currentactivity/0.1/\"\n" +
					"xmlns:z.1=\"http://dynamix.org/semmodel/0.1/\" > \n";
			for(int i=0; i<frontactivitys.size(); i++)
			{
				Application a = frontactivitys.get(i);
				result=result+" <rdf:Description rdf:about=\"http://dynamix.org/semmodel/org.ambientdynamix.contextplugins.currentactivity/0.1/application/"+a.getProcessName()+"_"+CurrentActivityPluginRuntime.deviceid+"\">\n";
				result=result+" <rdf:type>org.ambientdynamix.contextplugins.context.info.device.runningapplications</rdf:type>\n";
				result=result+"<z.0:hasName>"+a.getAppName()+"</z.0:hasName>\n" +
							  "<z.0:hasProcessName>"+a.getProcessName()+"</z.0:hasProcessName>\n" +
							  "<z.0:hasDescription>"+a.getAppDescription()+"</z.0:hasDescription>\n" +				
							  "<z.0:hasRuntime>"+a.getRunntime()+"</z.0:hasRuntime>\n" +	
							  "</rdf:Description>\n";
			}
			result=result+"</rdf:RDF>";
			return result;
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
		formats.add("RDF/XML");
		return formats;
	}
}