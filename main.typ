#show heading.where(level: 1): it => pagebreak(weak: true) + it
#set document(
  title: "Projektdokumentation",
  author: "Aleyna Yalcinkaya",
)

#set page(
  paper: "a4",
  margin: (top: 2.5cm, bottom: 2.5cm, left: 3cm, right: 2.5cm),
  numbering: "1",
)

#set text(
  font: "Libertinus Serif",
  size: 11pt,
  lang: "de",
)

#set par(
  justify: true,
  leading: 0.7em,
)

#set heading(numbering: "1.")

#set page(
  header: context {
    if counter(page).get().first() > 1 [
      #text(size: 9pt)[Projektdokumentation]
      #h(1fr)
      #text(size: 9pt)[Sportplatzfinder]
    ]
  },
  footer: context {
    if counter(page).get().first() > 1 [
      #align(center)[
        #text(size: 9pt)[#counter(page).display("1")]
      ]
    ]
  },
)

// TITELBLATT
#align(center)[
  #v(3cm)

  #text(size: 13pt)[Hochschule Esslingen]

  #v(3cm)

  #text(size: 28pt, weight: "bold")[Projektdokumentation]

  #v(1cm)

  #text(size: 18pt)[Sportplatzfinder]

  #v(2cm)

  #line(length: 60%)

  #v(2cm)

  #set text(size: 12pt)
  Team: Serhan Sarikaya, Eray Köse, Aleyna Yalcinkaya \
  Modul: Projekt Softwaretechnik und Medieninformatik \
  Betreuung: Michael Watzko \
]

#pagebreak()

// INHALTSVERZEICHNIS
#outline(title: [Inhaltsverzeichnis], depth: 3)

#pagebreak()

// KAPITEL
= Problemstellung und Zielgruppe
In diesem Kapitel werfen wir einen Blick auf die aktuelle Ausgangslage, leiten daraus unsere Projektziele ab und definieren, für wen wir den Sportplatzfinder eigentlich bauen.

== Ausgangssituation
Sportbegeisterte stehen häufig vor der Herausforderung, unkompliziert geeignete und frei zugängliche Sportstätten (wie Basketballkörbe, Bolzplätze oder Tischtennisplatten) in ihrer direkten Umgebung zu finden. Es mangelt an einer zentralen Übersicht, die nicht nur den Standort, sondern auch verlässliche Informationen über die aktuelle Ausstattung, den Untergrund oder die Beleuchtung bereitstellt. Bei kartenbasierten Crowdsourcing-Plattformen besteht zudem das klassische "Henne-Ei-Problem": Ohne eingetragene Daten ist die Plattform für Nutzer unattraktiv, ohne Nutzer werden jedoch keine Daten generiert.

== Zielsetzung
Das primäre Ziel dieses Projekts ist die Entwicklung der Webanwendung "Sportplatzfinder", die als zentrales Verzeichnis für öffentliche und private Sportstätten dient. Um das initiale Datenproblem zu überwinden, wird die Datenbank zum Start der Plattform mit bestehenden Datensätzen und Dummy-Daten für den Raum Esslingen und Umgebung via Seed-Skript befüllt. Darauf aufbauend ermöglicht die Plattform ein Community-getriebenes Crowdsourcing, bei dem Nutzer selbst neue Plätze eintragen und bestehende bewerten können.

== Zielgruppe
Der Sportplatzfinder bedient zwei zentrale Nutzergruppen, die sich gegenseitig ergänzen. \
Auf der aktiven Nutzerseite *(User)* stehen Menschen jeden Alters (vom spontanen Hobbysportler bis zum Jugendlichen), die eine unkomplizierte Lösung suchen, um Plätze in ihrer Nähe zu finden, zu bewerten oder selbst neue Orte einzutragen.\ Auf der Verwaltungsseite *(Admin)* richtet sich die Plattform an Betreiber oder kommunale Mitarbeiter. Sie bekommen ein einfaches Werkzeug an die Hand, um die von der Community eingetragenen Daten zu prüfen, freizugeben und das System sauber zu halten.

= Geforderter Funktionsumfang
Der Funktionsumfang definiert die interaktiven und technischen Fähigkeiten der Anwendung, unterteilt in funktionale und nichtfunktionale Anforderungen sowie eine klare Abgrenzung.

