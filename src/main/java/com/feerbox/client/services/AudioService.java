package com.feerbox.client.services;

import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class AudioService {
    protected final static Logger logger = Logger.getLogger(AudioService.class);

    public static void playAnswerSound(int buttonNumber) {
        try {
            logger.debug("Going to play sound - Step 1");
            String soundPath = "audios/answer_" + buttonNumber + ".wav";

            logger.debug("Going to play sound - Step 2");
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(soundPath);

            logger.debug("Going to play sound - Step 3");
            AudioInputStream stream = AudioSystem.getAudioInputStream(inputStream);

            logger.debug("Going to play sound - Step 4");
            final Clip clip = AudioSystem.getClip();

            // Listener which allow method return once sound is completed
            /*clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent e) {
                    if (e.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });*/

            clip.open(stream);
            clip.start();

            logger.debug("Playing answer " + buttonNumber + " sound (" + soundPath + ")");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException  e) {
            logger.debug("Error playing answer sound: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.debug("Other error: " + e.getMessage());
        }
    }
}
