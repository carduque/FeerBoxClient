package com.feerbox.client.services;

import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class AudioService {
    protected final static Logger logger = Logger.getLogger(AnswerService.class);

    public static void playAnswerSound(int buttonNumber) {
        try {
            final CountDownLatch syncLatch = new CountDownLatch(1);
            final String soundPath = "audios/answer_" + buttonNumber + ".wav";

            try (AudioInputStream stream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream(soundPath))) {
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
            }
            syncLatch.await();
        }  catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            logger.error("Error playing answer sound: " + e.getMessage());
        }
    }
}
