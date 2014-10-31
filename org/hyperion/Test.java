package org.hyperion;

import java.io.BufferedReader;
import java.io.FileReader;

public class Test {
	public static void main(String[] args) {
		try {
			BufferedReader r = new BufferedReader(new FileReader("./data/capitals.txt"));
			String s;
			while((s = r.readLine()) != null) {
				int index = s.indexOf("- ");
				String country = s.substring(0, index);
				country = country.replaceAll(" ", "");
				String capital = s.substring(index + 2).toLowerCase();
				System.out.println("<question>What is the capital of " + country + "?");
				System.out.println("<answer>" + capital);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
