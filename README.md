## Auteur
  BEN FARES Mohamed



Une application Java avec interface graphique (JavaFX) permettant de convertir des fichiers ou du texte **XML en JSON** et inversement (**JSON en XML**).

Ce projet a pour but pédagogique de comparer deux approches :
1.  **Approche Manuelle** : Utilisation d'algorithmes et d'expressions régulières (Regex) sans librairie externe.
2.  **Approche Librairie** : Utilisation de la librairie **Jackson** pour une conversion robuste.

## Fonctionnalités

* **Conversion Bidirectionnelle** : XML vers JSON et JSON vers XML.
* **Double Implémentation** : Choix entre le convertisseur manuel ou automatique.
* **Interface Graphique (GUI)** : Interface utilisateur moderne construite avec JavaFX.
* **Gestion des Attributs** : Le convertisseur manuel gère les attributs XML et les transforme en champs JSON.

## Où trouver le vidéo de demonstration ?
https://drive.google.com/file/d/1mF-kdxDHOCbRULIoDN6XUsL0nijm3MfA/view?usp=drive_link


## Où trouver les codes sources ?


Voici l'arborescence du projet et l'emplacement des fichiers clés :

```text
src
└── main
    ├── java
    │   └── org.example.xmljsonconverter
    │       ├── AppLauncher.java            # Point d'entrée de l'application (pour éviter les erreurs de module)
    │       ├── MainApp.java                # Classe principale JavaFX
    │       ├── MainController.java         # Logique de l'interface (gestion des clics boutons)
    │       ├── ManualConverterService.java # L'ALGORITHME MANUEL (Regex & Parsing)
    │       └── JacksonConverterService.java# L'implémentation via la librairie Jackson
    │
    └── resources
        └── org.example.xmljsonconverter
            ├── hello-view.fxml             # Le fichier de vue (Design de l'interface)
            └── style.css                   # Feuilles de style pour l'interface




