package model.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class RegoleAiuto {
 private final static String dir = "/resources/images/regole/";
 private String regoleAiuto;
 private List<Image> listaImmagini;
 
 public RegoleAiuto()
 {
  this.regoleAiuto = "Loasgdfuiaebruaewb";
  
  this.listaImmagini = new ArrayList<Image>(4);
  this.initImmagini();
 }
 
 public String getRegoleGioco() {return this.regoleAiuto;}
 public List<Image> getListaImmagini() {return this.listaImmagini;}
 
 
 
 
 
 
 
 private void initImmagini()
 {  
  this.listaImmagini.add(new Image(dir + "img0.png"));
  this.listaImmagini.add(new Image(dir + "img1.png"));
  this.listaImmagini.add(new Image(dir + "img2.png"));
  this.listaImmagini.add(new Image(dir + "img3.png"));
  this.listaImmagini.add(new Image(dir + "img4.png"));
  this.listaImmagini.add(new Image(dir + "img5.png"));
  this.listaImmagini.add(new Image(dir + "img6.png"));
  this.listaImmagini.add(new Image(dir + "img7.png"));
 }
}