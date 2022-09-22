package com.vulci.suedtirolwetter.sax;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;

public class ProvBulletin implements Parcelable {

	private String date;
	private HashMap<Integer, StationData> stationDataMap;
	private static HashMap<Integer, Integer> stationMap;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public HashMap<Integer, StationData> getStationDataMap() {
		return stationDataMap;
	}

	public void setStationDataMap(HashMap<Integer, StationData> stationDataMap) {
		this.stationDataMap = stationDataMap;
	}

	public ProvBulletin(Parcel in) {
		date = in.readString();
		in.readMap(stationDataMap, ProvBulletin.class.getClassLoader());
		in.readMap(stationMap, ProvBulletin.class.getClassLoader());
	}

	public ProvBulletin() {
		setupStationMap();
	}

	public Integer getIdForStation(int station) {

		Log.d("XXX", "station: " + station);

		if (stationMap == null) {
			setupStationMap();
		}

		return stationMap.get(station);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public ProvBulletin createFromParcel(Parcel in) {
			return new ProvBulletin(in);
		}

		public ProvBulletin[] newArray(int size) {
			return new ProvBulletin[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(date);
		dest.writeMap(stationDataMap);
		dest.writeMap(stationMap);
	}

	private void setupStationMap() {
		stationMap = new HashMap<Integer, Integer>();
		stationMap.put(1, 642);
		stationMap.put(2, 101);
		stationMap.put(3, 100);
		stationMap.put(4, 109);
		stationMap.put(5, 963);
		stationMap.put(6, 110);
	}
}
