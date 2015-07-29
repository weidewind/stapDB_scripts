package scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class AccToGi {

	public static void main (String[] args) throws IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loading success!");
			String url = "jdbc:mysql://localhost/utils";
			String name = "root";
			String password = "lotus34";
			try {
				Connection con = DriverManager.getConnection(url, name, password);
				System.out.println("Connected.");

				FileReader in = new FileReader("C:/Users/weidewind/Documents/CMD/DBs/greengenes/gg_13_5_accessions.txt");
				BufferedReader br = new BufferedReader(in);
				PrintWriter out2 = new PrintWriter(new FileWriter("new_gg_no_gi.txt"));
				PrintWriter out = new PrintWriter(new FileWriter("new_gg_acc_gi.txt"));

				//matcher for ACC?


				while (br.ready()) {
					String str = br.readLine();
					String outstr;

//					if (str.charAt(0) == '>'){

						String header =  str.substring(1);
						Statement statement = con.createStatement();
//                     changed from 1 to 2
						String acc = str.split("\\s++")[2];
						String acc_no_version;
						ResultSet resultSet;
						resultSet = statement
								.executeQuery("select gi from acc_to_gi where acc = '" + acc + "';");


//						if (acc.indexOf('.') != -1){
//							acc_no_version = acc.split("\\.")[0];
//							resultSet = statement
//									.executeQuery("select gi from acc_to_gi where acc like '" + acc_no_version + "._';");
//
//						}
//						else {
//							acc_no_version = acc;
//							resultSet = statement
//									.executeQuery("select gi from acc_to_gi where acc = '" + acc_no_version + "';");
//
//
//						}


						if (resultSet.next()){
							String gi = resultSet.getString("gi");
							outstr = "gi|" + gi + "|" + header;
						}
						else {
							out2.println(acc);
//							String update = Fetcher.get("http://www.ncbi.nlm.nih.gov/nuccore/" + acc + "?report=docsum&format=text");
//							update.split("\\n")[3];
							outstr = "gi|NO GI|acc" + acc + "|" + header;
						}
						if (resultSet.next()) {
							out2.println("Multiple gi's for acc " + acc);
							outstr = "gi|MULTI GI|acc" + acc + "|" + header;
						}


//					}
//					else {
//						outstr = str;
//					}

					out.println(outstr);
				}

				in.close();
				out.close();
				out2.close();
				con.close();
				System.out.println("Disconnected.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
