package com.vulci.suedtirolwetter.sax;
import android.os.Parcel;
import android.os.Parcelable;

public class StationData implements Parcelable {

	private int id;
	private Symbol symbolData;
	private Temperature temperatureData;


	public StationData() {
	}

	public StationData(Parcel in) {
		id = in.readInt();
		symbolData = in.readParcelable(Symbol.class.getClassLoader());
		temperatureData = in.readParcelable(Temperature.class.getClassLoader());
	}

	public int getId() {
		return id;
	}

	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}

	public Symbol getSymbolData() {
		return symbolData;
	}

	public void setSymbolData(Symbol symbolData) {
		this.symbolData = symbolData;
	}

	public Temperature getTemperatureData() {
		return temperatureData;
	}

	public void setTemperatureData(Temperature temperatureData) {
		this.temperatureData = temperatureData;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public StationData createFromParcel(Parcel in) {
			return new StationData(in);
		}

		public StationData[] newArray(int size) {
			return new StationData[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeParcelable(symbolData, flags);
		dest.writeParcelable(temperatureData, flags);
	}
}
