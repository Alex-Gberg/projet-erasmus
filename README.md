# Projet de programmation
Veuillez trouver ci-dessous les instructions pour lancer et jouer le jeu Qui-est-ce? réalisé par le groupe Erasmus :
* Alexander Goldberg
* Ali Mohammed Abdul
* Eugène Ton

## Table des matières
* [Conditions](#conditions)
* [Comment exécuter le programme](#comment-exécuter-le-programme)
* [Comment jouer](#comment-jouer)
* [Comment générer un jeu](#comment-générer-un-jeu)

## Conditions
- Java 11

## Comment exécuter le programme
1. Téléchargez le dossier zip
2. Extrayez le dossier zip
3. Ouvrez le dossier dans un terminal
4. Utilisez la commande `mvn clean javafx:run`

## Comment jouer
- Choisissez avec quel type de personnages vous voulez jouer
- Appuyez sur le bouton `Nouveau jeu`
- Posez votre question en utilisant les options en bas de la fenêtre
- Appuyez sur `Interroger!` pour poser votre question
- Le jeu est en mode `Automatique` par défaut, ainsi les personnages non conformant à la réponse seront éliminer automatiquement
- L'autre mode est `Manuel`. Dans ce mode vous recevrez les réponses à vos interrogations, mais vous serez obliger d'éliminer les personnages vous-mêmes en cliquant sur les images
- Vous pouvez changer le mode depuis le bouton `Options` en haut de la fenêtre
- Le menu `Options` donne aussi accès au bouton `Sauvegarder la partie`
- Vous ne pouvez sauvegarder qu'une partie à la fois, donc quand vous appuyez sur le bouton vous perdrez la sauvegarde précédente
- Pour continuer le jeu que vous avez sauvegardé, utiliser le bouton `Continuer la partie` depuis le menu principal

## Comment générer un jeu
- Sélectionnez un ensemble d'images
- Vous pouvez sélectionner l'un des jeux d'images prédéfinis ou importer le vôtre en appuyant sur le bouton `Importer des images` (Les images ne seront pas redimensionnées, vous devez donc vous assurer que vous dimensionnez correctement vos images)
- Appuyez sur le bouton `Générer un jeu`
- Pour modifier la façon dont les images sont affichées, utilisez la section `Ajuster la taille de la grille`
- Entrez le nombre de lignes et de colonnes et si votre entrée est valide, les images affichées changeront
- Pour ajouter de nouveaux attributs, entrez-les dans l'encadré, puis appuyez sur `Ajouter`
- Pour supprimer un attribut, sélectionnez l'attribut souhaité dans la liste puis appuyez sur `Supprimer`
- Pour continuer aux entrées de valeur d'attribut, appuyez sur `Valider et continuer`
- Vous allez maintenant parcourir chaque image et être invité à entrer la valeur de chaque attribut
- Après avoir entré toutes les valeurs et si les valeurs données sont valides, vous serez invité à nommer et enregistrer le fichier
- Vous aurez la possibilité de jouer immédiatement à votre nouveau jeu ou vous pourrez y accéder depuis le menu principal où il apparaîtra dans la liste `Choisir le type de caractères`


