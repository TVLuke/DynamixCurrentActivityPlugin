package org.ambientdynamix.contextplugins.currentactivity;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Application implements Parcelable 
{

	private final static String TAG = "CURRENTACTIVITY";
	
	String playurl="";
	String picurl = "";
	String appName="";
	String appCategory="";
	String appDescription="";
	String processName="";
	int appRating=0;
	int importance=0;
	long startAt=0;
	boolean stillrunning=false;
	/**
     * Static Creator factory for Parcelable.
     */
    public static Parcelable.Creator<Application > CREATOR = new Parcelable.Creator<Application >() 
    {
		public Application  createFromParcel(Parcel in) 
		{
		    return new Application (in);
		}
	
		public Application[] newArray(int size) 
		{
		    return new Application [size];
		}
    };
    
	public Application(String playurl, String picurl, String appName, String appCategory, String appDescription, int appRating, String processName)
	{
		Log.d(TAG, "new applicstion "+appName);
		this.playurl=playurl;
		this.picurl=picurl;
		this.appName=appName;
		this.appCategory=appCategory;
		this.appDescription = appDescription;
		this.appRating = appRating;
		this.processName=processName;
		startAt = new Date().getTime();
		stillrunning=true;
	}


	public Application(Parcel in) 
	{
		this.playurl=in.readString();
		this.picurl=in.readString();
		this.appName=in.readString();
		this.appCategory=in.readString();
		this.appDescription = in.readString();
		this.appRating = in.readInt();
		this.processName=in.readString();
		this.importance=in.readInt();
		this.startAt = in.readLong();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) 
	{
		parcel.writeString(playurl);
		parcel.writeString(picurl);
		parcel.writeString(appName);
		parcel.writeString(appCategory);
		parcel.writeString(appDescription);
		parcel.writeInt(appRating);
		parcel.writeString(processName);
		parcel.writeInt(importance);
		parcel.writeLong(startAt);
	}
	
	@Override
	public int describeContents() 
	{
		return 0;
	}
	
	public void setImportance(int importance) 
	{
		this.importance=importance;	
	}
	
	public String getAppName()
	{
		return appName;
	}
	
	public String getAppCategory()
	{
		return appCategory;
	}
	
	public String getAppDescription()
	{
		return appDescription;
	}


	public int getImportance() 
	{
		return importance;
	}
	
	public String getProcessName()
	{
		return processName;
	}
	
	public int getRunntime()
	{
		Date d = new Date();
		Date start = new Date(startAt);
		int x = (int) ((d.getTime()-start.getTime())/1000);
		return x;
	}
	
	public boolean getStillRunningFlag()
	{
		return stillrunning;
	}
	
	public void setStillRunningFlag(boolean s)
	{
		stillrunning=s;
	}
	
	
}
