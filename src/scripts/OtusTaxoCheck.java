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




public class OtusTaxoCheck {


	public static void check() throws IOException, ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loading success!");
		String url = "jdbc:mysql://localhost/ncbi";
		String name = "root";
		String password = "lotus34";

		Connection con = DriverManager.getConnection(url, name, password);
		System.out.println("Connected.");

		String getTaxonString =
				"select taxid from gi_taxid where gi = ?";

		con.setAutoCommit(false);

		PreparedStatement getTaxon = con.prepareStatement(getTaxonString);


		FileWriter fw = new FileWriter("otus_taxo_check");
		BufferedReader br = new BufferedReader(new FileReader("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_otus/otus/99_otu_map.txt"));
		int count = 0;
		while(br.ready()){
			HashSet<Integer> set = new HashSet<Integer>();
			String str = br.readLine();
			String[] gis = str.split("\\s+");
			for (int i = 1; i < gis.length; i++){
				getTaxon.setString(1, gis[i]);
				ResultSet children = getTaxon.executeQuery();
				children.next();
				set.add(children.getInt("taxid"));
			}
			if (set.size() > 1){
				for (int taxid: set){
					fw.write(taxid + " ");
				}
				fw.write("\n");
			}
			count++;
			System.out.println(count);
		}
		br.close();
		fw.close();

	}

	public static void main (String[] args) throws ClassNotFoundException, IOException, SQLException{
		check();
	}
	
}
	