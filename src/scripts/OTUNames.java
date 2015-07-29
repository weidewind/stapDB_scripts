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
import java.util.HashSet;
import java.util.List;


public class OTUNames {

	public static void main (String[] args){
		try {
			
			BufferedReader br1 = new BufferedReader(new FileReader("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_otus/otu_mysql_table/genera.txt"));
			HashSet<String> genera = new HashSet<String>();
			
			while(br1.ready()){
				String str = br1.readLine();
				genera.add(str.trim());
			}

			
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loading success!");
		String url = "jdbc:mysql://localhost/greengenes";
		String name = "root";
		String password = "lotus34";

		Connection con = DriverManager.getConnection(url, name, password);
		System.out.println("Connected.");

		String getNameString = "select PROKMSANAME from greengenes where PROKMSA_ID = ?";
		
		con.setAutoCommit(false);

		PreparedStatement getName =  con.prepareStatement(getNameString);
		
		FileWriter fw = new FileWriter("99_otu_organisms");
		
		BufferedReader map = new BufferedReader(new FileReader("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_otus/otus/99_otu_map.txt"));
		
		while(map.ready()){

			String str = map.readLine();
			String[] gis = str.split("\\s+");
			HashMap<String, Integer> otu_names = new HashMap<String, Integer>();
			for (int i = 1; i < gis.length; i++){
				getName.setString(1, gis[i]);
				ResultSet rs = getName.executeQuery();
				rs.next();
				String prokname = rs.getString("PROKMSANAME");
				
				if (prokname !=null){
				String organism = "";
                String[] parser = prokname.split("\\s+");
                if ((parser[0].length() == 2 && parser[0].charAt(1) == '.') || genera.contains(parser[0])){
                	if (parser.length > 1){
                	organism = parser[0] + " " + parser[1];
                	if (parser[1].equals("subsp.")){
                		organism += parser[2];
                	}
                	}
                	else organism = parser[0];
                	
                	if (otu_names.containsKey(organism)){
                		int count = otu_names.get(organism);
                		count++;
                		otu_names.put(organism,  count);
                	}
                	else {
                		otu_names.put(organism,  1);
                	}

                }
                

                
			    }
			}
			
			if (!otu_names.isEmpty()){
				
				StringBuilder names = new StringBuilder();
				for (String org: otu_names.keySet()){
					names.append(org);
					names.append(" (" + otu_names.get(org) + ")");
					names.append(", ");
				}
				
				fw.write(gis[1] + "\t" + names.substring(0, names.length()-2) + "\n");
				
			}
			else fw.write(gis[1] + "\n");

		}
		map.close();
		fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}