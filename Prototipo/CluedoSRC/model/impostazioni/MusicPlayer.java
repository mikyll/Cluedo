package model.impostazioni;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

// Singleton
public class MusicPlayer {
	private final static String dir = "/resources/music/";
	
	private static MusicPlayer istanza = null;
	private static MediaPlayer mediaPlayer;
	
	private MusicPlayer() {}	
	public static synchronized MusicPlayer getIstanza(String titolo)
	{
		if(istanza == null)
		{
			istanza = new MusicPlayer();
			mediaPlayer = new MediaPlayer(new Media(MusicPlayer.class.getResource(dir + titolo).toString()));
		}
		return istanza;
	}
	public MediaPlayer getMediaPlayer()
	{
		return mediaPlayer;
	}
	public static synchronized MusicPlayer createNewMusicPlayer(String titolo)
	{
		istanza = new MusicPlayer();
		mediaPlayer = new MediaPlayer(new Media(MusicPlayer.class.getResource(dir + titolo).toString()));
		return istanza;
	}
}
