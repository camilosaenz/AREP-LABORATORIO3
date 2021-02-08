package edu.escuelaing.arep.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase ayuda a implementar un Spark propio
 * @author Camilo
 *
 */
public class MySpark {
	
	private static Map<String, String> fin = new HashMap<>();
	
	/**
	 * Este metodo ayuda a la implementacion de Spark
	 * @param app String con un html.
	 * @return retorna 
	 */
	public static String get(String app) {
		if(fin.containsKey(app)) {
			return fin.get(app);
		}
		return null;
	}

	/**
	 * Implementa un Post de Spark.
	 * @param llave 
	 * @param valor
	 */
	public static void post(String llave, String valor) {
		fin.put(llave, valor);
	}

}
