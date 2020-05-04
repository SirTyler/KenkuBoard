package me.sirtyler.kenkuboard.util;

public class SoundClip {
	public String path;
	public String name;
	
	public SoundClip(String file_path, String file_name) {
		this.path = file_path;
		this.name = file_name;
	}
	
	@Override
	public String toString() {
		return name + ":" + path;
	}
}
