package com.feerbox.client.services;

import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AudioService implements Runnable {
    protected final static Logger logger = Logger.getLogger(AudioService.class);
    private static Clip clip;
    private String name;
    public URL url;

    public AudioService(String name) {
        this.name = name;
    }

    public AudioService(URL url) {
        this.url = url;
    }

    public static void playSound(String name) {
        Thread t = new Thread(new AudioService(name));
        t.start();
    }

    public static void playSound(URL url) {
        Thread t = new Thread(new AudioService(url));
        t.start();
    }

    public static void playAnswerSound(int buttonNumber) {
        Thread t = new Thread(new AudioService("answer_" + buttonNumber));
        t.start();
    }

    @Override
    public void run() {
        try {
            if (url == null && name != null) {
                File file = new File("/opt/FeerBoxClient/audios/" + name + ".wav");

                if (file.exists()) { // Custom audio
                    url = file.toURI().toURL();
                } else { // Default audio
                    url = this.getClass().getClassLoader().getResource("audios/" + name + ".wav");
                }
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            if (clip == null) {
                clip = AudioSystem.getClip();
            }
            if (clip.isRunning()) {
                clip.stop();
            }
            if(clip.isOpen()) {
                clip.close();
            }

            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (LineEvent.Type.STOP.equals(event.getType())) {
                        clip.close();
                    }
                }
            });

            clip.open(audioIn);
            clip.start();
            logger.debug("Playing sound " + url.getFile());
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (Exception  e) {
            logger.error("Error playing answer sound: " + e.getMessage(), e);
        }
    }
}