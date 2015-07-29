package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Ends {
public static void main(String[] args) throws IOException{
	BufferedReader br = new BufferedReader(new FileReader("src/tests/endsin"));
	ArrayList<String> sequences = new ArrayList<String>();
	while(br.ready()){
		sequences.add(br.readLine());
	}
	int[][] allEnds = new int[2][3];
	allEnds[0][0] = 3;
	allEnds[0][1] = 3;
	allEnds[0][2] = 3;
	allEnds[1][0] = 13;
	allEnds[1][1] = 12;
	allEnds[1][2] = 13;
	 int leftmost = sequences.get(0).length();
	   int rightmost = 0;
	   for(int i = 0; i < sequences.size() - 1; i++){ // -1, because we shouldn't take outgroup into account
	   int start = allEnds[0][i];
	   int stop = allEnds[1][i];
	   String seq = sequences.get(i);
		int t = 0;
		int count;
		if (seq.charAt(0) == '-') count = 1;
		else count = 0;
		
		while(t-count+1 < stop){
			t = seq.indexOf('-', t+1);
			if (t == -1) break;
			count++;
			if (t-count+1 >= start){
				if (leftmost > start+count-2) leftmost = start+count-2;
			}
		}
		if (rightmost < stop+count-1) rightmost = stop+count-1; 
	   }
	   System.out.println(leftmost + " " + rightmost);
	   
	   ArrayList<String> hitsAndOutgroupAlignment = new ArrayList<String>();
		for(String seq: sequences){
			String fragment = new String(seq.substring(leftmost, rightmost));
			hitsAndOutgroupAlignment.add(fragment);
		}
	   char[][] alignment = new char[hitsAndOutgroupAlignment.size()][rightmost - leftmost];
		
		int count = 0; //symbols, excluding '-'
		position: for(int j = 0; j <rightmost - leftmost; j++){
		for(String seq: hitsAndOutgroupAlignment){
			if(seq.charAt(j) != '-') {
				for (int k = 0; k < hitsAndOutgroupAlignment.size(); k++){
					alignment[k][count] = hitsAndOutgroupAlignment.get(k).charAt(j);
				}
				count++;
				continue position;
			}
		}
	}
		for(int  i = 0; i < hitsAndOutgroupAlignment.size(); i++){
			System.out.println(String.copyValueOf(alignment[i]));
		}
br.close();
}
}
