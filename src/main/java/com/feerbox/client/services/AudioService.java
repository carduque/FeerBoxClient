package com.feerbox.client.services;

import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class AudioService {
    protected final static Logger logger = Logger.getLogger(AudioService.class);

    public static void playAnswerSound(int buttonNumber) {
        try {
            final CountDownLatch syncLatch = new CountDownLatch(1);

            logger.debug("Going to play sound 1");
            String soundPath = "audios/answer_" + buttonNumber + ".wav";

            logger.debug("Going to play sound 2");
            AudioInputStream stream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream(soundPath));

            logger.debug("Going to play sound 3");
            final Clip clip = AudioSystem.getClip();

            // Listener which allow method return once sound is completed
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent e) {
                    if (e.getType() == LineEvent.Type.STOP) {
                        clip.close();
                        syncLatch.countDown();
                    }
                }
            });

            clip.open(stream);
            clip.start();

            logger.debug("Playing answer " + buttonNumber + " sound (" + soundPath + ")");
            syncLatch.await();
        } catch (InterruptedException | UnsupportedAudioFileException | IOException | LineUnavailableException  e) {
            logger.debug("Error playing answer sound: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
