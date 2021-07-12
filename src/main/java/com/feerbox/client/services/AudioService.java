package com.feerbox.client.services;

import com.feerbox.client.services.voice.AskQuestion;
import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class AudioService implements Runnable {
    protected final static Logger logger = Logger.getLogger(AudioService.class);
    private int buttonNumber;

    public AudioService(int buttonNumber) {
        this.buttonNumber = buttonNumber;
    }

    public static void playAnswerSound(int buttonNumber) {
        Thread t = new Thread(new AudioService(buttonNumber));
        t.start();
    }

    @Override
    public void run() {
        AudioInputStream audioIn;
        try {
            URL url = this.getClass().getClassLoader().getResource("answer_" + buttonNumber + ".wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength()/1000);
        } catch (Exception  e) {
            logger.error("Error playing answer sound: " + e.getMessage(), e);
        }
    }
}


/*{
    protected final static Logger logger = Logger.getLogger(AudioService.class);

    public static void playAnswerSound(int buttonNumber) {
        try {
            logger.debug("Going to play sound - Step 1");
            String soundPath = "audios/answer_" + buttonNumber + ".wav";

            logger.debug("Going to play sound - Step 2");
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(soundPath);

            logger.debug("inputStream is null: " + (inputStream == null));
            logger.debug("Going to play sound - Step 3");
            AudioInputStream stream = AudioSystem.getAudioInputStream(inputStream);

            logger.debug("Going to play sound - Step 4");
            final Clip clip = AudioSystem.getClip();

            // Listener which allow method return once sound is completed
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent e) {
                    if (e.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });

            clip.open(stream);
            clip.start();

            logger.debug("Playing answer " + buttonNumber + " sound (" + soundPath + ")");
        } catch (Exception  e) {
            logger.error("Error playing answer sound: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
}*/
