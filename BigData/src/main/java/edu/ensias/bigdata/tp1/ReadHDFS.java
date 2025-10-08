package edu.ensias.bigdata.tp1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

public class ReadHDFS {
    
    public static void main(String[] args) throws IOException {
        // Vérifier qu'on a bien 1 paramètre
        if (args.length != 1) {
            System.err.println("Usage: hadoop jar ReadHDFS.jar <chemin_fichier>");
            System.err.println("Exemple: hadoop jar ReadHDFS.jar /user/root/input/purchases.txt");
            System.exit(1);
        }
        
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        
        // Utiliser le chemin passé en argument
        Path nomcomplet = new Path(args[0]);
        
        // Vérifier si le fichier existe
        if (!fs.exists(nomcomplet)) {
            System.err.println("Erreur: Le fichier " + args[0] + " n'existe pas!");
            fs.close();
            System.exit(1);
        }
        
        FSDataInputStream inStream = fs.open(nomcomplet);
        InputStreamReader isr = new InputStreamReader(inStream);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
        
        System.out.println(line);
        inStream.close();
        fs.close();
    }
}