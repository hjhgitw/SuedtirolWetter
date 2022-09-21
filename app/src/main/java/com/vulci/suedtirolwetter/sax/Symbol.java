package com.vulci.suedtirolwetter.sax;

import android.os.Parcel;
import android.os.Parcelable;

public class Symbol implements Parcelable {

	private String code;
	private String description;
	private String imageUrl;

	public Symbol(Parcel in) {
		code = in.readString();
		description = in.readString();
		imageUrl = in.readString();
	}

	public Symbol() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Symbol createFromParcel(Parcel in) {
			return new Symbol(in);
		}

		public Symbol[] newArray(int size) {
			return new Symbol[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(description);
		dest.writeString(imageUrl);
	}

}
