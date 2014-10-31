package org.hyperion.rs2.sql;

public class SQLUtils {

	/**
	 * Defends against SQL Injection.
	 *
	 * @param s
	 * @return
	 */
	public static String checkInput(String s) {
		s = s.replaceAll("'", "");
		return s;
	}

	public static void main(String... args) {
		System.out.println(checkInput("vls's"));
	}

}
