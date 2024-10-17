<div align="center">

[![CC_BY_--NC--_SA_4.0 License][license-shield]][license-url]
[![Size][size-shield]][size-url]
[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]
[![Downloads][downloads-shield]][downloads-url]
[![Stars][stars-shield]][stars-url]\
[![Java][java-shield]][java-url]
[![IntelliJ IDEA][intellij-shield]][intellij-url]

</div>

# Cluedo
Applicazione per il gioco Cluedo, NON UFFICIALE e SENZA SCOPO DI LUCRO.

Nato come progetto del corso di Ingegneria del Software della triennale in Ingegneria Informatica presso l'unibo, ho deciso di portarlo a termine, sostituendo il prototipo con quello che sarà un gioco completo e funzionante.

*In corso di sviluppo: quando avrò tempo lo porterò a termine*.

### Esecuzione
TO-DO

### Roadmap

- [ ] Game Engine
  - [ ] Classi e metodi utili (e.g. ottenere la lista delle possibili posizioni di arrivo)
- [ ] AI
- [ ] Grafica:
  - [ ] Taccuino, note etc. devono essere finestre aggiuntive (dialog?) e possono essere aperte contemporaneamente
  - [ ] Implementazione GUI Swing
  - [ ] Permettere la scelta di CLI, GUI JavaFX e GUI Java Swing

- [ ] Sfruttare le classi del JavaFX multiplayer lobby system per il multigiocatore
- [ ] Una volta ottenuto un prototipo giocabile, fare la grafica. NB: l'associazione delle caselle ai punti specifici nella mappa la fa il controller.
* NB: modificarlo e scriverlo in INGLESE principalmente.
* Classi base del model
* Trovare qualcuno che mi disegni la mappa
* Sistema per il multigiocatore molto base (server host, client si connettono al server, con socket UDP/TCP. Sistema di room con creazione/unione)
* Per le socket usare un thread aggiuntivo: quando l'utente seleziona "crea una stanza" viene creato un oggetto Server che, dopo il set dei parametri si mette in listen delle connessioni. Quando l'utente seleziona "unisciti ad una stanza" viene creato un oggetto Client che si connette al server. Serve thread separato altrimenti le chiamate bloccanti interferiscono con la GUI (sempre che funzionino, perché javafx si arrabbia spesso) - pattern observer(?)
* Scrivere documentazione su tutto. Fondamentalmente su come funzionano client e server, quali thread eseguono, quali sono i workflow e quali sono le sequenze di scambio di messaggi. Utilizzare anche dei diagrammi UML.
* ATTENZIONE: aggiungere chiusura di tutto quando si chiude l'app (potrebbero esserci dei thread attivi)
* Persistenza impostazioni varie con parsing XML

Niente gestore sicurezza che tanto non serve a una mazza.

### Multiplayer execution flow:
1. Un utente1 inserisce il nickname e seleziona "crea una lobby";
2. viene creato il Server:
  - il server crea un thread che crea una socket, la binda all'address locale, porta 9001 e si mette in ascolto (il thread viene creato perché ascoltare sulla socket è un'operazione bloccante;
4. un utente2 inserisce il nickname, l'IP del server a cui si vuole connettere, e prova ad unirsi ad una stanza.
5. dunque viene creato il Client:
  - il client crea un thread che crea la socket, si connette al server, e gli invia un messaggio CONNECT;
  - il server risponde dicendo se è possibile la connessione (CONNECT_OK, in caso affermativo, CONNECT_FAILED, in caso negativo);
  - una volta ricevuto l'OK, il client riceve un secondo messaggio, contenente la lista degli utenti connessi (così può aggiornare l'interfaccia grafica);
  - dopodiché si mette in ascolto per i messaggi futuri.



### Built With
versione Java: JavaSE-11 (jdk-11.0.11)<br/>
versione JavaFX: JavaFX 11 (javafx-sdk-11.0.2)

### References
- Progetto utilissimo per implementare la chat della lobby: [JavaFX-Chat](https://github.com/DomHeal/JavaFX-Chat)
- Tutorial Gioco JavaFX: [Space Shooter](https://www.youtube.com/watch?v=6BKI26gxK2Q)
- Simple API with high-level abstraction, to build a game without troubles: [Java FXGL](https://www.youtube.com/watch?v=gj0yKmsKwvc)

<div align="center">

[![LinkedIn][linkedin-shield]][linkedin-url]
[![GitHub followers][github-shield]][github-url]

</div>

[downloads-shield]: https://img.shields.io/github/downloads/mikyll/Cluedo/total
[downloads-url]: https://github.com/mikyll/Cluedo/releases/latest
[contributors-shield]: https://img.shields.io/github/contributors/mikyll/Cluedo
[contributors-url]: https://github.com/mikyll/Cluedo/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/mikyll/Cluedo
[forks-url]: https://github.com/mikyll/Cluedo/network/members
[size-shield]: 	https://img.shields.io/github/repo-size/mikyll/Cluedo
[size-url]: https://github.com/mikyll/Cluedo
[stars-shield]: https://img.shields.io/github/stars/mikyll/Cluedo?style=flat
[stars-url]: https://github.com/mikyll/Cluedo/stargazers
[issues-shield]: https://img.shields.io/github/issues/mikyll/Cluedo
[issues-url]: https://github.com/mikyll/Cluedo/issues
[license-shield]: https://img.shields.io/badge/license-CC_BY--NC--SA_4.0-lightgrey.svg
[license-url]: https://github.com/mikyll/Cluedo/blob/master/LICENSE

[java-shield]: https://custom-icon-badges.herokuapp.com/badge/Java-ED8B00?logo=java&logoColor=white
[java-url]: https://www.java.com
[intellij-shield]: https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?logo=intellij-idea&logoColor=blue
[intellij-url]: https://www.jetbrains.com/idea/ 

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?logo=linkedin&colorB=0077B5
[linkedin-url]: https://www.linkedin.com/in/michele-righi/?locale=it_IT
[github-shield]: https://img.shields.io/github/followers/mikyll.svg?style=social&label=Follow
[github-url]: https://github.com/mikyll