== Funktionale Anforderungen
Die App verzichtet bewusst auf komplizierte Hierarchien und beschränkt sich auf zwei zentral über Keycloak verwaltete Rollen:\
*USER:* 
+ Suchen & Filtern: Das System muss dem User die Möglichkeit bieten, Plätze auf einer Karte zu suchen und zu filtern.
+ Platz eintragen: Das System muss dem User die Möglichkeit bieten, neue Plätze inklusive Fotos und Ausstattungs-Informationen einzutragen.
+ Reviews verfassen: Das System muss dem User die Möglichkeit bieten, Bewertungen in Form von Sternen und Texten abzugeben.
+ Korrektur melden: Das System muss dem User die Möglichkeit bieten, veraltete Platzinformationen zur Korrektur zu melden.\
*ADMIN:* 
+ Status-Verwaltung: Das System muss neu eingetragene Plätze standardmäßig als PENDING kennzeichnen und verbergen.
+ Freigabe durch Admin: Das System muss dem Admin die Möglichkeit bieten, ausstehende Plätze zu prüfen und auf ACTIVE zu schalten.
+ Korrektur-Dashboard: Das System muss dem Admin die Möglichkeit bieten, eingegangene Korrekturvorschläge über ein Dashboard per Ein-Klick-Funktion zu bearbeiten.
+ Rollenverwaltung: Das System muss fähig sein, die Benutzerrollen User und Admin über Keycloak zu verwalten.

== Nichtfunktionale Anforderungen
+ Responsive Design / Mobile First: Das System muss fähig sein, die Benutzeroberfläche sowohl für mobile Endgeräte als auch für Desktop-Anwendungen optimiert darzustellen.
+ Sicherheit: Das System muss fähig sein, das Benutzermanagement und die Authentifizierung nach aktuellen Sicherheitsstandards über Keycloak abzusichern.
+ Performance: Das System sollte fähig sein, Kartendaten und Navigationsinhalte ohne merkliche Verzögerung zuladen und bereitzustellen.

== Abgrenzung des Funktionsumfangs
Um zügig eine erste funktionierende Version (Minimum Viable Product) an den Start zu bringen, ziehen wir ganz klare Grenzen. In dieser Ausbaustufe verzichten wir bewusst auf aufwendige Features wie ein integriertes Buchungs- oder Bezahlsystem für kostenpflichtige Anlagen. Ebenso lassen wir ein verschachteltes Rechtemanagement – zum Beispiel mit zusätzlichen Moderatoren-Ebenen – komplett weg, um die Systemarchitektur schlank und leicht wartbar zu halten.

= UI-Entwürfe
Die Gestaltung der Benutzeroberfläche fokussiert sich auf intuitive Bedienbarkeit und schnelle Informationserfassung auf mobilen Endgeräten.

== Gestaltungskonzept
Das Design-Konzept orientiert sich an etablierten Karten-Apps. Im Zentrum steht eine großflächige, interaktive Kartenansicht, über der sich ein schwebendes Such- und Filtermenü befindet. Die Farbgebung ist kontrastreich gewählt, um die Lesbarkeit bei direkter Sonneneinstrahlung im Freien zu gewährleisten.

== Entwürfe der Benutzeroberfläche
Dieser Abschnitt visualisiert die Gestaltung der Benutzeroberfläche. Dabei wird der Designprozess von den initialen, groben Skizzen bis hin zu den detaillierten, digitalen Mockups schrittweise dokumentiert.
=== Unsere ersten Konzepte

Zu Beginn der Konzeption stand die Frage im Mittelpunkt, wie die Nutzerführung so intuitiv wie möglich gestaltet werden kann. Die ersten Low-Fidelity-Entwürfe konzentrierten sich daher ausschließlich auf die grundlegende Navigation und den logischen Aufbau der Anwendung. Durch den bewussten Verzicht auf Farben und grafische Details konnte die reine Bedienbarkeit der interaktiven Karte in den Fokus gerückt werden.
#grid(
  columns: (1fr, 1fr),
  gutter: 12pt,
  align: top,

  [
    #figure(
      image("images/ui_map.png", height: 12cm),
      caption: [UI-Entwurf der mobilen Kartenansicht mit Suchleiste und Filtern],
    )
  ],

  [
    #figure(
      image("images/ui_sportplatz_info.png", height: 12cm),
      caption: [UI-Entwurf des [...]],
    )
  ],
)

=== Weiterverarbeitung von unserem Konzept

