package model.impostazioni;


// Singleton
public class Impostazioni {
	private static Impostazioni istanza = null;
	
	private static double volumeMusica;
	private static boolean enableMusica;	
	private static double effettiAudio;
	private static boolean enableEffetti;
	private static String lingua;
	private static String[] lingueDisponibili;
	
	private static boolean primoAvvio;
	private static boolean primoAccesso;
		
	private Impostazioni() {}
	
	public static synchronized Impostazioni getIstanza()
	{
		if(istanza == null)
		{
			istanza = new Impostazioni();
			
			/*
			// prova senza parser XML
			volumeMusica = 35.0;
			enableMusica = true;
			effettiAudio = 40.0;
			enableEffetti = true;
			lingua = "it-IT";
			
			primoAvvio = true;
			primoAccesso = true;*/
			lingueDisponibili = new String[2];
			lingueDisponibili[0] = "it-IT";
			lingueDisponibili[1] = "en-US";
		}
		return istanza;
	}
	
	public double getVolumeMusica() {return volumeMusica;}
	public boolean isEnabledMusica() {return enableMusica;}
	public double getEffettiAudio() {return effettiAudio;}
	public boolean isEnabledEffetti() {return enableEffetti;}
	public String getLingua() {return lingua;}	
	public String[] getLingueDisponibili() {return lingueDisponibili;}
	public boolean isPrimoAvvio() {return primoAvvio;}
	public boolean isPrimoAccesso() {return primoAccesso;}
	
	
	public void setVolumeMusica(double volumeGioco)
	{
		if(volumeGioco >= 0.0 && volumeGioco <= 100.0)
			Impostazioni.volumeMusica = volumeGioco;
		else Impostazioni.volumeMusica = 50.0; // default
	}
	public void setEnabledMusica(boolean enableMusica)
	{
		Impostazioni.enableMusica = enableMusica;
	}
	public void setEffettiAudio(double effettiAudio)
	{
		if(effettiAudio >= 0.0 && effettiAudio <= 100.0)
			Impostazioni.effettiAudio = effettiAudio;
		else Impostazioni.effettiAudio = 50.0; // default
	}
	public void setEnabledEffetti(boolean enableEffetti)
	{
		Impostazioni.enableEffetti = enableEffetti;
	}
	public void setLingua(String lingua) 
	{
		for(String s : lingueDisponibili)
		{
			if(s.equalsIgnoreCase(lingua))
			{
				Impostazioni.lingua = lingua;
				return;
			}
		}
		Impostazioni.lingua = "it-IT"; // default
	}
	public void setPrimoAvvio(boolean primoAvvio) 
	{
		Impostazioni.primoAvvio = primoAvvio;
	}
	public void setPrimoAccesso(boolean primoAccesso)
	{
		Impostazioni.primoAccesso = primoAccesso;
	}

	
	public String toString()
	{
		return "VolumeMusica: " + volumeMusica + "% (" + (enableMusica ? "ON)\n" : "OFF)\n") +
				"EffettiAudio: " + effettiAudio + "% (" + (enableEffetti ? "ON)\n" : "OFF)\n") +
				"Lingua: " + lingua + "\n" +
				"primoAvvio: " + primoAvvio + " - primoAccesso: " + primoAccesso;	
	}
}
