package scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashSet;

public class NamedIsolatesTax {
	public static void main (String[] args){
		try {
			FileWriter fw_tax = new FileWriter("named_taxonomy.txt");
			BufferedReader br_seq = new BufferedReader(new FileReader("prok_named.seq"));
			BufferedReader br_tax = new BufferedReader(new FileReader("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_taxonomy.txt"));
			HashSet<Integer> gis = new HashSet<Integer>();

			while(br_seq.ready()){

				String str = br_seq.readLine();
				String gi = str.substring(1).trim();
				br_seq.readLine();
				gis.add(Integer.parseInt(gi));
			}
			
			br_seq.close();
			
			
			while(br_tax.ready()){
				String str = br_tax.readLine();
				String[] splitter = str.split("\\s+");
				if (gis.contains(Integer.parseInt(splitter[0]))){
					fw_tax.write(str +"\n");
				}
			}
			br_tax.close();
			fw_tax.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
