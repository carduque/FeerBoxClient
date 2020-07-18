package com.feerbox.client.services.voice;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AskQuestion implements Runnable {
    public static void main(String[] args){
        Thread t = new Thread(new AskQuestion());
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException  e1) {
            e1.printStackTrace();
        }
    }
}