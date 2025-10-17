# Big-Data-Project---Hadoop-MapReduce
Un projet complet démontrant l'utilisation de Hadoop, HDFS et MapReduce pour le traitement de données à grande échelle.
# PROJET BIG DATA - HADOOP & MAPREDUCE

## 1. Installation du Cluster Hadoop

### Prérequis
- Installer Docker

### Étapes d'installation

1. Télécharger l'image hadoop-spark-cluster :
   docker pull yassern1/hadoop-spark-jupyter:1.0.3

2. Création d'un volume de partage :
   Pour mon cas : C:\Users\pc\Documents\hadoop_project

3. Création des trois conteneurs :

   a. Créer un réseau pour relier les conteneurs :
      docker network create --driver=bridge hadoop

   b. Créer et lancer les trois conteneurs :

      - Conteneur 1 : hadoop-master
        docker run -itd -v C:\Users\pc\Documents\hadoop_project/:/shared_volume --net=hadoop -p 9870:9870 -p 8088:8088 -p 7077:7077 -p 19888:19888 --name hadoop-master --hostname hadoop-master yassern1/hadoop-spark-jupyter:1.0.3

      - Conteneur 2 : hadoop-slave1
        docker run -itd -p 8040:8042 --net=hadoop --name hadoop-slave1 --hostname hadoop-slave1 yassern1/hadoop-spark-jupyter:1.0.3

      - Conteneur 3 : hadoop-slave2
        docker run -itd -p 8041:8042 --net=hadoop --name hadoop-slave2 --hostname hadoop-slave2 yassern1/hadoop-spark-jupyter:1.0.3

4. Configuration initiale :

   a. Accéder au master :
      docker exec -it hadoop-master bash

   b. Démarrer Hadoop et YARN :
      ./start-hadoop.sh

   c. Créer les répertoires HDFS :
      hadoop fs -mkdir -p /user/root
      hdfs dfs -mkdir input

5. Copier des fichiers vers HDFS :

   a. Placer les fichiers dans le dossier hadoop_project sur la machine locale
   b. Copier vers HDFS depuis le conteneur :
      hdfs dfs -put /shared_volume/datasets/fichier.txt input/


## 2. Programmation avec l'API HDFS

### Structure du projet
- Créer un projet Maven nommé "BigData"
- Créer le package : edu.ensias.bigdata.tp1

### Application 1 : HadoopFileStatus
- Créer la classe HadoopFileStatus.java
- Extraire vers le JAR : C:\Users\pc\Documents\hadoop_project\HadoopFileStatus.jar
- Commande d'exécution :
  hadoop jar /shared_volume/HadoopFileStatus.jar edu.ensias.bigdata.tp1.HadoopFileStatus /user/root/input purchases.txt nouveau_nom.txt

### Application 2 : HDFSInfo
- Créer la classe HDFSInfo.java
- Extraire le JAR vers shared_volume
- Commande d'exécution :
  hadoop jar /shared_volume/HDFSInfo.jar edu.ensias.bigdata.tp1.HDFSInfo /user/root/input/new.txt

### Application 3 : ReadHDFS
- Créer la classe ReadHDFS.java
- Extraire le JAR vers shared_volume
- Commande d'exécution :
  hadoop jar /shared_volume/ReadHDFS.jar edu.ensias.bigdata.tp1.ReadHDFS /user/root/input/purchases.txt

### Application 4 : HDFSWrite
- Créer la classe HDFSWrite.java
- Extraire le JAR vers shared_volume
- Commande d'exécution :
  hadoop jar /shared_volume/HDFSWrite.jar edu.ensias.bigdata.tp1.HDFSWrite /user/root/output/message.txt "dadiiii"


## 3. Programmation avec l'API MapReduce

### Objectif
Simuler l'exemple WordCount pour compter le nombre d'occurrences de chaque mot dans un fichier texte.

### Architecture du traitement
- Phase de Mapping : Découpe le texte en mots et génère des paires (mot, 1)
- Phase de Reducing : Regroupe par mot et fait la somme des occurrences

### Implémentation

1. Structure du projet :
   - Créer le package : edu.ensias.hadoop.mapreducelab
   - Emplacement : src/main/java/edu/ensias/hadoop/

2. Classes à créer :

   a. TokenizerMapper (Mapper)
      - Prend chaque ligne de texte
      - Tokenise en mots
      - Émet des paires (mot, 1)

   b. IntSumReducer (Reducer)
      - Agrège les valeurs pour chaque clé
      - Calcule la somme des occurrences
      - Émet des paires (mot, total)

   c. WordCount (Classe principale)
      - Configure et lance le job MapReduce
      - Définit les classes Mapper et Reducer
      - Spécifie les chemins d'entrée/sortie

