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
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Responsible for persistency of favourite threads/comments. Contains methods
 * to Serialize/Deserialize both comments and threads. Is a singleton.
 * 
 * @author Artem Chikin
 */
public class FavouritesIOManager {
	private static FavouritesIOManager instance;
	private Context context;
	private Gson gson;
	private static final String FILENAME1 = "favcom.sav";
	private static final String FILENAME2 = "favthr.sav";

	private FavouritesIOManager(Context context) {
		this.context = context;
		this.gson = GsonHelper.getOfflineGson();
	}

	public static FavouritesIOManager getInstance(Context context) {
		if (instance == null) {
			instance = new FavouritesIOManager(context);
		}
		return instance;
	}

	/**
	 * Serializes favourited ThreadComments and stores them in the cache.
	 */
	public void serializeThreads() {
		try {
			String json = gson.toJson(FavouritesLog.getInstance(context)
					.getThreads());
			FileOutputStream f = context.openFileOutput(FILENAME2,
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
	 * Serialize arrayList of threads to JSON. These threadComments are
	 * favourited comments converted to ThreadComment to save their replies as
	 * well.
	 */
	public void serializeFavComments() {
		try {
			String json = gson.toJson(FavouritesLog.getInstance(context)
					.getFavComments());
			FileOutputStream f = context.openFileOutput(FILENAME1,
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
	 * Deserializes the favourited Comments.
	 * @return An ArrayList of the favourited comments as ThreadComments.
	 */
	public ArrayList<ThreadComment> deSerializeFavComments() {
		ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
		try {
			FileInputStream f = context.openFileInput(FILENAME1);
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
			Type type = new TypeToken<ArrayList<ThreadComment>>() {
			}.getType();
			list = gson.fromJson(json, type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Deserialize ArrayList of favourited ThreadComments from JSON
	 * 
	 * @return ArrayList of favourited ThreadComments.
	 */
	public ArrayList<ThreadComment> deSerializeThreads() {
		ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
		try {
			FileInputStream f = context.openFileInput(FILENAME2);
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
			Type type = new TypeToken<ArrayList<ThreadComment>>() {
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
