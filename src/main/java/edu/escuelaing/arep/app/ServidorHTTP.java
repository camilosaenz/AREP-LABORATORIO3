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

public class ServidorHTTP {
	
	private String ruta = "src/main/resources";
	private PrintWriter out = null;
	private ConexionDB conexion = null;
	
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
	
	
	private int getPort() {
		if(System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 35000;
	}


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
	
	private String invoke(String app) {
		
		String line = getHeader("html");
		String file = MySpark.get(app);
		
		if(app.equals("/partido")) {
			file = "";
			ArrayList<String[]> partido = conexion.getPartido();
			for(String[] p : partido) {
				file = file + " Partido UEFA - " + p[0] + " VS " + p[1] + "Estadio: " + p[2] + "Fecha: " + p[3] + "\n";
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

	private int getType(String tipo) {
		if(tipo.contains("html")) {
			return 0;
		}else if(tipo.contains("js")) {
			return 1;
		}else {
			return 2;
		}
		
	}

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
