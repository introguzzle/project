package ru.chess.model;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class SoundPlayer {

    private SoundPlayer() {

    }

    public static File moveSound  = new File(".\\src\\main\\java\\ru\\chess\\sounds\\move.wav");
    public static File startSound = new File(".\\src\\main\\java\\ru\\chess\\sounds\\start.wav");
    public static File endSound   = new File(".\\src\\main\\java\\ru\\chess\\sounds\\end.wav");

    public static void playStartSound() {
        playSound(startSound);
    }

    public static void playMoveSound() {
        playSound(moveSound);
    }

    public static void playEndSound() {

    }

    public static void playSound(File file) {
        playSound(file, -10.0f);
    }

    public static void playSound(File file, float volume) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            gainControl.setValue(gainControl.getValue() + volume);

            clip.start();

        } catch (Exception ignored) {

        }
    }
}
