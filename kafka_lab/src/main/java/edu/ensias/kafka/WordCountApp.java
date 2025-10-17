package edu.ensias.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import java.util.*;

public class WordCountApp {
    
    public static void main(String[] args) {
        
        // Vérifier que les topics sont fournis comme arguments
        if(args.length < 2) {
            System.out.println("Usage: java WordCountApp <input-topic> <output-topic>");
            System.out.println("Example: java WordCountApp input-topic output-topic");
            return;
        }
        
        String inputTopic = args[0];
        String outputTopic = args[1];
        
        System.out.println("========================================");
        System.out.println("Kafka Streams Word Count Application");
        System.out.println("========================================");
        System.out.println("Input Topic: " + inputTopic);
        System.out.println("Output Topic: " + outputTopic);
        System.out.println("========================================");
        
        // Configuration de Kafka Streams
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        // Créer le builder pour construire la topologie
        StreamsBuilder builder = new StreamsBuilder();
        
        // Lire les données du topic d'entrée
        KStream<String, String> textLines = builder.stream(inputTopic, 
            Consumed.with(Serdes.String(), Serdes.String()));
        
        // ==================== LOGIQUE DE WORD COUNT ====================
        
        textLines
            // Convertir en minuscules et diviser en mots
            .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
            
            // Filtrer les mots vides
            .filter((key, word) -> word.length() > 0)
            
            // Grouper par mot
            .groupBy((key, word) -> word)
            
            // Compter les occurrences de chaque mot
            .count(Materialized.as("word-counts-store"))
            
            // Convertir en stream
            .toStream()
            
            // Convertir le compteur (long) en string
            .mapValues(count -> "Mot: " + ", Nombre: " + count)
            
            // Envoyer les résultats au topic de sortie
            .to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        
        // ==================== FIN DE LA LOGIQUE ====================
        
        // Créer l'application Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        
        // Ajouter un hook pour arrêter proprement l'application
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nArrêt de l'application...");
            streams.close();
            System.out.println("Application fermée avec succès.");
        }));
        
        // Démarrer l'application
        System.out.println("Application démarrée. En attente de messages...");
        streams.start();
    }
}