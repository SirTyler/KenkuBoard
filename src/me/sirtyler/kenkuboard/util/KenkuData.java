package me.sirtyler.kenkuboard.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KenkuData {
	private static int device;
	private static HashMap<String, ArrayList<SoundClip>> map = new HashMap<String, ArrayList<SoundClip>>();
	private static KenkuData instance;
	private static String file_path;
	
	public static int setAudioDevice(int audio_device) {
		device = audio_device;
		return device;
	}
	
	public static int getAudioDevice() {
		return device;
	}
	
	public static boolean addTab(String tab_name) {
		if(!map.containsKey(tab_name)) {
			map.put(tab_name, new ArrayList<SoundClip>());
			return true;
		} return false;
	}
	
	public static void removeTab(String folder_name) {
		map.remove(folder_name);
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, ArrayList<SoundClip>> getTabs() {
		return (HashMap<String, ArrayList<SoundClip>>) map.clone();
	}
	
	public static void addSoundClip(String folder_name, SoundClip clip) {
		addTab(folder_name);
		ArrayList<SoundClip> list = map.get(folder_name);
		list.add(clip);
		map.put(folder_name, list);
	}
	
	public static void removeSoundClip(String folder_name, SoundClip clip) {
		if(!map.keySet().contains(folder_name)) return;
		ArrayList<SoundClip> list = map.get(folder_name);
		list.remove(clip);
		map.put(folder_name, list);
	}
	
	public static SoundClip getSoundClip(String folder_name, String clip_name) {
		if(!map.keySet().contains(folder_name)) return null;
		ArrayList<SoundClip> list = map.get(folder_name);
		for(SoundClip clip : list) {
			if(clip_name.equalsIgnoreCase(clip.name)) return clip;
		}
		return null;
	}
	
	public static synchronized KenkuData getInstance( ) {
	      if (instance == null) instance=new KenkuData();
	      return instance;
	}
	
	private static void fileCheck() throws IOException {
		if(file_path == null) {
			File f = new File("kenku.data");
			f.createNewFile();
			file_path = f.getAbsolutePath();
		}
	}
	
	private static String saveData() {
		GsonBuilder gsonBuilder  = new GsonBuilder();
		// Allowing the serialization of static fields    

		gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
		// Creates a Gson instance based on the current configuration
		Gson gson = gsonBuilder.create();
		System.out.println(gson.toJson(KenkuData.getInstance()));
		return gson.toJson(KenkuData.getInstance());
	}
	
	private static void loadData(String json) {
		GsonBuilder gsonBuilder  = new GsonBuilder();
		// Allowing the serialization of static fields    

		gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
		// Creates a Gson instance based on the current configuration
		Gson gson = gsonBuilder.create();
		KenkuData data = gson.fromJson(json, KenkuData.class);
		instance = data;
	}
	
	public static void save() throws IOException, URISyntaxException {
		fileCheck();
		FileWriter writer = new FileWriter(file_path);
		writer.write(KenkuData.saveData());
		writer.flush();
		writer.close();
	}
	
	public static void load() throws IOException, URISyntaxException {
		fileCheck();
		List<String> lines = Files.readAllLines(Paths.get(file_path), StandardCharsets.UTF_8);
		KenkuData.loadData(String.join(System.lineSeparator(), lines));
	}

}
