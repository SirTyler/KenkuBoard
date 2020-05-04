package me.sirtyler.kenkuboard;

import java.io.IOException;
import java.net.URISyntaxException;

import com.jsyn.devices.AudioDeviceManager;

import me.sirtyler.kenkuboard.util.KenkuData;

public class KenkuBoard {

	public static KenkuPlayer player;
	private static AudioDeviceManager manager;
	private static int in_devices[];
	private static int out_devices[];
	
	public static void main(String[] args) {
		try {
			KenkuData.load();
			player = new KenkuPlayer();
			manager = player.synth.getAudioDeviceManager();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		
		int x = manager.getDeviceCount();
		in_devices = new int[x];
		out_devices = new int[x];

		int in_count = 0;
		int out_count = 0;

		for (int i = 0; i < x; i++) {
			int ins = manager.getMaxInputChannels(i);
			int outs = manager.getMaxOutputChannels(i);

			if (ins > 0)
				in_devices[in_count++] = i;
			if (outs > 0)
				out_devices[out_count++] = i;
		}

		in_devices = trim_array(in_devices);
		out_devices = trim_array(out_devices);

		KenkuBoardGUI gui = new KenkuBoardGUI();
		gui.setDevices(manager, out_devices);
		gui.show();
	}

	private static int[] trim_array(int[] array) {
		int x = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0)
				x++;
		}

		int[] trimmed = new int[x];
		x = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0)
				trimmed[x++] = array[i];
		}

		return trimmed;
	}

	public static void makeStream(int device_id) {
		player.setOutputDevice(device_id);
		KenkuData.setAudioDevice(device_id);
	}
}
