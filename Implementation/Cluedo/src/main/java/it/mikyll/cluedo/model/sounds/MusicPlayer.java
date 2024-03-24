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
    private static double volume = 0.5;

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
            mediaPlayer.setVolume(volume);
            mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        }
        return instance;
    }

    private static void loadMusic() {
        musicTracks = new HashMap<>();

        musicTracks.put(MusicTrack.MENU, new Media(MusicPlayer.class.getResource(Settings.RESOURCES_PATH + "music/David Fesliyan - Unfolding Revelation.mp3").toString()));
        musicTracks.put(MusicTrack.GAME, new Media(MusicPlayer.class.getResource(Settings.RESOURCES_PATH + "music/George Arkomanis - Relaxation In Mystery.mp3").toString()));
    }

    public MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }
    public void setVolume(double value)
    {
        volume = value;
        mediaPlayer.setVolume(volume);
    }


    public void setTrack(MusicTrack musicTrack)
    {
        mediaPlayer = new MediaPlayer(musicTracks.get(musicTrack));
        mediaPlayer.setVolume(volume);
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
    }

    public void play()
    {
        mediaPlayer.play();
    }

    public void stop()
    {
        mediaPlayer.stop();
    }
}
