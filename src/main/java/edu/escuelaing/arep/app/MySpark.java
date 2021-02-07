package edu.escuelaing.arep.app;

import java.util.HashMap;
import java.util.Map;

public class MySpark {
	
	private static Map<String, String> fin = new HashMap<>();

	public static String get(String app) {
		if(fin.containsKey(app)) {
			return fin.get(app);
		}
		return null;
	}

	public static void post(String llave, String valor) {
		fin.put(llave, valor);
	}

}
