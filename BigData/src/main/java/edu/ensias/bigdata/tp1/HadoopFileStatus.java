package edu.ensias.bigdata.tp1;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

public class HadoopFileStatus {
    public static void main(String[] args) {
        // Vérifier qu'on a bien 3 paramètres
        if (args.length != 3) {
            System.err.println("Usage: hadoop jar HadoopFileStatus.jar <chemin_fichier> <nom_fichier> <nouveau_nom>");
            System.err.println("Exemple: hadoop jar HadoopFileStatus.jar ./input purchases.txt achats.txt");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        //conf.set("fs.defaultFS","hdfs://localhost:9000");
        FileSystem fs;
        try {
            fs = FileSystem.get(conf);

            // Utiliser les paramètres passés
            String chemin = args[0];
            String nomFichier = args[1];
            String nouveauNom = args[2];

            Path nomcomplet = new Path(chemin, nomFichier);
            FileStatus infos = fs.getFileStatus(nomcomplet);

            System.out.println(Long.toString(infos.getLen()) + " octets");
            System.out.println("File Name: " + infos.getPath().getName());
            System.out.println("File Size: " + infos.getLen());
            System.out.println("File Replication: " + infos.getReplication());
            System.out.println("File Block Size: " + infos.getBlockSize());

            // Renommer avec le nouveau nom passé en paramètre
            // Renommer avec vérification
            boolean success = fs.rename(nomcomplet, new Path(chemin, nouveauNom));
            if (success) {
                System.out.println("✓ Fichier renomme de " + nomFichier + " vers " + nouveauNom);
            } else {
                System.out.println("✗ Échec du renommage !");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

