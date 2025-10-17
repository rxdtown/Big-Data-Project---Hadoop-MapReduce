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

