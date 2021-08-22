[![Downloads][downloads-shield]][downloads-url]
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]
[![GitHub followers][github-shield]][github-url]

<h1 align="center">CluedoApp</h1>
Applicazione per il gioco Cluedo, NON UFFICIALE e SENZA SCOPO DI LUCRO.

Prendere ciò che era giusto del prototipo e piazzarlo in modo decente nell'implementazione vera e propria. NIENTE CODICE SPAGHETTI.
Usare il pattern observer per inviare i dati sulle socket tramite un thread secondario a quello della GUI.

### Esecuzione
TO-DO

### Roadmap
* Classi base del model
* Trovare qualcuno che mi disegni la mappa
* Sistema per il multigiocatore molto base (server host, client si connettono al server, con socket UDP/TCP. Sistema di room con creazione/unione)
* Per le socket usare un thread aggiuntivo: quando l'utente seleziona "crea una stanza" viene creato un oggetto Server che, dopo il set dei parametri si mette in listen delle connessioni. Quando l'utente seleziona "unisciti ad una stanza" viene creato un oggetto Client che si connette al server. Serve thread separato altrimenti le chiamate bloccanti interferiscono con la GUI (sempre che funzionino, perché javafx si arrabbia spesso) - pattern observer(?)

Niente gestore sicurezza che tanto non serve a una mazza.

### Built With
versione Java: JavaSE-11 (jdk-11.0.11)<br/>
versione JavaFX: JavaFX 11 (javafx-sdk-11.0.2)

### References

[downloads-shield]: https://img.shields.io/github/downloads/mikyll/ROQuiz/total
[downloads-url]: https://github.com/mikyll/ROQuiz/releases/latest
[contributors-shield]: https://img.shields.io/github/contributors/mikyll/ROQuiz
[contributors-url]: https://github.com/mikyll/ROQuiz/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/mikyll/ROQuiz
[forks-url]: https://github.com/mikyll/ROQuiz/network/members
[stars-shield]: https://img.shields.io/github/stars/mikyll/ROQuiz
[stars-url]: https://github.com/mikyll/ROQuiz/stargazers
[issues-shield]: https://img.shields.io/github/issues/mikyll/ROQuiz
[issues-url]: https://github.com/mikyll/ROQuiz/issues
[license-shield]: https://img.shields.io/github/license/mikyll/ROQuiz
[license-url]: https://github.com/mikyll/ROQuiz/blob/master/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?logo=linkedin&colorB=0077B5
[linkedin-url]: https://www.linkedin.com/in/michele-righi/?locale=it_IT
[github-shield]: https://img.shields.io/github/followers/mikyll.svg?style=social&label=Follow
[github-url]: https://github.com/mikyll
