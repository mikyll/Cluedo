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
    private double volume;

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
        mediaPlayer.setVolume(value);
    }
    public void play(MusicTrack musicTrack)
    {
        mediaPlayer = new MediaPlayer(musicTracks.get(musicTrack));
        mediaPlayer.play();
    }
    public void setLoopNumber(int value)
    {
        mediaPlayer.setCycleCount(value);
    }
}
