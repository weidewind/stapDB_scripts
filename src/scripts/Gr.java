package scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Gr {

	public static void convert() throws IOException{
		
		HashMap<Integer, String> hash = new HashMap<Integer, String>();
		
		File dir = new File(".");
		
		File [] files = dir.listFiles(new FilenameFilter(){

			public boolean accept(File dir, String name) {
		            return name.startsWith("PROK."); 
			}

	        });
		
		
		List<File> list = Arrays.asList(files);
		
		for(File f: list){
			BufferedReader br = new BufferedReader(new FileReader(f));
			while (br.ready()){
				String str = br.readLine();
				String[] splitter = str.split("[K_]");
				hash.put(Integer.parseInt(splitter[1]), splitter[3].trim().substring(1));
			}
		}
		
		
		
		
	}
	
}
