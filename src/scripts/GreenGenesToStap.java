package scripts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class GreenGenesToStap {
	Connection con;
	FileWriter fw;
	PreparedStatement getChildren;
	PreparedStatement getName;
	PreparedStatement getGi;
	HashMap <Integer, String> giToStapId;

	private static GreenGenesToStap instance;

	private GreenGenesToStap() throws IOException, ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loading success!");
		String url = "jdbc:mysql://localhost/ncbi";
		String name = "root";
		String password = "lotus34";

		con = DriverManager.getConnection(url, name, password);
		System.out.println("Connected.");

		String getChildrenString =
				"select taxid from nodes where parent_taxid = ?";
		String getGiString =
				"select gi from gi_taxid where taxid = ?";
		String getNameString =
				"select name from names where taxid = ?";

		//	String countChildrenString =
		//			"select count (*) from ";

		//		String getInfoString =
		//				"select gi_taxid.gi, names.name, nodes.taxid from gi_taxid"
		//				+ " join names on gi_taxid.taxid = names.taxid"
		//				+ " left join nodes on gi_taxid.taxid = nodes.parent_taxid"
		//				+ "  where gi_taxid.taxid = ?";




		con.setAutoCommit(false);

		getChildren = con.prepareStatement(getChildrenString);
		getName = con.prepareStatement(getNameString);
		getGi = con.prepareStatement(getGiString);

		fw = new FileWriter("prok2.xml.index");

	}

	public static GreenGenesToStap getInstance() throws IOException, ClassNotFoundException, SQLException {
		if (instance == null) {
			instance = new GreenGenesToStap();
		}
		return instance;
	}

	public static void flush() throws SQLException, IOException{
		if (instance != null){
			instance.con.close();
			instance.fw.close();
		}
	}
	
	//returns taxonomy level for given stapid string: 7th level (species), if it has 6 points
	public static int level(String node_stapid){
		int count = 0;
		int start = 0;
		while(start < node_stapid.length()){
			start = node_stapid.indexOf('.', start);
			count++;
		}
		return count;
	}

	public void printChildren(String node_stapid, int node_taxid) throws SQLException, IOException{

		getChildren.setInt(1, node_taxid);
		getName.setInt(1, node_taxid);
		getGi.setInt(1, node_taxid);
		ResultSet children = getChildren.executeQuery();
		ResultSet name = getName.executeQuery();
		ResultSet gi = getGi.executeQuery();



		name.next();

	
		fw.write("<PROK" + node_stapid + " name=\"" + name.getString("name").toUpperCase() + "\" number=\"\">\n");

		if(!children.isBeforeFirst()){
			if (level(node_stapid) == 7){
				// update 17-06-14
				// if this taxonomic node is a species node
				fw.write("<file>PROK" + node_stapid + "_listdb</file>\n");
				FileWriter listdb = new FileWriter("PROK" + node_stapid + "_listdb");
				while(gi.next()){
					listdb.write("PROK" + gi.getInt("gi") + "_PROK" + node_stapid + "\n");
					//вот тут надо будет заполнять хэш
				}
				listdb.close();
			}
			
			else {
			// if this taxonomic node has no children nodes but is not a species node (I assume it obviously has some sequences, otherwise there wouldn't be such a node)
				// update 17-06-14
				fw.write("<PROK" + node_stapid + ".1" + " name=\"UNCLASSIFIED\" number=\"\">\n");
				fw.write("<file>PROK" + node_stapid + ".1" + "_listdb</file>\n");
				fw.write("</PROK" + node_stapid + ".1" + ">\n");
				FileWriter listdb = new FileWriter("PROK" + node_stapid + ".1" + "_listdb");
				while(gi.next()){
					listdb.write("PROK" + gi.getInt("gi") + "_PROK" + node_stapid + ".1\n");
					//вот тут надо будет заполнять хэш
				}
				listdb.close();
			}
		}
		else {

			ArrayList<Integer> childrenList = new ArrayList<Integer>();
			while(children.next()){
				childrenList.add(children.getInt("taxid"));
			}

			int count;
			if (!gi.isBeforeFirst()){ //this taxonomic node has no sequences (but has children nodes)
				count = 1;
			}
			else {//there are some sequences classified only to this taxonomic node
				fw.write("<PROK" + node_stapid + ".1" + " name=\"UNCLASSIFIED\" number=\"\">\n");
				fw.write("<file>PROK" + node_stapid + ".1" + "_listdb</file>\n");
				fw.write("</PROK" + node_stapid + ".1" + ">\n");
				FileWriter listdb = new FileWriter("PROK" + node_stapid + ".1" + "_listdb");
				while(gi.next()){
					listdb.write("PROK" + gi.getInt("gi") + "_PROK" + node_stapid + ".1\n");
					//вот тут надо будет заполнять хэш
				}
				listdb.close();
				count = 2;
			}


			for(int taxid: childrenList){
				printChildren(node_stapid + "." + count, taxid);
				count++;
			}


		}

		fw.write("</PROK" + node_stapid + ">\n");

	}


	public static void setNumbers() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("prok2.xml.index"));
		FileWriter fwr = new FileWriter("prok2_numbers.xml.index");
		while(br.ready()){
			String str = br.readLine();
			if(str.charAt(1) == 'P'){
				String[] splitter = str.split("\\s+");
				final String stapid = splitter[0].trim().substring(6);
				System.out.println(stapid);

				File dir = new File(".");

				File [] files = dir.listFiles(new FilenameFilter(){

					public boolean accept(File dir, String name) {
						return (name.startsWith("PROK."+ stapid + ".") || name.startsWith("PROK."+ stapid + "_") ); 
					}

				});


				List<File> list = Arrays.asList(files);

				int count = 0;
				for (File f: list){
					System.out.println(f.getName());
					count += countLines(f);
				}

				String[] splitter2 = str.split("\"\"");
				fwr.write(splitter2[0] + "\"" +  count + "\"" +  splitter2[1] + "\n");
			}
			else {
				fwr.write(str + "\n");
			}
		}
		br.close();
		fwr.close();
	}

	public static int countLines(File f) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(f));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	public static void adjustHeaders(String ali, String seq, String repseq, String combo) throws IOException{
		BufferedReader br_ali = new BufferedReader(new FileReader(ali));
		BufferedReader br_seq = new BufferedReader(new FileReader(seq));
		BufferedReader br_repseq = new BufferedReader(new FileReader(repseq));
		BufferedReader br_combo = new BufferedReader(new FileReader(combo));
		FileWriter fw_ali = new FileWriter("prok2.ali");
		FileWriter fw_seq = new FileWriter("prok2.seq");
		FileWriter fw_repseq = new FileWriter("prok2.rep.seq");
		FileWriter fw_combo = new FileWriter("combo2.rep.seq");

		HashMap <Integer, String> giToStapId = new HashMap <Integer, String>();

		//этот кусок кода станет лишним, когда хэш будет создаваться в нужном месте
		File dir = new File(".");

		File [] files = dir.listFiles(new FilenameFilter(){

			public boolean accept(File dir, String name) {
				return (name.startsWith("PROK")); 
			}

		});


		List<File> list = Arrays.asList(files);
		for (File f: list){
			BufferedReader brf = new BufferedReader(new FileReader(f));
			while(brf.ready()){
				String str = brf.readLine();
				String[] splitter = str.split("[K_]");

				giToStapId.put(Integer.parseInt(splitter[1]), splitter[3]);
			}
			brf.close();

		}

		//

		try{
					while(br_ali.ready()){
						String str = br_ali.readLine();
						if(str.charAt(0) != '>') throw new IllegalArgumentException("corrupted alignment file");
				
						else {
							int gi = Integer.parseInt(str.substring(1).trim());
							String stap_header = ">PROK" + gi + "_PROK" +  giToStapId.get(gi);
							fw_ali.write(stap_header + "\n");
							if (!br_ali.ready()) throw new IllegalArgumentException("corrupted alignment file (unexpected end of file)");
			
							else {
								fw_ali.write(br_ali.readLine() +"\n");
							}
						}
					}
				
					
					
					while(br_seq.ready()){
						String str = br_seq.readLine();
						if(str.charAt(0) != '>') throw new IllegalArgumentException("corrupted sequence file");
						else {
							int gi = Integer.parseInt(str.substring(1).trim());
							String stap_header = ">PROK" + gi + "_PROK" +  giToStapId.get(gi);
							fw_seq.write(stap_header + "\n");
							if (!br_seq.ready()) throw new IllegalArgumentException("corrupted sequence file (unexpected end of file)");
							else {
								fw_seq.write(br_seq.readLine() +"\n");
							}
						}
					}
					
					
					
					while(br_repseq.ready()){
						String str = br_repseq.readLine();
						if(str.charAt(0) != '>') throw new IllegalArgumentException("corrupted repseq file");
						else {
							int gi = Integer.parseInt(str.substring(str.indexOf('K')+1, str.indexOf('_')));
							String stap_header = ">PROK" + gi + "_PROK" +  giToStapId.get(gi);
							String stap_full_header = stap_header + str.substring(str.indexOf(' '));
							fw_repseq.write(stap_full_header + "\n");
							if (!br_repseq.ready()) throw new IllegalArgumentException("corrupted repseq file (unexpected end of file)");
							else {
								fw_repseq.write(br_repseq.readLine() +"\n");
							}
						}
					}

			while(br_combo.ready()){
				String str = br_combo.readLine();
				if(str.charAt(0) != '>') throw new IllegalArgumentException("corrupted combo file");
				else {
					if (str.charAt(1) != 'P'){
						fw_combo.write(str +"\n");
						fw_combo.write(br_combo.readLine() +"\n");
					}
					else {
						int gi = Integer.parseInt(str.substring(str.indexOf('K')+1, str.indexOf('_')));
						String stap_header = ">PROK" + gi + "_PROK" +  giToStapId.get(gi);
						String stap_full_header = stap_header + str.substring(str.indexOf(' '));
						fw_combo.write(stap_full_header + "\n");
						if (!br_combo.ready()) throw new IllegalArgumentException("corrupted combo file (unexpected end of file)");
						else {
							fw_combo.write(br_combo.readLine() +"\n");
						}
					}
				}
			}

		}
		finally{
			fw_ali.close();
			fw_seq.close();
			fw_repseq.close();
			fw_combo.close();
			br_ali.close();
			br_seq.close();
			br_repseq.close();
			br_combo.close();
		}



	}

	public static void main (String[] args) throws SQLException, IOException, ClassNotFoundException  {
		//		GreenGenesToStap worker = getInstance();
		//		try {
		//			worker.printChildren("", 1);
		//		} catch (SQLException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		finally{
		//			flush();
		//		}

		//		setNumbers();

		adjustHeaders("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_ssualign/gg_13_5_ssualign.fasta", "C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5.fasta", "C:/Users/weidewind/Documents/CMD/DBs/stap/db_dir/prok.rep.seq", "C:/Users/weidewind/Documents/CMD/DBs/stap/db_dir/combo.rep.seq");

	}
}

