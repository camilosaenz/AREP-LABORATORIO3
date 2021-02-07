package edu.escuelaing.arep.app;

import java.sql.*;
import java.util.ArrayList;

/**
 * 
 * @author Camilo
 *
 */
public class ConexionDB {
	
	private static String uri = "postgres://cfrrzvighilred:6e049102c73d24f83e047feb5abfc96dd73742a2356e97383c795a2bf7fcc574@ec2-3-214-3-162.compute-1.amazonaws.com:5432/dc4sebsf2fbtrc\r\n"
			+ "";
	private static String user = "cfrrzvighilred";
	private static String password = "6e049102c73d24f83e047feb5abfc96dd73742a2356e97383c795a2bf7fcc574";
	private static Connection conexion = null;
	
	/**
	 * 
	 */
	public ConexionDB() {
		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection(uri, user, password);
		}catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 
	 */
	public void createTable() {
		
		String CREATE_TABLE="CREATE TABLE Equipo ("
				+ "LOCAL VARCHAR(20) NOT NULL,"
				+ "VISITANTE VARCHAR(20) NOT NULL,"
				+ "ESTADIO VARCHAR(20) NOT NULL,"
				+ "FECHA VARCHAR(20) NOT NULL,";
		
        try {
            Statement statement = conexion.createStatement();
            statement.execute(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String[]> getPartido(){
		
		ArrayList<String[]> lista = new ArrayList<>();
		String select = "SELECT * FROM Equipo";
		try {
			Statement  statement = conexion.createStatement();
			ResultSet resultSet = statement.executeQuery(select);
			while(resultSet.next()) {
				String LOCAL = resultSet.getString("LOCAL");
				String VISITANTE = resultSet.getString("VISITANTE");
				String ESTADIO = resultSet.getString("ESTADIO");
				String FECHA = resultSet.getString("FECHA");
				String[] valores = {LOCAL, VISITANTE, ESTADIO, FECHA};
				lista.add(valores);
			}
		} catch (SQLException e) {
            e.printStackTrace();
        }
		return lista;
	}
	
}
