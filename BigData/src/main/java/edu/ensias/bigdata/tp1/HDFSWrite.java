package edu.ensias.bigdata.tp1;

import java.io.IOException;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;

public class HDFSWrite {
    
    public static void main(String[] args) throws IOException {
        // Vérifier qu'on a bien 2 paramètres
        if (args.length != 2) {
            System.err.println("Usage: hadoop jar HDFSWrite.jar <chemin_fichier> <texte_a_ecrire>");
            System.err.println("Exemple: hadoop jar HDFSWrite.jar /user/root/output/message.txt \"Bonjour tout le monde !\"");
            System.exit(1);
        }
        
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        
        // Utiliser le chemin passé en argument
        Path nomcomplet = new Path(args[0]);
        
        if (!fs.exists(nomcomplet)) {
            FSDataOutputStream outStream = fs.create(nomcomplet);
            outStream.writeUTF("Bonjour tout le monde !\n");
            outStream.writeUTF(args[1]);
            outStream.writeUTF("\n");
            outStream.close();
            System.out.println("Fichier cree avec succes: " + args[0]);
            System.out.println("Contenu ecrit: " + args[1]);
        } else {
            System.err.println("Le fichier " + args[0] + " existe déjà!");
            System.err.println("Veuillez choisir un autre nom ou supprimer le fichier existant.");
        }
        
        fs.close();
    }
}