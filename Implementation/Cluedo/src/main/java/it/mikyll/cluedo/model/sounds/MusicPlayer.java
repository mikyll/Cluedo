package it.mikyll.cluedo.model.sounds;

import it.mikyll.cluedo.model.settings.Settings;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
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
            try {
                if (musicTracks == null)
                {
                    loadMusic();
                }
                mediaPlayer = new MediaPlayer(musicTracks.get(MusicTrack.MENU));
                mediaPlayer.setVolume(volume / 100);
                mediaPlayer.setCycleCount(Integer.MAX_VALUE);
            } catch (MediaException e) {
                // Audio is not available (e.g. missing codecs): the game goes on without music
                System.out.println("Music disabled: " + e.getMessage());
                mediaPlayer = null;
            }
        }
        return instance;
    }

    private static void loadMusic() {
        musicTracks = new HashMap<>();

        musicTracks.put(MusicTrack.MENU, new Media(MusicPlayer.class.getResource(Settings.RESOURCES_PATH + "music/David Fesliyan - Unfolding Revelation.mp3").toString()));
        musicTracks.put(MusicTrack.GAME, new Media(MusicPlayer.class.getResource(Settings.RESOURCES_PATH + "music/George Arkomanis - Relaxation In Mystery.mp3").toString()));
    }

    public boolean isAvailable()
    {
        return mediaPlayer != null;
    }

    public void setVolume(double value)
    {
        volume = value;
        if (isAvailable())
            mediaPlayer.setVolume(volume / 100);
    }


    public void setTrack(MusicTrack musicTrack)
    {
        if (!isAvailable())
            return;
        this.stop();
        mediaPlayer = new MediaPlayer(musicTracks.get(musicTrack));
        loadedTrack = musicTrack;
        mediaPlayer.setVolume(volume / 100);
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
    }

    public void play()
    {
        if (!isAvailable())
            return;
        this.stop();
        mediaPlayer.play();
        isPlaying = true;
    }

    public void play(MusicTrack musicTrack)
    {
        if (isAvailable() && (!isPlaying || musicTrack != loadedTrack))
        {
            this.setTrack(musicTrack);
            mediaPlayer.play();
            isPlaying = true;
        }
    }

    public void stop()
    {
        if (isAvailable())
            mediaPlayer.stop();
        isPlaying = false;
    }
}
