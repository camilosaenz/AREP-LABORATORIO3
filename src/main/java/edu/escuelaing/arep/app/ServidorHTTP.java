package edu.escuelaing.arep.app;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Esta clase permite hacer el proceso para poder montar los servidores web y utilizar para poder abrir los diferentes recursos de la pagina.
 * @author Camilo
 *
 */
public class ServidorHTTP {
	
	private String ruta = "src/main/resources";
	private PrintWriter out = null;
	private ConexionDB conexion = null;
	
	/**
	 * Este metodo inicia obteniendo el puerto de arranque
	 * @throws IOException
	 */
	public void start() throws IOException {
		
		int port = getPort();
		conexion = new ConexionDB();
		
		while(true) {
			
			ServerSocket serverSocket = null;
			
			// Server Socket
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
                System.err.println("Could not listen on port: 35000.");
                System.exit(1);
            }
			Socket clientSocket = null;
			
			// Client Socket
			try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
			} catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
			processRequest(clientSocket);
			out.close();
			clientSocket.close();
			serverSocket.close();
		}
	}
	
	/**
	 * Ayuda implementado y retornando el puerto de funcion.
	 * @return retorna el puerto.
	 */
	private int getPort() {
		if(System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 35000;
	}

	/**
	 * Permite que se realice el proceso para abrir /index.html
	 * @param clientSocket cliente.
	 * @throws IOException
	 */
	private void processRequest(Socket clientSocket) throws IOException {
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String line, file = "";
		
		while((line = buffer.readLine()) != null) {
			if(line.contains("GET")){
				file = line.split(" ")[1];
				if(file.startsWith("/Apps")) {
					String app = file.substring(5);
					out.println(invoke(app));
				}else {
					if(file.equals("/")) {
						file = "/index.html";
					}
					getResourse(file, clientSocket);
				}
			}
			if(!buffer.ready()) {
				break;
			}
		}
		buffer.close();
	}
	
	/**
	 * Este metodo permite obtener los datos de la base de datos, en un recurso /Apps/partidos
	 * @param app String con la pagina a abrir
	 * @return
	 */
	private String invoke(String app) {
		
		String line = getHeader("html");
		String file = MySpark.get(app);
		
		if(app.equals("/partidos")) {
			file = "";
			ArrayList<String[]> partidos = conexion.getPartido();
			for(String[] partido : partidos) {
				file = file + " Partido UEFA - " + partido[0] + " VS " + partido[1] + " - Estadio: " + partido[2] + " - Fecha: " + partido[3]+"\n";
			}
			return line + file;
			
		}if(file != null) {
			return line + file;
		}
		
		return errorResponse(app);
	}

	private String getHeader(String tipo) {
		return "HTTP/1.1 200 OK\r\n" + "Content-Type: text/"+ tipo +"\r\n" + "\r\n";
	}
	
	private String errorResponse(String app) {
        String line = "HTTP/1.1 404 Not Found \r\nContent-Type: text/html \r\n\r\n <!DOCTYPE html> <html>"
                + "<head><title>404</title></head>" + "<body> <h1>404 Not Found " + app
                + "</h1></body></html>";
        return line;
	}

	/**
	 * Este metodo permite obtener el recurso dado.
	 * @param file archivo String
	 * @param clientSocket
	 * @throws IOException
	 */
	private void getResourse(String file, Socket clientSocket) throws IOException {
		
		String line;
		int tipo = getType(file);
		if(tipo == 0) {
			line = getFile(file, "html");
			out.println(line);
		}else if(tipo == 1) {
			line = getFile(file, "json");
			out.println(line);
		}else if(tipo == 2) {
			getImage(file, clientSocket.getOutputStream());
		}
	}
	
	/**
	 * Este motodo permite obtener el tipo de recurso
	 * @param tipo String del tipo de recurso
	 * @return el recurso
	 */
	private int getType(String tipo) {
		if(tipo.contains("html")) {
			return 0;
		}else if(tipo.contains("js")) {
			return 1;
		}else {
			return 2;
		}
		
	}
	
	/**
	 * Permite obtener una imagen en una pagina.
	 * @param tipo String con la imagen a abrir
	 * @param outClient cliente
	 */
	private void getImage(String tipo, OutputStream outClient) {
		
		String path = ruta + tipo;
		File file = new File(path); 
		if(file.exists()) {
			try {
				BufferedImage image = ImageIO.read(file);
				ByteArrayOutputStream bit = new ByteArrayOutputStream();
				DataOutputStream data = new DataOutputStream(outClient);
				ImageIO.write(image, "PNG", bit);
				data.writeBytes("HTTP/1.1 200 OK \r\n" + "Content-Type: image/png \r\n" + "\r\n");
				data.write(bit.toByteArray());
			} catch (IOException e) {
                e.printStackTrace();
            }
		}else {
			out.println(errorResponse(file.getName()));
		}
		
	}
	/**
	 * Este metodo permite a obtener un String a abrir
	 * @param route ruta que contiene los archivos
	 * @param tipo String para generar pagina
	 * @return
	 */
	private String getFile(String route, String tipo) {
		
		String line = getHeader(tipo);
		String path = ruta + route;
		File file = new File(path);
		if(file.exists()) {
			String contenido;
			try {
				BufferedReader buffer = new BufferedReader(new FileReader(file));
				while((contenido = buffer.readLine()) != null) {
					line = line + contenido;
				}
			}catch (IOException e) {
                e.printStackTrace();
            }
		}else {
			line = errorResponse(file.getName());
		}
		
		return line;
	}
	
}
