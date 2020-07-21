package com.feerbox.client.services.voice;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

public class AskRecordAndTranscribe implements Runnable {
	private final static Logger logger = Logger.getLogger(AskRecordAndTranscribe.class);
	// record duration, in milliseconds
    static final long RECORD_TIME = 5000;  // 5 secs
 
    // path of the wav file
    File wavFile = new File("RecordAudio.wav");
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
    
    private static Lock lock = new ReentrantLock();
	
    public static void main(String[] args){
    	if (AskRecordAndTranscribe.lock.tryLock())
        {
    	try {
	        Thread t = new Thread(new AskRecordAndTranscribe());
	        t.start();
        } catch(Exception e) {
        	logger.error(e.getMessage(), e);
        } finally
        {
        	AskRecordAndTranscribe.lock.unlock();
        }
    }   
   }

    @Override
    public void run() {
    	logger.debug("Start running AskRecordandTranscribe");
    	Instant start = Instant.now();
        AudioInputStream audioIn;
        try {
        	URL url = this.getClass().getClassLoader().getResource("bad_feedback_es-ES.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            logger.debug("Reproducing sound");
            clip.start();
            Thread.sleep(clip.getMicrosecondLength()/1000);
            
            RecordAnswer();
            
            Transcribe();
            
            url = this.getClass().getClassLoader().getResource("thankyou_es-ES.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength()/1000);
            Instant end = Instant.now();
            logger.debug("Ending in: "+Duration.between(start, end).getSeconds());
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException  e1) {
        	logger.error(e1.getMessage(), e1);
        }
    }

	private void Transcribe() throws IOException {
		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(this.getClass().getClassLoader().getResourceAsStream("feerboxclient-1595060063869-c497e8407142.json")));
		 SpeechSettings settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
		try (SpeechClient speechClient = SpeechClient.create(settings)) {

		      // The path to the audio file to transcribe
		      //String fileName = "how_was_our_attention_es-ES.m4a";
		    	String fileName ="RecordAudio.wav";

		      // Reads the audio file into memory
		      Path path = Paths.get(fileName);
		      byte[] data = Files.readAllBytes(path);
		      ByteString audioBytes = ByteString.copyFrom(data);
		      logger.debug("Start google transcribe");
		      // Builds the sync recognize request
		      RecognitionConfig config =
		          RecognitionConfig.newBuilder()
		              .setEncoding(AudioEncoding.LINEAR16)
		              .setSampleRateHertz(16000)
		              .setLanguageCode("es-ES")
		              .setAudioChannelCount(2)
		              .build();
		      RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

		      // Performs speech recognition on the audio file
		      RecognizeResponse response = speechClient.recognize(config, audio);
		      List<SpeechRecognitionResult> results = response.getResultsList();
		      
		      for (SpeechRecognitionResult result : results) {
		        // There can be several alternative transcripts for a given chunk of speech. Just use the
		        // first (most likely) one here.
		        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
		        logger.info("Transcription: "+alternative.getTranscript());
		        //System.out.printf("Transcription: %s%n", alternative.getTranscript());
		      }
		      logger.debug("Finish google transcribe");
		    }
	}

	private void RecordAnswer() throws LineUnavailableException, IOException, InterruptedException {
		 AudioFormat format = getAudioFormat();
         DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

         // checks if system supports the data line
         if (!AudioSystem.isLineSupported(info)) {
             logger.error("Line not supported");
             return;
         }
         line = (TargetDataLine) AudioSystem.getLine(info);
         line.open(format);
         line.start();   // start capturing

         //logger.debug("Start capturing...");

         AudioInputStream ais = new AudioInputStream(line);

         logger.debug("Start recording...");

         // start recording
         AudioSystem.write(ais, fileType, wavFile);
         Thread.sleep(RECORD_TIME);
         line.stop();
         line.close();
         logger.debug("Finish recording");
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