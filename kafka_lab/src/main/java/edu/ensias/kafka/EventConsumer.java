package edu.ensias.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Arrays;
import java.util.Properties;

public class EventConsumer {
    
    public static void main(String[] args) throws Exception {
        // Vérifier que le topic est fourni comme argument
        if(args.length == 0) {
            System.out.println("Entrer le nom du topic");
            return;
        }
        
        String topicName = args[0].toString(); // lire le topicName fourni comme paramètre
        
        Properties props = new Properties(); // accéder aux configurations du consommateur
        props.put("bootstrap.servers", "localhost:9092"); // spécifier le serveur kafka
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-1");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Définir un désérialiseur pour les requêtes du consommateur
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        
        // S'abonner au topic
        consumer.subscribe(Arrays.asList(topicName));
        
        System.out.println("========================================");
        System.out.println("Consommateur Kafka - Topic: " + topicName);
        System.out.println("========================================");
        System.out.println("En attente de messages... (Ctrl+C pour arrêter)");
        System.out.println();
        
        final int[] messageCount = {0};
        
        try {
            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                
                if(records.count() > 0) {
                    records.forEach(record -> {
                        messageCount[0]++;
                        System.out.println(">>> MESSAGE REÇU <<<");
                        System.out.println("Partition: " + record.partition() + 
                                         " | Offset: " + record.offset());
                        System.out.println("Clé: " + record.key());
                        System.out.println("Message: " + record.value());
                        System.out.println("-----------------------------------");
                    });
                }
            }
        } catch(Exception e) {
            System.out.println("\nConsommateur arrêté.");
            System.out.println("Total de messages reçus: " + messageCount[0]);
        } finally {
            consumer.close();
        }
    }
}