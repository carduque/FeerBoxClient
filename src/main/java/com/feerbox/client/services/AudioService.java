package com.feerbox.client.services;

import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AudioService implements Runnable {
    protected final static Logger logger = Logger.getLogger(AudioService.class);
    private final static Lock lock = new ReentrantLock();
    private String name;

    public AudioService(String name) {
        this.name = name;
    }

    public static void playSound(String name) {
        Thread t = new Thread(new AudioService(name));
        t.start();
    }

    public static void playAnswerSound(int buttonNumber) {
        Thread t = new Thread(new AudioService("answer_" + buttonNumber));
        t.start();
    }

    @Override
    public void run() {
        lock.lock();
        try {
            URL url = this.getClass().getClassLoader().getResource("audios/" + name + ".wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            logger.error("Playing answer file " + url.getFile());
            Thread.sleep(clip.getMicrosecondLength());
        } catch (Exception  e) {
            logger.error("Error playing answer sound: " + e.getMessage(), e);
        }
        lock.unlock();
    }
}