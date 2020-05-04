package me.sirtyler.kenkuboard.util;

public class AudioDevice {
	private String device_name = "DEVICE";
	private int device_id = -1;
	
	public AudioDevice(String name, int id) {
		device_name = name;
		device_id = id;
	}
	
	public String getName() {
		return device_name;
	}
	
	public int getID() {
		return device_id;
	}
	
	@Override
	public String toString() {
		return device_name + ":" + device_id;
	}
}
