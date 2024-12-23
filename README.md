Configuration du mot de passe d'application Gmail Pour service notification
-----------------------------------------------

1. Activer l'authentification à 2 facteurs sur votre compte Google
   - Aller dans Paramètres du compte Google > Sécurité
   - Activer "Validation en 2 étapes"

2. Créer le mot de passe d'application
   - Aller dans Paramètres du compte > Sécurité
   - Dans "Validation en 2 étapes", sélectionner "Mots de passe des applications"
   - Sélectionner "Application" et "Autre (nom personnalisé)"
   - Donner un nom (ex: "Service Notifications")
   - Cliquer "Générer"

3. Utiliser le mot de passe généré
   - Copier le mot de passe de 16 caractères
   - Utiliser ce mot de passe dans votre application dans  le fichier application.yml : 
   -chemin : notification-service\src\main\resources\application.yml
    -chercher ces lignes et remplacer username avec votre gmail et password avec le mot de passe généré  
    - supprimer l'espace entre les caractere par exemple: password généré: kqps wxnm dfrt hyjk => kqpswxnmdfrthyjk
#########Champs a chercher dans application.yml##########
    mail:
    host: smtp.gmail.com
    port: 587
    username: email@gmail.com (ex: username: yourgmail@gmail.com ) 
    password: app_password  (ex: password: kqpswxnmdfrthyjk )
######################