3. Déploiement et exécution :

   a. Exporter le projet en JAR vers shared_volume local
   b. Dans le bash de Hadoop master, exécuter :
      hadoop jar /shared_volume/WordCount.jar input/new.txt output2/resultat

### Notes importantes
- Le répertoire de sortie ne doit pas exister avant l'exécution
- Utiliser : hadoop fs -rm -r output2/resultat pour supprimer l'ancien résultat
- Vérifier les résultats avec : hadoop fs -cat output2/resultat/part-r-00000


## 4. MapReduce avec Python

### Objectif
Implémenter l'exemple wordcount avec MapReduce en Python et l'utilitaire Hadoop Streaming.

### Étapes d'exécution

1. Préparation des fichiers :
   - Créer deux fichiers Python : mapper.py et reducer.py
   - Créer un fichier alice.txt (contenant le texte à analyser)
   - Placer ces fichiers dans le dossier /shared_volume/python/

2. Contenu des fichiers Python :

mapper.py
reducer.py

3. Exécution dans le bash du master :

# Accéder au conteneur et vérifier les fichiers
cd /shared_volume/python
ls -la mapper.py reducer.py alice.txt

# Trouver le JAR Hadoop Streaming
find / -name "hadoop-streaming*.jar" 2>/dev/null
# Résultat attendu : /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-3.2.0.jar

# Tester les scripts Python localement
cat alice.txt | python3 mapper.py
cat alice.txt | python3 mapper.py | sort | python3 reducer.py

# Donner les permissions d'exécution
chmod +x mapper.py reducer.py

# Copier le fichier vers HDFS
hadoop fs -put alice.txt input/

# Exécuter le job MapReduce avec Hadoop Streaming
hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-3.2.0.jar \
    -files /shared_volume/python/mapper.py,/shared_volume/python/reducer.py \
    -mapper "python3 mapper.py" \
    -reducer "python3 reducer.py" \
    -input input/alice.txt \
    -output output_python_wordcount

4. Vérification des résultats :

# Lister les fichiers de sortie
hadoop fs -ls output_python_wordcount

# Afficher le résultat complet
hadoop fs -cat output_python_wordcount/part-00000

# Afficher les premiers résultats
hadoop fs -cat output_python_wordcount/part-00000 | head -20









========================================
TP APACHE KAFKA - GUIDE SIMPLE
========================================

OBJECTIFS DU TP :
- Installation d'Apache Kafka
- Première utilisation d'Apache Kafka
- Création d'une application Kafka en Java
- Communication Producer/Consumer en temps réel

========================================
CONCEPTS FONDAMENTAUX
========================================

Apache Kafka :
- Système de messagerie distribué basé sur le pattern Publish/Subscribe
- Permet de publier et s'abonner à des flux d'événements
- Stocke les événements de manière durable
- Traite les flux d'événements en temps réel

Zookeeper :
- Service centralisé pour la coordination
- Gère la synchronisation entre les nœuds Kafka

========================================
DEMARRAGE DE L'ENVIRONNEMENT
========================================

1. Démarrer les conteneurs :
   docker start hadoop-master hadoop-slave1 hadoop-slave2

2. Accéder au master :
   docker exec -it hadoop-master bash

3. Démarrer Hadoop :
   ./start-hadoop.sh

4. Démarrer Kafka et Zookeeper :
   ./start-kafka-zookeeper.sh

5. Vérifier le démarrage :
   jps

========================================
CONFIGURATION KAFKA
========================================

Créer un topic :
kafka-topics.sh --create --bootstrap-server localhost:9092 \
    --replication-factor 1 --partitions 1 --topic idf

Lister les topics :
kafka-topics.sh --list --bootstrap-server localhost:9092

Afficher les détails du topic :
kafka-topics.sh --describe --topic idf --bootstrap-server localhost:9092

========================================
TEST AVEC KAFKA CLI
========================================

Terminal 1 - Producer (Console) :
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic idf

Terminal 2 - Consumer (Console) :
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic idf --from-beginning

========================================
APPLICATION JAVA KAFKA
========================================

Structure du projet :
kafka_lab/
├── pom.xml
└── src/main/java/edu/ensias/kafka/
    ├── EventProducer.java
    └── EventConsumer.java

Compilation :
mvn clean package -DskipTests

JARs générés :
- target/producer.jar
- target/consumer.jar

Copier les JARs :
cp target/producer.jar /shared_volume/kafka/
cp target/consumer.jar /shared_volume/kafka/

========================================
EXECUTION DE L'APPLICATION
========================================

Terminal 1 - Lancer le Consumer :
java -jar /shared_volume/kafka/consumer.jar idf

Terminal 2 - Lancer le Producer :
java -jar /shared_volume/kafka/producer.jar idf

Taper des messages dans le Producer :
Message > Bonjour Kafka
Message > Ceci est un test
Message > exit

