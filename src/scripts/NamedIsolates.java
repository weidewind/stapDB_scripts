package scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

public class NamedIsolates {
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
		
		FileWriter fw_ali = new FileWriter("prok_named.ali");
		FileWriter fw_seq = new FileWriter("prok_named.seq");
	
		HashSet<Integer> gis = new HashSet<Integer>();
		
		BufferedReader br_ali = new BufferedReader(new FileReader("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_ssualign/gg_13_5_ssualign.fasta"));
		BufferedReader br_seq = new BufferedReader(new FileReader("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5.fasta"));
		
		while(br_seq.ready()){

			String str = br_seq.readLine();
			String gi = str.substring(1).trim();
			String seq = br_seq.readLine();
			
			getName.setString(1, gi);
			ResultSet rs = getName.executeQuery();
			rs.next();
			String prokname = rs.getString("PROKMSANAME");
				
			if (prokname !=null){
                String[] parser = prokname.split("\\s+");
                if (genera.contains(parser[0])){
                	fw_seq.write(str + "\n" + seq + "\n");
                	gis.add(Integer.parseInt(gi));
                }
			 }
			
			
			
		}
		br_seq.close();
		fw_seq.close();
		
		while(br_ali.ready()){
			String str = br_ali.readLine();
			String gi = str.substring(1).trim();
			String seq = br_ali.readLine();
			
			if (gis.contains(Integer.parseInt(gi))){
              	fw_ali.write(str + "\n" + seq + "\n");
			}
		}
		
		br_ali.close();
		fw_ali.close();

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