Die derzeitigen Entwürfe zeigen ein klares Mobile-First-Design. Die App lässt sich leicht bedienen. Nutzer können draußen alle Infos schnell erfassen. Dieser Stand ist nicht final und wird im weiteren Projektverlauf noch angepasst. Aktuell gibt es drei Hauptbereiche: die Karte, eine Liste und die Detailseite.

Die Startseite bietet eine interaktive Karte. Oben befindet sich eine Suchleiste. Darunter liegen Filter für Sportarten wie Basketball oder Fußball. Spezielle Icons auf der Karte zeigen direkt die Art des Platzes. Unten gibt es eine Menüleiste. Sie führt zur Entdecken-Seite, zur Merkliste und zum Profil.

Alternativ zur Karte gibt es eine Listenansicht. Hier stehen die Plätze übersichtlich untereinander. Wichtige Details sind sofort sichtbar. Kurze Tags zeigen zum Beispiel den Untergrund, die Ausstattung oder ob der Platz kostenlos ist.

Ein Klick auf einen Platz öffnet die Detailansicht. Sie zeigt ein großes Bild und eine kurze Beschreibung. Darunter stehen genaue Infos zur Ausstattung. Dazu gehören der Bodenbelag, die Anzahl der Körbe oder das Licht. Ganz unten gibt es einen großen Button. Dieser startet die direkte Routenplanung zum Platz.

#grid(
  columns: (1fr, 1fr, 1fr), // Hier auf 3 Spalten geändert für 3 Handys nebeneinander
  gutter: 12pt,
  align: top,

  [
    #figure(
      image("images/SportplätzeEntdecken.png", height: 8cm), // Höhe leicht reduziert für 3 nebeneinander
      caption: [Entdecken-Ansicht mit Karte],
    )
  ],

  [
    #figure(
      image("images/SucheSeite.png", height: 8cm),
      caption: [Listenansicht der Suchergebnisse],
    )
  ],

  [
    #figure(
      image("images/DetailAnsicht.png", height: 8cm),
      caption: [Detailansicht eines Sportplatzes],
    )
  ]
)

== Bedienabläufe
Der Kern-Workflow für das Eintragen eines neuen Platzes umfasst vier einfache Schritte. Zuerst tippt der Nutzer auf der Karte auf den genauen Ort. Danach füllt er ein kurzes Formular aus. Dort trägt er die Sportart und die Ausstattung ein und lädt ein Bild hoch. Zum Schluss klickt er auf Speichern. Das System bestätigt den Eingang. Der neue Platz ist noch nicht öffentlich sichtbar. Er erhält den Status PENDING. Ein Admin muss den Eintrag nun erst prüfen und freigeben.

= Architektur
Dieses Kapitel beschreibt die konzeptionelle und technologische Architektur des Systems sowie die Grundlagen der Datenhaltung und der Kommunikation zwischen den einzelnen Komponenten. Die Architektur basiert auf einer Trennung von Frontend, Backend und Datenbank.

== Konzeptionelle Architektur
Das System folgt einer klassischen Client-Server-Architektur. Das Frontend wird als Single Page Application (SPA) mit einer Erweiterung zur Progressive Web App (PWA) realisiert. Dies ermöglicht Nutzern, die Webanwendung wie eine native App auf dem Homescreen abzulegen, ohne den Umweg über einen App Store gehen zu müssen. 

Das Frontend ist für die Darstellung der Benutzeroberfläche und die Interaktion mit den Nutzern verantwortlich. Die Verarbeitung fachlicher Anwendungslogik erfolgt im Backend, das über eine REST-API mit dem Frontend kommuniziert. Die dauerhafte Speicherung der Anwendungsdaten übernimmt eine PostgreSQL-Datenbank.

Die Architektur trennt damit Präsentation, Geschäftslogik und Datenhaltung voneinander. Dadurch können die einzelnen Komponenten unabhängig weiterentwickelt und gewartet werden.

== Technologische Architektur
Frontend: Vue.js dient zur Entwicklung der Benutzeroberfläche. Vite wird als Entwicklungs- und Build-Werkzeug eingesetzt. Die Erweiterung zur Progressive Web App ermöglicht unter anderem die Installation der Anwendung auf geeigneten Endgeräten.

Kartenintegration: Leaflet.js übernimmt die interaktive Kartendarstellung. OpenStreetMap (OSM) stellt die zugrunde liegenden Kartendaten bereit.

Backend: Java mit Spring Boot bildet die serverseitige Anwendungslogik. Die Implementierung ist in Controller-, Service- und Repository-Schichten gegliedert. Controller verarbeiten eingehende Anfragen, Services enthalten die Geschäftslogik und Repositories übernehmen den Datenbankzugriff.