Résultat dans le Consumer :
>>> MESSAGE REÇU <<<
Partition: 0 | Offset: 0
Clé: 1
Message: Bonjour Kafka
-----------------------------------

========================================
FLUX DE COMMUNICATION
========================================

Producer → Topic (idf) → Consumer

Producer :
- Interface interactive
- Accepte les messages saisis par l'utilisateur
- Envoie les messages à Kafka
- Arrêt avec "exit"

Consumer :
- Écoute le topic en temps réel
- Affiche les messages reçus
- Arrêt avec Ctrl+C

========================================
COMMANDES UTILES
========================================

jps
- Affiche les processus Java en cours

netstat -tulpn | grep 9092
- Vérifie que Kafka écoute sur le port 9092

mvn clean package -DskipTests
- Compile le projet Maven

========================================
DEPANNAGE
========================================

Erreur "Connection refused on port 9092" :
→ Kafka n'est pas démarré. Lancez ./start-kafka-zookeeper.sh

Erreur "Topic not found" :
→ Créez le topic avec kafka-topics.sh --create

Pas de messages reçus :
→ Vérifiez que le Producer a envoyé des messages

Erreur de compilation Maven :
→ Vérifiez la syntaxe Java et les dépendances



PARTIE 2 : 


Description - Kafka Connect (Sink et Fichiers)
📌 Qu'est-ce que Kafka Connect ?
Kafka Connect est un framework qui permet de faire circuler les données entre Kafka et d'autres systèmes (fichiers, bases de données, APIs, etc.) sans écrire de code.
Il utilise deux types de connecteurs :

Source Connector : Lit les données d'une source externe et les envoie à Kafka
Sink Connector : Reçoit les données de Kafka et les écrit vers une destination externe


========================================
KAFKA CONNECT - GUIDE COMPLET
========================================

FICHIERS DE CONFIGURATION EXISTANTS :

connect-file-source.properties :
  - file=test.txt
  - topic=connect-test

connect-file-sink.properties :
  - file=test.sink.txt
  - topics=connect-test

========================================
ÉTAPE 1 : AJOUTER LE PLUGIN PATH
========================================

Commande :
echo "plugin.path=/usr/local/kafka/libs/" >> $KAFKA_HOME/config/connect-standalone.properties

Vérifier :
tail $KAFKA_HOME/config/connect-standalone.properties

========================================
ÉTAPE 2 : CRÉER LE TOPIC KAFKA
========================================

Commande :
kafka-topics.sh --create --bootstrap-server localhost:9092 \
    --topic connect-test --partitions 1 --replication-factor 1

Vérifier :
kafka-topics.sh --list --bootstrap-server localhost:9092

========================================
ÉTAPE 3 : CRÉER LE FICHIER SOURCE
========================================

Aller dans le dossier Kafka :
cd $KAFKA_HOME

Créer le fichier test.txt avec des données :
echo "Bonjour Kafka" > test.txt
echo "Bienvenue dans le monde du streaming" >> test.txt

Vérifier :
cat test.txt

Résultat attendu :
Bonjour Kafka
Bienvenue dans le monde du streaming

========================================
ÉTAPE 4 : DÉMARRER KAFKA CONNECT
========================================

Commande (dans le dossier $KAFKA_HOME) :
cd $KAFKA_HOME

./bin/connect-standalone.sh \
    config/connect-standalone.properties \
    config/connect-file-source.properties \
    config/connect-file-sink.properties

Vous devriez voir les logs :
[INFO] Starting FileStreamSource with tasks.max=1
[INFO] Starting FileStreamSink with tasks.max=1

Ne fermez pas ce terminal !

========================================
ÉTAPE 5 : VÉRIFIER LES RÉSULTATS
========================================

Ouvrir un NOUVEAU terminal et exécuter :

cd $KAFKA_HOME
cat test.sink.txt

Résultat attendu :
Bonjour Kafka
Bienvenue dans le monde du streaming

========================================
ÉTAPE 6 : TESTER LE PIPELINE EN TEMPS RÉEL
========================================

Dans le nouveau terminal, ajouter une ligne au fichier source :
echo "Exercice Kafka Connect simple" >> test.txt

Vérifier immédiatement que la ligne s'est synchronisée :
cat test.sink.txt

Résultat attendu :
Bonjour Kafka
Bienvenue dans le monde du streaming
Exercice Kafka Connect simple

========================================
EMPLACEMENTS DES FICHIERS
========================================

Tous les fichiers se trouvent dans $KAFKA_HOME :

$KAFKA_HOME/
├── test.txt                    ← Fichier source (entrée)
├── test.sink.txt               ← Fichier destination (sortie)
├── config/
│   ├── connect-standalone.properties
│   ├── connect-file-source.properties
│   └── connect-file-sink.properties
└── bin/
    └── connect-standalone.sh

