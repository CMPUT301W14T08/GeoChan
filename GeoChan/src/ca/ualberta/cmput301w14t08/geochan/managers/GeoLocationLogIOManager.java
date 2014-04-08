/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Responsible for persistency of the log of used geolocations. Contains methdos
 * to serialize/deserialize log. Is a singleton.
 * 
 * @author Artem Chikin
 */
public class GeoLocationLogIOManager {
	private static GeoLocationLogIOManager instance;
	private Context context;
	private Gson gson;
	private static final String FILENAME = "geolog.sav";

	private GeoLocationLogIOManager(Context context) {
		this.context = context;
		this.gson = GsonHelper.getOnlineGson();
	}

	/**
	 * Returns the instance of the GeoLocationLogIOManager.
	 * @param context The Context in which the GeoLocationLogIOManager is running.
	 * @return The GeolocationLogIOManager instance.
	 */
	public static GeoLocationLogIOManager getInstance(Context context) {
		if (instance == null) {
			instance = new GeoLocationLogIOManager(context);
		}
		return instance;
	}

	/**
	 * Serializes an ArrayList of Geolocations and saves them.
	 * @param list The ArrayList of GeoLocations to serialize.
	 */
	public void serializeLog(ArrayList<GeoLocation> list) {
		try {
			String json = gson.toJson(list);
			FileOutputStream f = context.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f));
			w.write(json);
			w.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deserializes and returns the GeoLocations stored in our app.
	 * @return An ArrayList of the stored GeoLocations.
	 */
	public ArrayList<GeoLocation> deserializeLog() {
		ArrayList<GeoLocation> list = new ArrayList<GeoLocation>();
		try {
			FileInputStream f = context.openFileInput(FILENAME);
			BufferedReader r = new BufferedReader(new InputStreamReader(f));
			String json = "";
			String temp = "";
			temp = r.readLine();
			while (temp != null) {
				json = json + temp;
				temp = r.readLine();
			}
			r.close();
			f.close();
			Type type = new TypeToken<ArrayList<GeoLocation>>() {
			}.getType();
			list = gson.fromJson(json, type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
