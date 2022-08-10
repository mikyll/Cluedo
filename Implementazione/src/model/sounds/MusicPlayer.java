package model.sounds;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {
private final static String dir = "/resources/music/";
	
	private static MusicPlayer instance = null;
	private static MediaPlayer mediaPlayer;
	
	private MusicPlayer() {}	
	public static synchronized MusicPlayer getInstance(String filename)
	{
		if(instance == null)
		{
			instance = new MusicPlayer();
			mediaPlayer = new MediaPlayer(new Media(MusicPlayer.class.getResource(dir + filename).toString()));
		}
		return instance;
	}
	public MediaPlayer getMediaPlayer()
	{
		return mediaPlayer;
	}
	public void setVolume(double value)
	{
		mediaPlayer.setVolume(value);
	}
	public void play()
	{
		mediaPlayer.play();
	}
	public void setLoopNumber(int value)
	{
		mediaPlayer.setCycleCount(value);
	}
}
