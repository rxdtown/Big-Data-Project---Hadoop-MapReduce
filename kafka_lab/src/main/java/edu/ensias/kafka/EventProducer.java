package edu.ensias.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
import java.util.Scanner;

public class EventProducer {
    
    public static void main(String[] args) throws Exception {
        // Vérifier que le topic est fourni comme argument
        if(args.length == 0) {
            System.out.println("Entrer le nom du topic");
            return;
        }
        
        String topicName = args[0].toString(); // lire le topicName fourni comme paramètre
        
        Properties props = new Properties(); // accéder aux configurations du producteur
        props.put("bootstrap.servers", "localhost:9092"); // spécifier le serveur kafka
        
        // Définir un sérialiseur pour les requêtes du producteur
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        
        // Si la requête échoue, le producteur peut rééssayer automatiquement
        props.put("retries", 0);
        
        System.out.println("========================================");
        System.out.println("Producteur Kafka - Topic: " + topicName);
        System.out.println("========================================");
        System.out.println("Tapez vos messages (tapez 'exit' pour quitter)");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        int messageCount = 0;
        
        try {
            while(true) {
                System.out.print("Message > ");
                String message = scanner.nextLine();
                
                // Quitter si l'utilisateur tape "exit"
                if(message.equalsIgnoreCase("exit")) {
                    System.out.println("Fermeture du producteur...");
                    break;
                }
                
                // Ignorer les messages vides
                if(message.trim().isEmpty()) {
                    System.out.println("Message vide, veuillez réessayer.");
                    continue;
                }
                
                messageCount++;
                String key = String.valueOf(messageCount);
                
                // Envoyer le message
                producer.send(new ProducerRecord<String, String>(topicName, key, message));
                System.out.println("✓ Message envoyé avec succès: " + message);
                System.out.println();
            }
        } finally {
            System.out.println(messageCount + " message(s) envoyé(s)");
            producer.close();
        }
    }
}