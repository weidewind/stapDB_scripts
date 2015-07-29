package scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {

	public static void parseTaxonomy(String input, String gi_taxid, String name, String nodes, boolean use_db) throws IOException{
		BufferedReader br = null;
		FileWriter fw_gi = null;
		FileWriter fw_name = null;
		FileWriter fw_nodes = null;


		try {
			br = new BufferedReader(new FileReader(input));

			fw_gi = new FileWriter(gi_taxid);
			fw_name = new FileWriter(name);
			fw_nodes = new FileWriter(nodes);

			HashSet<Integer> taxids = new HashSet<Integer>();
			HashMap <Integer, Integer> gitax = new HashMap <Integer, Integer>();
			String line;
			int taxid_counter = 1; 
			Node root = new Node(taxid_counter); // root_of_life
			taxids.add(1);
			fw_name.write(1 + "\t" + "root of life" + "\n");
			while ((line = br.readLine()) != null) {
				String[] split = line.split("[\t;)]");
				if (split.length != 8) {
					System.out.println("Line '" + line + "' is corrupt");
				}
				else {
					int gg_id = Integer.parseInt(split[0].trim());
					Node temp = root;


					for (int i = 1; i < 8; i++){


						if(split[i].trim().length() > 4){

							if (i >1){

								int taxid = temp.getId();
								if (!taxids.contains(taxid)){
									taxids.add(taxid);
									String tax_name = temp.getName();
									int parent_taxid = temp.getParent().getId();



									int rank = rankForChar(tax_name.charAt(0), use_db);

									fw_name.write(taxid + "\t" + tax_name.substring(3) + "\n");
									fw_nodes.write(taxid + "\t" + parent_taxid + "\t" + rank + "\n");
								}

							}

							if (!temp.hasChild(split[i].trim())){
								temp.addChild(new Node(split[i].trim(), ++taxid_counter, temp));
							}
							temp = temp.getChild(split[i].trim());



						}
						else break;
					}


					int taxid = temp.getId();
					if (!taxids.contains(taxid)){
						taxids.add(taxid);
						String tax_name = temp.getName();
						int parent_taxid = temp.getParent().getId();



						int rank = rankForChar(tax_name.charAt(0), use_db);

						fw_name.write(taxid + "\t" + tax_name.substring(3) + "\n");
						fw_nodes.write(taxid + "\t" + parent_taxid + "\t" + rank + "\n");
					}

					//int taxid = temp.getId();
					//fw_gi.write(gg_id + "\t" + taxid + "\n");
					gitax.put(gg_id, taxid);


				}
			}
			ArrayList<Integer> sorted_gi = new ArrayList<Integer>();
			sorted_gi.addAll(gitax.keySet());
			Collections.sort(sorted_gi);
			for(int gi: sorted_gi){
				fw_gi.write(gi + "\t" + gitax.get(gi) + "\n");
			}
			fw_gi.flush();
			fw_name.flush();
			fw_nodes.flush();

		} finally {
			if (br != null) {
				br.close();
			}
			if (fw_gi != null) {
				fw_gi.close();
			}
			if (fw_nodes != null) {
				fw_nodes.close();
			}
			if (fw_name != null) {
				fw_name.close();
			}
		}


	}

	// converts >xxx header to >gi|xxx| format, where xxx is greengenes id (yes, it's a lie)
	public static void GGFastaToGIFormat(String input, String output) throws IOException{
		BufferedReader br = null;
		FileWriter fw = null;
		try {
			br = new BufferedReader(new FileReader(input));
			fw = new FileWriter(output);

			while(br.ready()){
				String str = br.readLine();
				if(str.charAt(0) == '>'){
					fw.write(">gi|" + str.substring(1).trim() + "|" + "\n");
				}
				else fw.write(str.trim() + "\n");
			}

		}
		finally {
			if (br != null) {
				br.close();
			}
			if (fw != null) {
				fw.close();
			}
		}
	}


	public static int rankForChar(char c, boolean use_db){
		int rank;
		//mysql autoincrement starts with 1
		if(use_db){
			switch (c) {
			case 'k':  rank = 2; 
			break;
			case 'p':  rank = 3; 
			break;
			case 'c':  rank = 4; 
			break;
			case 'o':  rank = 5; 
			break;
			case 'f':  rank = 6; 
			break;
			case 'g':  rank = 7; 
			break;
			case 's':  rank = 8; 
			break;
			case 'r': rank = 1; 
			break;
			default:  rank = 0; 
			break;
			}
		}

		else{		
			switch (c) {
			case 'k':  rank = 1; 
			break;
			case 'p':  rank = 2; 
			break;
			case 'c':  rank = 3; 
			break;
			case 'o':  rank = 4; 
			break;
			case 'f':  rank = 5; 
			break;
			case 'g':  rank = 6; 
			break;
			case 's':  rank = 7; 
			break;
			case 'r': rank = 0; 
			break;
			default:  rank = 0; 
			break;
			}
		}

		return rank;
	}

	// rank: 7 - genus, 8 - species
	// identity: 99, 97, 94
	public static HashMap<Integer, HashSet<Integer>> findCandidateSisterTaxons(int rank, int identity) throws ClassNotFoundException, SQLException, IOException{
		Connection con = null;
		BufferedReader br = null;
		FileWriter fw = null;
if (identity != 99 && identity != 97 && identity != 94 ) {
	System.out.println("identity must be equal to 99, 97 or 94");
}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loading success!");
			String url = "jdbc:mysql://localhost/ncbi";
			String name = "root";
			String password = "lotus34";

			con = DriverManager.getConnection(url, name, password);
			System.out.println("Connected.");

			String path = "C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_otus~/gg_13_5_otus/otus/";

			HashMap <Integer, HashSet<Integer>> candidates99 = new HashMap<Integer, HashSet<Integer>>();
			br = new BufferedReader(new FileReader(path + "99_otu_map.txt"));
			fw = new FileWriter("log_sister.txt");

int counter = 0 ;
			while(br.ready()){
				String str = br.readLine();
				String[] splitter = str.split("\\s++");
				HashSet<Integer> taxids = new HashSet<Integer>(1);
				StringBuilder sb = null;
				if(rank == 7){
					
					sb = new StringBuilder("select if(nodes.id_ranks = 7, gi_taxid.taxid, nodes.parent_taxid) as ti"
							+ " from gi_taxid join nodes on gi_taxid.taxid=nodes.taxid "
							+ "where gi_taxid.gi in (");
					for (int i = 1; i < splitter.length-1; i++){
						sb.append(splitter[i].trim()+ ", ");
					}
					sb.append(splitter[splitter.length-1].trim());
					sb.append(") and nodes.id_ranks > 6;");
					
				}
					Statement statement = con.createStatement();
					ResultSet resultSet;
					resultSet = statement
							.executeQuery(sb.toString());
					while (resultSet.next()){
						if(resultSet.getString("ti")!=null) {
                    taxids.add(Integer.parseInt(resultSet.getString("ti")));
					}
					}
					candidates99.put(Integer.parseInt(splitter[1]), taxids);
					//System.out.println(Integer.parseInt(splitter[1]) + " " + taxids);
					counter++;
					if(counter == 100)
					{
					System.out.println(Integer.parseInt(splitter[1]));
					counter = 0;
					}
			}
			

			if (identity == 99) return candidates99;
			else if (identity < 99){
				br.close();
				
				HashMap <Integer, HashSet<Integer>> candidates97 =  new HashMap<Integer, HashSet<Integer>>();
				br = new BufferedReader(new FileReader(path + "97_otu_map.txt"));
				
				while(br.ready()){
					String str = br.readLine();
					String[] splitter = str.split("\\s++");
					HashSet<Integer> taxids = new HashSet<Integer>(1);
					
					for (int i = 1; i < splitter.length; i++){
						
						if(!candidates99.containsKey(Integer.parseInt(splitter[i]))){
							fw.write("no key "  + splitter[i] +"\n");
						}
						else if(!candidates99.get(Integer.parseInt(splitter[i])).isEmpty()){
						
						taxids.addAll(candidates99.get(Integer.parseInt(splitter[i])));
						}
					}
					candidates97.put(Integer.parseInt(splitter[1]), taxids);
				}
				
				if (identity == 97) return candidates97;
				else if (identity < 97){
					br.close();
					
					HashMap <Integer, HashSet<Integer>> candidates94 =  new HashMap<Integer, HashSet<Integer>>();
					br = new BufferedReader(new FileReader(path + "94_otu_map.txt"));
					
					while(br.ready()){
						String str = br.readLine();
						String[] splitter = str.split("\\s++");
						HashSet<Integer> taxids = new HashSet<Integer>(1);
						
						for (int i = 1; i < splitter.length; i++){
							if(!candidates97.containsKey(Integer.parseInt(splitter[i]))){
								fw.write("no key in 97 "  + splitter[i] +"\n");
							}
							else if(!candidates97.get(Integer.parseInt(splitter[i])).isEmpty()){
							
							taxids.addAll(candidates97.get(Integer.parseInt(splitter[i])));
							}
						}
						candidates94.put(Integer.parseInt(splitter[1]), taxids);
					}
					
					return candidates94;
				}
			}

			System.out.println("Disconnected.");
			return null;
		}
		finally {
			if (br != null) {
				br.close();
			}
			if (con != null) {
				con.close();
			}
			if (fw != null) {
				fw.close();
			}
		}

	}




	public static void main (String[] args){
		try {
//			FileWriter fw = new FileWriter("genera_united_at_99.txt");
//			HashMap<Integer, HashSet<Integer>> cand = findCandidateSisterTaxons(7, 99);
//			for (int i: cand.keySet()){
//				fw.write("gi: " + i + " taxids: ");
//				for (int j: cand.get(i)){
//					fw.write(j + "	");
//				}
//				fw.write("\n");
//			}
//			fw.close();
			parseTaxonomy("named_taxonomy.txt", "gi_taxid.dmp", "names.dmp", "nodes.dmp", true);
			//GGFastaToGIFormat("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5.fasta", "greengenes.fasta");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
