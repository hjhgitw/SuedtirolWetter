package com.vulci.suedtirolwetter;


import com.vulci.suedtirolwetter.sax.ProvBulletin;
import com.vulci.suedtirolwetter.sax.StationData;
import com.vulci.suedtirolwetter.sax.Symbol;
import com.vulci.suedtirolwetter.sax.Temperature;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;


public class WetterdatenXmlHandler extends DefaultHandler {
	private ProvBulletin provBulletin;
	private HashMap<Integer, StationData> stationDataMap;
	private StationData stationData;
	private Symbol symbolData;
	private Temperature temperatureData;
	private StringBuilder stringBuilder;
	private boolean inStationData = false;
	private boolean inDayForecast = false;
	private boolean inMountainToday = false;
	private boolean inMountainTomorrow = false;
	private boolean inToday = false;
	private boolean inTomorrow = false;

	public WetterdatenXmlHandler(ProvBulletin provBulletin) {
		this.provBulletin = provBulletin;
		this.stationDataMap = new HashMap<Integer, StationData>();
		stringBuilder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
							 Attributes attributes) throws SAXException {

		clearString();
		if (inToday && localName.equalsIgnoreCase("stationData")) {
			inStationData = true;
			stationData = new StationData();
		} else if (inStationData && localName.equalsIgnoreCase("symbol")) {
			symbolData = new Symbol();
		} else if (inStationData && localName.equalsIgnoreCase("temperature")) {
			temperatureData = new Temperature();
		} else if (localName.equalsIgnoreCase("dayForecast")) {
			inDayForecast = true;
		} else if (localName.equalsIgnoreCase("mountainToday")) {
			inMountainToday = true;
		}else if (localName.equalsIgnoreCase("mountainTomorrow")) {
			inMountainTomorrow = true;
		} else if (localName.equalsIgnoreCase("today")) {
			inToday = true;
		}else if (localName.equalsIgnoreCase("tomorrow")) {
			inTomorrow = true;
		}

		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		stringBuilder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		//Log.d("XXX", ""+localName+": "+stringBuilder.toString());
		//Log.d("XXX", "----------------------------------------");

		if (inToday && localName.equalsIgnoreCase("stationData")) {
			inStationData = false;
			stationData.setSymbolData(symbolData);
			stationData.setTemperatureData(temperatureData);
			stationDataMap.put(provBulletin.getIdForStation(stationData.getId()), stationData);
		} else if (localName.equalsIgnoreCase("dayForecast")) {
			inDayForecast = false;
		} else if (localName.equalsIgnoreCase("mountainToday")) {
			inMountainToday = false;
		} else if (localName.equalsIgnoreCase("mountainTomorrow")) {
			inMountainTomorrow = false;
		} else if (localName.equalsIgnoreCase("today")) {
			inToday = false;
		} else if (localName.equalsIgnoreCase("tomorrow")) {
			inTomorrow = false;
		} else if (localName.equalsIgnoreCase("date") && !inDayForecast
				&& !inToday && !inTomorrow && !inMountainToday && !inMountainTomorrow) {
			provBulletin.setDate(stringBuilder.toString());
		} else if (localName.equalsIgnoreCase("provBulletin")) {
			provBulletin.setStationDataMap(stationDataMap);
		}

		if (inToday && inStationData) {

			//Log.d("XXX", ""+localName+": "+stringBuilder.toString());
			//Log.d("XXX", "*****************************************");

			if (localName.equalsIgnoreCase("Id")) {
				stationData.setId(stringBuilder.toString());
			} else if (localName.equalsIgnoreCase("code")) {
				symbolData.setCode(stringBuilder.toString());
			} else if (localName.equalsIgnoreCase("description")) {
				symbolData.setDescription(stringBuilder.toString());
			} else if (localName.equalsIgnoreCase("imageURL")) {
				symbolData.setImageUrl(stringBuilder.toString());
			} else if (localName.equalsIgnoreCase("max")) {
				temperatureData.setMax(stringBuilder.toString());
			} else if (localName.equalsIgnoreCase("min")) {
				temperatureData.setMin(stringBuilder.toString());
			}
		}

		clearString();

		super.endElement(uri, localName, qName);
	}

	private void clearString(){
		stringBuilder.setLength(0);
	}
}
