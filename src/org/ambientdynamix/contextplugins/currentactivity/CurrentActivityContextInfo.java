package org.ambientdynamix.contextplugins.currentactivity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.ambientdynamix.api.application.IContextInfo;
import org.ambientdynamix.contextplugins.context.info.device.IDeviceScreenContextInfo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CurrentActivityContextInfo implements IContextInfo
{

	private final String TAG = "SCREENSTATUS";
	
	String activity;
	
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
		
	CurrentActivityContextInfo(String x)
	{
		activity=x;
	}
	
	public CurrentActivityContextInfo(Parcel in) 
	{
		activity = in.readString();
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
		out.writeString(activity);
	}

	@Override
	public String getContextType() 
	{
		return "org.ambientdynamix.contextplugins.context.info.device.currentactivity";
	}

	@Override
	public String getImplementingClassname() 
	{
		return this.getClass().getName();
	}

	@Override
	public String getStringRepresentation(String format) 
	{
		String result=activity;
		if (format.equalsIgnoreCase("text/plain"))
		{
			return result;
		}
		else if (format.equalsIgnoreCase("XML"))
		{
			return "<data><currentapplication>"+result+"</currentapplication></data>";
		}
		else if (format.equalsIgnoreCase("JSON"))
		{
			return " ";
		}
		else
			return null;
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

	public String currentActivity() 
	{
		return activity;
	}

}