package edu.ensias.bigdata.tp1;

import java.io.IOException;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;

public class HDFSInfo {
    
    public static void main(String[] args) throws IOException {
        // Vérifier qu'on a bien 1 paramètre
        if (args.length != 1) {
            System.err.println("Usage: hadoop jar HDFSInfo.jar <chemin_fichier>");
            System.err.println("Exemple: hadoop jar HDFSInfo.jar /user/root/input/achats.txt");
            System.exit(1);
        }
        
        // Obtenir un objet qui représente HDFS
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        
        // Nom du fichier à lire (passé en argument)
        Path nomcomplet = new Path(args[0]);
        
        if (! fs.exists(nomcomplet)) {
            System.out.println("Le fichier n'existe pas");
        } else {
            // Afficher taille du fichier
            FileStatus infos = fs.getFileStatus(nomcomplet);
            System.out.println(Long.toString(infos.getLen()) + " octets");
            
            // Liste des blocs du fichier
            BlockLocation[] blocs = fs.getFileBlockLocations(infos, 0, infos.getLen());
            
            System.out.println("Nombre de blocs: " + blocs.length);
            
            for (BlockLocation bloc : blocs) {
                System.out.println("\n--- Bloc ---");
                System.out.println("Offset: " + bloc.getOffset());
                System.out.println("Longueur: " + bloc.getLength());
                
                String[] hosts = bloc.getHosts();
                System.out.println("Hôtes: ");
                for (String host : hosts) {
                    System.out.println("  - " + host);
                }
            }
        }
        
        // Fermer HDFS
        fs.close();
    }
}