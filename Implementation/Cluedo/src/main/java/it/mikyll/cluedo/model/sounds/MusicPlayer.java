package it.mikyll.cluedo.model.sounds;

import it.mikyll.cluedo.model.settings.Settings;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {
    private static MusicPlayer instance = null;
    private static MediaPlayer mediaPlayer;

    private static Map<MusicTrack, Media> musicTracks;
    private static double volume = 50.0;
    private static boolean isPlaying = false;
    private static MusicTrack loadedTrack = null;

    private MusicPlayer() {}

    public static synchronized MusicPlayer getInstance()
    {
        if(instance == null)
        {
            instance = new MusicPlayer();
            if (musicTracks == null)
            {
                loadMusic();
            }
            mediaPlayer = new MediaPlayer(musicTracks.get(MusicTrack.MENU));
            mediaPlayer.setVolume(volume / 100);
            mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        }
        return instance;
    }

    private static void loadMusic() {
        musicTracks = new HashMap<>();

        musicTracks.put(MusicTrack.MENU, new Media(MusicPlayer.class.getResource(Settings.RESOURCES_PATH + "music/David Fesliyan - Unfolding Revelation.mp3").toString()));
        musicTracks.put(MusicTrack.GAME, new Media(MusicPlayer.class.getResource(Settings.RESOURCES_PATH + "music/George Arkomanis - Relaxation In Mystery.mp3").toString()));
    }

    public void setVolume(double value)
    {
        volume = value;
        mediaPlayer.setVolume(volume / 100);
    }


    public void setTrack(MusicTrack musicTrack)
    {
        this.stop();
        mediaPlayer = new MediaPlayer(musicTracks.get(musicTrack));
        loadedTrack = musicTrack;
        mediaPlayer.setVolume(volume / 100);
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
    }

    public void play()
    {
        this.stop();
        mediaPlayer.play();
        isPlaying = true;
    }

    public void play(MusicTrack musicTrack)
    {
        if (!isPlaying || musicTrack != loadedTrack)
        {
            this.setTrack(musicTrack);
            mediaPlayer.play();
            isPlaying = true;
        }
    }

    public void stop()
    {
        mediaPlayer.stop();
        isPlaying = false;
    }
}