Authentifizierung und Autorisierung: Keycloak übernimmt das Identitäts- und Zugriffsmanagement. Die Integration mit dem Backend erfolgt über die konfigurierte Authentifizierungs- und Autorisierungsmechanik. Keycloak wird in einer Docker-Umgebung betrieben.

== Datenhaltung und Schnittstellen

Als relationales Datenbanksystem wird PostgreSQL verwendet. Die Datenbank speichert die strukturierten Anwendungsdaten und bildet deren Beziehungen ab.

Für standortbezogene Funktionen, insbesondere räumliche Abfragen und Umkreissuchen, wird die PostgreSQL-Erweiterung PostGIS eingesetzt. Sie ermöglicht die Speicherung und Verarbeitung geografischer Daten sowie räumlicher Abfragen direkt in der Datenbank.

Bilddaten werden entweder direkt in der Datenbank oder im Dateisystem des Servers gespeichert.

Die Absicherung geschützter API-Endpunkte erfolgt über Spring Security in Verbindung mit Keycloak. Dabei werden Authentifizierungsinformationen in Form von Tokens verarbeitet und die Berechtigungen der jeweiligen Anfrage geprüft.

= Projektmanagement
Eine strukturierte Organisation und klare Arbeitsprozesse sind für die erfolgreiche Durchführung des Teamprojekts unabdingbar.

== Vorgehensmodell
Das Projektmanagement erfolgt agil. Für die Versionskontrolle wird das GitFlow-Modell eingesetzt: Neue Features und Aufgaben werden in isolierten Branches (basierend auf GitHub Tickets) entwickelt, um Merge-Konflikte zu vermeiden. Die Dokumentation wird parallel zum Code lokal in Typst gepflegt und über das Repository synchronisiert. Das Team trifft sich wöchentlich montags um 16:00 Uhr zu einem Sync- und FAQ-Meeting.

== Arbeitspakete und Meilensteine
Die Umsetzung des Projekts ist in mehrere Phasen gegliedert, die im Verlauf der Entwicklung überprüft und bei Bedarf angepasst werden:

1. *Projekt-Setup:* Einrichtung der Repositories und der Typst-Dokumentation sowie Bereitstellung der Keycloak-Instanz und der Datenbank.
2. *Backend Core:* Entwicklung der serverseitigen Anwendungslogik mit Spring Boot, einschließlich Datenmodellierung, Anbindung an PostgreSQL und Implementierung der zentralen Funktionen für Benutzer und Sportplätze.
3. *Frontend & Map:* Entwicklung der Vue.js-Webanwendung, Integration von Leaflet zur Darstellung interaktiver Karten und Anbindung der Spring-Boot-REST-API.
4. *Admin & Auth:* Umsetzung des Freigabeprozesses für Sportplätze mit den Status `PENDING` und `ACTIVE` sowie Absicherung der geschützten Routen.
5. *Datenintegration & Feinschliff:* Befüllung der Datenbank mit Sportplatzdaten aus Esslingen mithilfe eines Seed-Skripts sowie abschließende Tests und Fehlerbehebungen.

Der erste Meilenstein, die Präsentation der Architektur und der UI-Entwürfe, ist für den [Datum] vorgesehen.

== Risiken und Qualitätssicherung

Ein wesentliches Risiko bei Crowdsourcing-Plattformen besteht darin, dass Nutzer Spam oder fehlerhafte Daten eintragen. Dieses Risiko wird durch einen Freigabeprozess reduziert, bei dem neu eingereichte Sportplätze zunächst den Status `PENDING` erhalten und erst nach Prüfung durch Administratoren als `ACTIVE` veröffentlicht werden.

Technische Risiken, beispielsweise Fehler durch parallele Codeänderungen oder Integrationsprobleme zwischen den Komponenten, werden durch die strukturierte Verwaltung von Aufgaben über GitHub-Tickets, Code-Reviews und Pull-Requests vor dem Merge in den Main-Branch reduziert. Ergänzend tragen geeignete Tests dazu bei, Fehler frühzeitig zu erkennen und die Funktionsfähigkeit der Anwendung sicherzustellen.

= Aufwandsschätzung
Die Aufwandsschätzung dient der realistischen Planung der benötigten Entwicklungsressourcen über das laufende Semester. Sie ermöglicht eine grobe Priorisierung der Aufgaben und unterstützt die zeitliche Planung der einzelnen Projektphasen.

