package model.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class Tutorial {
	private final static String dir = "/resources/images/tutorial/";
	private String regoleGioco;
	private List<Image> listaImmagini;
	
	public Tutorial()
	{
		this.regoleGioco = "Loasgdfuiaebruaewb";
		
		this.listaImmagini = new ArrayList<Image>(4);
		this.initImmagini();
	}
	
	public String getRegoleGioco() {return this.regoleGioco;}
	public List<Image> getListaImmagini() {return this.listaImmagini;}
	
	
	
	
	
	
	
	private void initImmagini()
	{
		this.listaImmagini.add(new Image(dir + "first.png"));
		for(int i = 1; i <= 9; i++)
			this.listaImmagini.add(new Image(dir + "img" + i + ".png"));
		this.listaImmagini.add(new Image(dir + "last.png"));
	}
}
