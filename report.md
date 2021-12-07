# Projet TinyGram - Web, Cloud & Datastores
Malo GRALL, Mathis ROCHER, Guillaume POIGNANT

## Applications
- [Application web](http://univ-cloud.ew.r.appspot.com/)
- REST API : https://tinycrash.ew.r.appspot.com/_ah/api/instaCrash/v1/
- [Github](https://github.com/grallm/m1-google-cloud/)
  - [README](https://github.com/grallm/m1-google-cloud/blob/main/README.md)

### Technologies utilisées
#### Frontend
- NextJS
- Google App Engine (host)

#### Backend
- Google Endpoints Framework
- Google Datastore
- Google Cloud Storage


## Kinds

## Benchmark - Performances


## Commentaires
### Frontend
Cherchant à fournir la meilleure expérience pour nos utilisateurs, nous avons décidé d'utiliser le framework `NextJS`, basé sur `ReactJS`.

Ce choix fut fait par notre connaissance de React et des outils associés, mais aussi pour les performances que propose `NextJS`.


## Problemes
### Google Endpoint / App Engine
### Gestion des images
### Like scalables 
### Timeline
Afin d'obtenir une timeline efficace, nous avons du iterer plusieurs fois. 
- Dans un premier temps nous avions une premiere requete qui recuperais les entites follow liees au user puis les ajoutais 
a une liste et enfin nous recuperions tous les posts des utilisateurs dans la liste de followers.
Mais le ```Query.FilterOperator.IN``` est limite a 30 sous requetes donc nous ne recuperions que 30 posts. 

- Dans un second temps nous nous sommes debarasses de l'entite follow pour n'utiliser que des listes et des objets java 
accompagne d'un comparator pour les trier. Mais cette solution a tres vite vu ses limites puisque la manipulation de liste 
d'objets en java est tres couteuse en temps de plus pour le front ne plus utiliser d'entites Post et seulement des listes d'objets java nous penalisais.

- Enfin nous avons fait un mix des deux. Notre user a une liste de followings que l'on va recuperer pour obtenir les posts
vieux de maximum 1 jour afin d'avoir une timeline "intelligente" enfin nous recuperons la date des posts que nous trions avec notre
comparator afin d'avoir le post le plus recent en premier. En plus de ca a la fin des posts de notre timeline nous affichons une 
serie de posts afin que l'utiisateur ai quelque chose a voir. 

## Solutions


## Ameliorations