========================================
FLUX DE COMMUNICATION
========================================

test.txt (source)
   ↓
FileStreamSource lit ligne par ligne
   ↓
Envoie au topic : connect-test
   ↓
FileStreamSink reçoit du topic
   ↓
test.sink.txt (destination)

========





========================================
KAFKA STREAMS - WORD COUNT APPLICATION
========================================

OBJECTIF :
Développer une application Kafka Streams qui :
1. Lit des phrases depuis un topic Kafka (input-topic)
2. Compte la fréquence des mots
3. Envoie les résultats vers un autre topic (output-topic)

========================================
CONCEPT DE BASE
========================================

Kafka Streams est un framework pour traiter les flux de données en temps réel.

Flux de traitement :
Input Topic (phrases brutes)
    ↓
WordCountApp (traitement)
    ↓
Output Topic (mots + compteurs)

Exemple :
Entrée : "Bonjour le monde, Bonjour à tous"
Sortie : 
  bonjour → 2
  le → 1
  monde → 1
  à → 1
  tous → 1

========================================
ÉTAPE 1 : METTRE À JOUR LE POM.XML
========================================

VOIR LE PROJET
========================================
ÉTAPE 2 : CRÉER LA CLASSE WORDCOUNTAPP
========================================

========================================
ÉTAPE 3 : COMPILER LE PROJET
========================================

Commande :
cd kafka_lab
mvn clean package -DskipTests

Résultat :
Trois JARs sont générés dans target/ :
- producer.jar
- consumer.jar
- wordcount-app.jar ← C'est celui-ci qu'on va utiliser

Vérifier :
ls -lh target/*.jar

========================================
ÉTAPE 4 : COPIER LE JAR
========================================

Copier sur Windows :
copy target\wordcount-app.jar C:\Users\pc\Documents\hadoop_project\kafka\

Ou sur le serveur Hadoop :
cp target/wordcount-app.jar /shared_volume/kafka/

========================================
ÉTAPE 5 : CRÉER LES TOPICS KAFKA
========================================

Topic d'entrée (input-topic) :

kafka-topics.sh --create --bootstrap-server localhost:9092 \
    --topic input-topic \
    --partitions 1 \
    --replication-factor 1

Topic de sortie (output-topic) :

kafka-topics.sh --create --bootstrap-server localhost:9092 \
    --topic output-topic \
    --partitions 1 \
    --replication-factor 1

Vérifier les topics :

kafka-topics.sh --list --bootstrap-server localhost:9092

Résultat attendu :
input-topic
output-topic

========================================
ÉTAPE 6 : DÉMARRER L'APPLICATION
========================================

Commande :
java -jar /shared_volume/kafka/wordcount-app.jar input-topic output-topic

Résultat attendu :
========================================
Kafka Streams Word Count Application
========================================
Input Topic: input-topic
Output Topic: output-topic
========================================
Application démarrée. En attente de messages...

IMPORTANT : Ne fermez pas ce terminal !
L'application est en cours d'exécution et attend des messages.

========================================
ÉTAPE 7 : ENVOYER DES DONNÉES (PRODUCER)
========================================

Ouvrir un NOUVEAU terminal et lancer le producer :

kafka-console-producer.sh --broker-list localhost:9092 --topic input-topic

Taper des phrases (une par ligne) :

Bonjour le monde
Kafka est formidable
Streaming de données en temps réel
Bonjour à tous

Appuyer sur Ctrl+C pour arrêter le producer

========================================
ÉTAPE 8 : LIRE LES RÉSULTATS (CONSUMER)
========================================

Ouvrir un AUTRE terminal et lancer le consumer :

kafka-console-consumer.sh --topic output-topic \
    --from-beginning \
    --bootstrap-server localhost:9092 \
    --property print.key=true

Résultat attendu :
bonjour	Mot: , Nombre: 2
le	Mot: , Nombre: 1
monde	Mot: , Nombre: 1
kafka	Mot: , Nombre: 1
est	Mot: , Nombre: 1
formidable	Mot: , Nombre: 1
streaming	Mot: , Nombre: 1
de	Mot: , Nombre: 1
données	Mot: , Nombre: 1
en	Mot: , Nombre: 1
temps	Mot: , Nombre: 1
réel	Mot: , Nombre: 1
à	Mot: , Nombre: 1
tous	Mot: , Nombre: 1

========================================
ARCHITECTURE KAFKA STREAMS
========================================

Topologie créée dans l'application :

Source (input-topic)
    ↓
flatMapValues (diviser chaque valeur en plusieurs)
    ↓
filter (supprimer les valeurs vides)
    ↓
groupBy (regrouper par mot)
    ↓
count (compter les occurrences)
    ↓
toStream (convertir la table en stream)
    ↓
mapValues (transformer le compteur en string)
    ↓
Sink (output-topic)


