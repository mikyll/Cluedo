package model.impostazioni;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


// FORMATO FILE Impostazioni.xml
//		
//		<impostazioni xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
//						xsi:noNamespaceSchemaLocation="Impostazioni.xsd">
//		
//		<impostazione>
//			<volumeMusica>50.0</volumeMusica>
//			<musicaON>true</musicaON>
//			<effettiAudio>50.0</effettiAudio>
//			<effettiON>true</effettiON>
//			<lingua>it-IT</lingua>
//		</impostazione>
//		<primoAvvio>true</primoAvvio>
//		<primoAccesso>true</primoAccesso>
//		
//		</impostazioni>
//

public class ImpostazioniParserXML {
	private static String schemaFeature = "http://apache.org/xml/features/validation/schema";
	private static String ignorableWhitespace = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
	private static String xmlFilename = "Impostazioni.xml";
		
	public static void createDefaultImpostazioni()
	{
		BufferedWriter bw = null;
		try {
			Writer writer = new FileWriter("Impostazioni.xml");
			bw  = new BufferedWriter(writer);
			bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
					"\r\n" + 
					"<impostazioni xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \r\n" + 
					"         xsi:noNamespaceSchemaLocation=\"Impostazioni.xsd\">\r\n" + 
					"\r\n" + 
					"	<impostazione>\r\n" + 
					"		<volumeMusica>50.0</volumeMusica>\r\n" + 
					"		<enableMusica>true</enableMusica>\r\n" + 
					"		<effettiAudio>50.0</effettiAudio>\r\n" + 
					"		<enableEffetti>true</enableEffetti>\r\n" + 
					"		<lingua>it-IT</lingua>\r\n" + 
					"	</impostazione>\r\n" + 
					"	<primoAvvio>true</primoAvvio>\r\n" + 
					"	<primoAccesso>true</primoAccesso>\r\n" + 
					"	\r\n" + 
					"</impostazioni>");
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Impostazioni loadImpostazioni()
	{
		Impostazioni result = Impostazioni.getIstanza();
		
		try   
		{
			// 1) Costruzione DocumentBuilder che validi il documento XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			dbf.setNamespaceAware(true);
			
			// specifica che si sta validando tramite XSD
			dbf.setFeature(schemaFeature,true);
			// specifica che gli ignorable whitespaces tra un tag e l'altro devono essere scartati
			dbf.setFeature(ignorableWhitespace, false);
			
			// 2) Attivazione del gestore di non-conformita'
			DocumentBuilder db = dbf.newDocumentBuilder();
			// db.setErrorHandler(new ErrorHandler()); // è possibile crearne uno specifico
			
			// 3) Parsing documento per ottenere un documento DOM
			Document domDocument = db.parse(new File(xmlFilename));
			domDocument.getDocumentElement().normalize();
			
			// 4) Utilizzo
			NodeList impostazioneList = domDocument.getElementsByTagName("impostazione").item(0).getChildNodes();
			Node volumeMusica = impostazioneList.item(0);
			Node enableMusica = impostazioneList.item(1);
			Node effettiAudio = impostazioneList.item(2);
			Node enableEffetti = impostazioneList.item(3);
			Node lingua = impostazioneList.item(4);
			Node primoAvvio = domDocument.getElementsByTagName("primoAvvio").item(0);
			Node primoAccesso = domDocument.getElementsByTagName("primoAccesso").item(0);
			
			result.setVolumeMusica(Double.valueOf(volumeMusica.getTextContent()));
			result.setEnabledMusica(Boolean.valueOf(enableMusica.getTextContent()));
			result.setEffettiAudio(Double.valueOf(effettiAudio.getTextContent()));
			result.setEnabledEffetti(Boolean.valueOf(enableEffetti.getTextContent()));
			result.setLingua(lingua.getTextContent().toString());
			result.setPrimoAvvio(Boolean.valueOf(primoAvvio.getTextContent()));
			result.setPrimoAccesso(Boolean.valueOf(primoAccesso.getTextContent()));
			
		} catch (ParserConfigurationException e) {
			System.out.println("Errore di configurazione del parser");
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("Errori durante il parsing");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File di impostazioni '" + xmlFilename + "' non presente. Creazione file di default...");
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			System.out.println("Errore fatale: file di impostazioni non valido.");
			try {Thread.sleep(2000);} catch (InterruptedException e1) {e1.printStackTrace();}
			System.exit(1);		
		}
		System.out.println("File di impostazioni caricato correttamente.");
		
		return result;
	}
	public static void updateImpostazioni(Impostazioni impostazioni)
	{
		try   
		{
			// 1) Costruzione DocumentBuilder che validi il documento XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			dbf.setNamespaceAware(true);
			
			// specifica che si sta validando tramite XSD
			dbf.setFeature(schemaFeature,true);
			// specifica che gli ignorable whitespaces tra un tag e l'altro devono essere scartati
			dbf.setFeature(ignorableWhitespace, false);
			
			// 2) Attivazione del gestore di non-conformita'
			DocumentBuilder db = dbf.newDocumentBuilder();
			// db.setErrorHandler(new ErrorHandler()); // è possibile crearne uno specifico
			
			// 3) Parsing documento per ottenere un documento DOM
			Document domDocument = db.parse(new File(xmlFilename));
			domDocument.getDocumentElement().normalize();
			
			// 4) Utilizzo
			NodeList impostazioneList = domDocument.getElementsByTagName("impostazione").item(0).getChildNodes();
			Node volumeMusica = impostazioneList.item(0);
			Node enableMusica = impostazioneList.item(1);
			Node effettiAudio = impostazioneList.item(2);
			Node enableEffetti = impostazioneList.item(3);
			Node lingua = impostazioneList.item(4);
			Node primoAvvio = domDocument.getElementsByTagName("primoAvvio").item(0);
			Node primoAccesso = domDocument.getElementsByTagName("primoAccesso").item(0);
			
			volumeMusica.setTextContent(String.valueOf(impostazioni.getVolumeMusica()));
			enableMusica.setTextContent(String.valueOf(impostazioni.isEnabledMusica()));
			effettiAudio.setTextContent(String.valueOf(impostazioni.getEffettiAudio()));
			enableEffetti.setTextContent(String.valueOf(impostazioni.isEnabledEffetti()));
			lingua.setTextContent(String.valueOf(impostazioni.getLingua()));
			primoAvvio.setTextContent(String.valueOf(impostazioni.isPrimoAvvio()));
			primoAccesso.setTextContent(String.valueOf(impostazioni.isPrimoAccesso()));
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(domDocument);
			StreamResult result = new StreamResult(new File(xmlFilename));
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
			System.out.println("Errore di configurazione del parser");
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("Errori durante il parsing");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File di impostazioni '" + xmlFilename + "' non presente. Creazione file di default...");
			// e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		System.out.println("File di impostazioni aggiornato.");
	}
	/*public static void createDefaultDocument2()
	{
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			documentFactory.setValidating(true);
			documentFactory.setNamespaceAware(true);
			documentFactory.setFeature(schemaFeature,true);
			documentFactory.setFeature(ignorableWhitespace, false);
			
			DocumentBuilder documentBuilder;
			documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			
			// root element
			Element root = document.createElement("impostazioni");
			document.appendChild(root);
			
			// impostazione element
			Element impostazione = document.createElement("impostazione");
			root.appendChild(impostazione);
			
			// volumeMusica element
			Element volumeMusica = document.createElement("volumeMusica");
			volumeMusica.appendChild(document.createTextNode("50.0"));
			impostazione.appendChild(volumeMusica);
			
			// musicaON element
			Element musicaON = document.createElement("musicaON");
			musicaON.appendChild(document.createTextNode("true"));
			impostazione.appendChild(musicaON);
			
			// effettiAudio element
			Element effettiAudio = document.createElement("effettiAudio");
			effettiAudio.appendChild(document.createTextNode("50.0"));
			impostazione.appendChild(effettiAudio);
	
			// effettiON element
			Element effettiON = document.createElement("effettiON");
			effettiON.appendChild(document.createTextNode("true"));
			impostazione.appendChild(effettiON);
			
			// lingua element
			Element lingua = document.createElement("lingua");
			lingua.appendChild(document.createTextNode("it-IT"));
			impostazione.appendChild(lingua);
			
			// primoAvvio element
			Element primoAvvio = document.createElement("primoAvvio");
			primoAvvio.appendChild(document.createTextNode("true"));
			root.appendChild(primoAvvio);
			
			// primoAccesso element
			Element primoAccesso = document.createElement("primoAccesso");
			primoAccesso.appendChild(document.createTextNode("true"));
			root.appendChild(primoAccesso);
			
			
			
			// Create the xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File("temp.xml"));
			transformer.transform(domSource, streamResult);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}*/
}
