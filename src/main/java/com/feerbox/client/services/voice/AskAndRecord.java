package com.feerbox.client.services.voice;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AskAndRecord implements Runnable {
	// record duration, in milliseconds
    static final long RECORD_TIME = 5000;  // 5 secs
 
    // path of the wav file
    File wavFile = new File("RecordAudio.wav");
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
	
    public static void main(String[] args){
        Thread t = new Thread(new AskAndRecord());
        t.start();
    }   

    @Override
    public void run() {
        AudioInputStream audioIn;
        try {
        	URL url = this.getClass().getClassLoader().getResource("bad_feedback_es-ES.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength()/1000);
            
            RecordAnswer();
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException  e1) {
            e1.printStackTrace();
        }
    }

	private void RecordAnswer() throws LineUnavailableException, IOException {
		 AudioFormat format = getAudioFormat();
         DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

         // checks if system supports the data line
         if (!AudioSystem.isLineSupported(info)) {
             System.out.println("Line not supported");
             System.exit(0);
         }
         line = (TargetDataLine) AudioSystem.getLine(info);
         line.open(format);
         line.start();   // start capturing

         System.out.println("Start capturing...");

         AudioInputStream ais = new AudioInputStream(line);

         System.out.println("Start recording...");

         // start recording
         AudioSystem.write(ais, fileType, wavFile);
	}
	
	AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
}