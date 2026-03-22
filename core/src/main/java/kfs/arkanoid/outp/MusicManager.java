package kfs.arkanoid.outp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class MusicManager {

    private final List<Music> tracks = new ArrayList<>();
    private int currentTrack = 0;
    private Sound bounceSound;
    private Sound brickBreakSound;
    private Sound eatSurpriseSound;

    public MusicManager(String folderPath) {
        // Load all .ogg files in the folder
        FileHandle folder = Gdx.files.internal(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (FileHandle file : folder.list()) {
                if (file.extension().equalsIgnoreCase("mp3")) {
                    Music music = Gdx.audio.newMusic(file);
                    music.setLooping(false);
                    music.setVolume(0.5f);
                    tracks.add(music);
                }
            }
        }
        bounceSound = Gdx.audio.newSound(Gdx.files.internal("sounds/ball.wav"));
        brickBreakSound = Gdx.audio.newSound(Gdx.files.internal("sounds/brick.wav"));
        eatSurpriseSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gem.wav"));

        // Setup next track listener
        for (Music music : tracks) {
            music.setOnCompletionListener(this::playNextTrack);
        }
    }

    public void play() {
        if (!tracks.isEmpty()) {
            currentTrack = 0;
            tracks.get(currentTrack).play();
        }
    }

    public boolean isPlaying() {
        for (Music music : tracks) {
            if (music.isPlaying()) return true;
        }
        return false;
    }

    public void playBounceSound() {
        if (bounceSound != null) {
            bounceSound.play();
        }
    }

    public void playBrickBreakSound() {
        if (brickBreakSound != null) {
            brickBreakSound.play();
        }
    }

    public void playSurpriseSound() {
        if (eatSurpriseSound != null) {
            eatSurpriseSound.play();
        }
    }

    private void playNextTrack(Music completed) {
        completed.stop();
        currentTrack = (currentTrack + 1) % tracks.size();
        tracks.get(currentTrack).play();
    }

    public void stop() {
        for (Music music : tracks) {
            music.stop();
        }
    }

    public void dispose() {
        for (Music music : tracks) {
            music.dispose();
        }
        tracks.clear();
        if (bounceSound != null) {
            bounceSound.dispose();
        }
        if (brickBreakSound != null) {
            brickBreakSound.dispose();
        }
        if (eatSurpriseSound != null) {
            eatSurpriseSound.dispose();
        }
    }

}

