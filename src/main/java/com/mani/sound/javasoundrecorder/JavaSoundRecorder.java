package com.mani.sound.javasoundrecorder;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Scanner;

/**
 * @author: Manindar
 */
public class JavaSoundRecorder {

    /**
     * Record time limit
     */
    static long recordTime;
    /**
     * Output file path
     */
    static String filePath;

    /**
     * Format of output file.
     */
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    /**
     * the line from which audio data is captured
     */
    TargetDataLine line;

    /**
     * Defines an audio format
     */
    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
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
            AudioSystem.write(ais, fileType, new File(filePath));
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number seconds you wanted to record..!");
        recordTime = sc.nextLong() * 1000;
        System.out.println("Enter preffered output file path along with file name..!");
        filePath = sc.next();
        if (!new File(filePath).exists()) {
            throw new Exception("Invalid file path provided...!");
        }
        final JavaSoundRecorder recorder = new JavaSoundRecorder();

        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(recordTime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });
        stopper.start();
        // start recording
        recorder.start();
    }
}
