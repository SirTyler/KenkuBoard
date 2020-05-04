package me.sirtyler.kenkuboard;

import java.io.File;
import java.io.IOException;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

public class KenkuPlayer {
	public Synthesizer synth;
	private VariableRateDataReader samplePlayer;
	private LineOut lineOut;
	private int out_device_id;
	
	public boolean isPlaying = false;
	
	public KenkuPlayer() {
		synth = JSyn.createSynthesizer();
        synth.add(lineOut = new LineOut());
	}
	
	public void setOutputDevice(int device_id) {
		this.out_device_id = device_id;
	}
	
	public synchronized void play(String path) {
        File sampleFile = new File(path);

        FloatSample sample;
        try {
            // Load the sample and display its properties.
            SampleLoader.setJavaSoundPreferred(false);
            sample = SampleLoader.loadFloatSample(sampleFile);
            System.out.println("Sample has: channels  = " + sample.getChannelsPerFrame());
            System.out.println("            frames    = " + sample.getNumFrames());
            System.out.println("            rate      = " + sample.getFrameRate());
            System.out.println("            loopStart = " + sample.getSustainBegin());
            System.out.println("            loopEnd   = " + sample.getSustainEnd());

            if (sample.getChannelsPerFrame() == 1) {
                synth.add(samplePlayer = new VariableRateMonoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
            } else if (sample.getChannelsPerFrame() == 2) {
                synth.add(samplePlayer = new VariableRateStereoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
                samplePlayer.output.connect(1, lineOut.input, 1);
            } else {
                throw new RuntimeException("Can only play mono or stereo samples.");
            }

            // Start synthesizer using default stereo output at 44100 Hz.
            isPlaying = true;
            synth.start(44100, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, out_device_id, 2);

            samplePlayer.rate.set(44100.0);

            // We only need to start the LineOut. It will pull data from the
            // sample player.
            lineOut.start();

            // We can simply queue the entire file.
            // Or if it has a loop we can play the loop for a while.
            if (sample.getSustainBegin() < 0) {
                System.out.println("queue the sample");
                samplePlayer.dataQueue.queue(sample);
            } else {
                System.out.println("queueOn the sample");
                samplePlayer.dataQueue.queueOn(sample);
                synth.sleepFor(8.0);
                System.out.println("queueOff the sample");
                samplePlayer.dataQueue.queueOff(sample);
            }

            // Wait until the sample has finished playing.
            do {
                synth.sleepFor(1.0);
            } while (samplePlayer.dataQueue.hasMore());

            synth.sleepFor(0.5);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Stop everything.
        lineOut.stop();
        isPlaying = false;
    }
}
