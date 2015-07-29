package scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fetcher {

	public static String get(String urlString) {
		StringBuilder result = new StringBuilder();
		String line;
		try {
			URL url = new URL(urlString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			while ((line = reader.readLine()) != null) result.append(line);
			reader.close();
		} catch (Exception e) {
			System.out.println("Something has gone wrong, sorry");
		}

		return result.toString();
	}

	public static void parse(String in, String out){


		try {
			BufferedReader br = new BufferedReader(new FileReader(in));
			PrintWriter pw = new PrintWriter(new FileWriter(out));

			Pattern p = Pattern.compile("[0-9]*");


			while(br.ready()){
				String str = br.readLine();
				String[] arr1 = str.split("\\s+");
				String pre_acc = arr1[arr1.length-2];
				String acc;

				if (pre_acc.indexOf('.') != -1) {
					String[] pre_acc_arr = pre_acc.split("\\.");
					Matcher m = p.matcher(pre_acc_arr[pre_acc_arr.length-1]);
					if (m.matches()){
						acc = pre_acc_arr[pre_acc_arr.length-2] + "." +  pre_acc_arr[pre_acc_arr.length-1];
					}
					else acc = pre_acc_arr[pre_acc_arr.length-1];
				}
				else acc = pre_acc;
				
				
				String pre_gi = arr1[arr1.length-1];
				String gi = pre_gi.split("[:<]")[1];
				
				pw.println(acc + "\t" + gi);
				
			
			}
			br.close();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args){
		parse("lost_and_found.txt", "lost_and_found_acc_to_gi.txt");
	}
}