== Schätzgrundlage

Die Aufwandsschätzung basiert auf Erfahrungswerten aus vergleichbaren studentischen Projekten sowie auf einer Einschätzung des Umfangs und der technischen Komplexität der geplanten Funktionen. Da es sich um eine frühe Planung handelt, können die tatsächlichen Aufwände im Projektverlauf von den Schätzwerten abweichen.

== Aufwand nach Arbeitspaketen

*- Projektmanagement & Dokumentation: 15 %*\
Beinhaltet die kontinuierliche Steuerung des Projekts, die Abstimmung im Team sowie die Erstellung der technischen Architektur- und Projektdokumentation. Dies stellt eine geordnete Umsetzung und transparente Nachvollziehbarkeit aller Projektschritte sicher.\

*- Infrastruktur & Setup (Keycloak, Datenbank, Server): 15 %*\
Umfasst das Einrichten der Entwicklungsumgebungen, der Serverstrukturen sowie die Konfiguration von Keycloak für die Authentifizierung und Autorisierung. Zudem wird die Datenbank aufgesetzt und abgesichert, um eine stabile Basis für Backend und Frontend zu schaffen.\

*- Backend-Entwicklung (Spring Boot, PostgreSQL/PostGIS): 30 %*\
Deckt die Implementierung der Geschäftslogik in Spring Boot sowie die Modellierung der relationalen Geodatenbank mittels PostgreSQL und PostGIS ab. Hier werden die nötigen Schnittstellen bereitgestellt, um Daten effizient zu verarbeiten und sicher bereitzustellen.\

*- Frontend-Entwicklung (Vue.js, Leaflet, UI/UX): 30 %*\
Beinhaltet die Entwicklung der Benutzeroberfläche mit Vue.js sowie die Einbindung interaktiver Kartenkomponenten über Leaflet. Das Hauptaugenmerk liegt auf einer intuitiven UI/UX, damit Endanwender die Geodaten optimal visualisieren und bedienen können.\

*- Tests, Fehlerbehebung & PWA-Integration: 10 %*\
Dient der Qualitätssicherung durch das Schreiben und Ausführen von Tests sowie das Beheben auftretender Fehler. Ergänzend wird die Anwendung als Progressive Web App konfiguriert, um eine mobile Nutzung und Grundfunktionen wie Offline-Verfeinerungen zu ermöglichen.\
\
\
\
\
\
\
\
\
\
\
\
== Zeitplanung und Puffer

Die reine Entwicklungsphase soll vier Wochen vor den finalen Semesterabgaben abgeschlossen sein. Dieser zeitliche Puffer ermöglicht die Behebung spät auftretender Fehler, die Überprüfung und Verbesserung des Seed-Skripts zur Bereitstellung von Testdaten für Esslingen sowie die abschließende Überarbeitung und Formatierung der Projektdokumentation. Die zeitliche Verteilung der Arbeitspakete über den Projektzeitraum ist in @gantt-diagramm dargestellt.

#figure(
  image("images/grantt.png", width: 100%),
  caption: [Gantt-Diagramm des Projektzeitplans.],
) <gantt-diagramm>


= Fazit und Ausblick
Das Projekt "Sportplatzfinder" bietet eine zeitgemäße, mobile-first Lösung für ein alltägliches Problem von Freizeitsportlern. Durch den Einsatz moderner Technologien wie Vue.js, Spring Boot und PostGIS wird ein solides und performantes Fundament geschaffen. Der bewusste Verzicht auf komplexe Features in der ersten Phase und die Lösung des Daten-Henne-Ei-Problems durch gezielte Initialdaten sichern einen schnellen Projekterfolg. Zukünftige Iterationen könnten um Echtzeit-Auslastungsanzeigen, Community-Events oder ein Buchungssystem für kostenpflichtige Anlagen erweitert werden.

= Quellenverzeichnis
- Atlassian. (o. J.). *Gitflow Workflow*. Abgerufen im Oktober 2026, von https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow
- arc42. (o. J.). *Overview & Templates*. Abgerufen im Oktober 2026, von https://arc42.org/
- Typst. (o. J.). *Compose papers faster*. Abgerufen im Oktober 2026, von https://typst.app/
- Vue.js. (o. J.). *Quick Start*. Abgerufen im Oktober 2026, von https://vuejs.org/guide/quick-start.html
