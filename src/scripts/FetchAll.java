package scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FetchAll {
	public static void main (String[] args) {

		BufferedReader br;
		PrintWriter out;
		try {
		 br = new BufferedReader(new FileReader("C:/Users/weidewind/Documents/CMD/DBs/acc_to_gi/out2_obsolete_version.txt"));
			
			out = new PrintWriter(new FileWriter("lost_gi_obsolete_version.txt"));
			while(br.ready()){
				String acc = br.readLine();
				String update = Fetcher.get("http://www.ncbi.nlm.nih.gov/nuccore/" + acc + "?report=docsum&format=text");
				String[] report = update.split("\\n");
				out.println(report[report.length-1]);
			}
			out.close();
			br.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

	}
}
