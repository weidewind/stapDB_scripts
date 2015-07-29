package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Rewrite {
	
public static void main (String[] args){
	try {
		FileWriter fw = new FileWriter("otu_organisms_correct");
		BufferedReader map = new BufferedReader(new FileReader("src/tests/99_otu_map.txt"));
		BufferedReader orgs = new BufferedReader(new FileReader("src/tests/99_otu_organisms"));
		
		while (orgs.ready()){
			String[] org_line = orgs.readLine().split(";");;
			String[] map_line = map.readLine().split("\\s+");
			fw.write(map_line[1]+ ";");
			
			if (org_line.length > 1){
				fw.write(org_line[1]);
			}
			fw.write("\n");

			
		}
		map.close();
		orgs.close();
		fw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}

}