package com.vulci.suedtirolwetter.sax;


import android.os.Parcel;
import android.os.Parcelable;

public class Temperature implements Parcelable {

	private String min;
	private String max;

	public Temperature(Parcel in) {
		min = in.readString();
		max = in.readString();
	}

	public Temperature() {
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Temperature createFromParcel(Parcel in) {
			return new Temperature(in);
		}

		public Temperature[] newArray(int size) {
			return new Temperature[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(min);
		dest.writeString(max);
	}
}
