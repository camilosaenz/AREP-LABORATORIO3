package edu.escuelaing.arep.app;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App{
    
	public static void main( String[] args ){
        ServidorHTTP servidor = new ServidorHTTP();
        MySpark.post("/about", "En esta Pagina encontraras la informacion con respecto a los Octavos de Final de la UEFA Champions League, que se disputaran en la tercer y cuarta semana del mes de febrero del año 2021, dicha competicion marcara el inicio de las rondas finales de la máxima competición continental y ya se conocen los cruces después del sorteo que la UEFA llevó a cabo el lunes 14 de diciembre en Nyom. Algo interesante en el sorteo fue la composicion de los bombos los cuales eran : Composición del Bombo 1, cabezas de serie: Bayern, Manchester City, Liverpool, Borussia Dortmund, Chelsea Juventus, PSG, Real Madrid,\r\n"
        		+ "\r\n"
        		+ "Composición del Bombo 2, segundo de grupo: Porto, Lazio, Barcelona, RB Leipzig, Sevilla, Atalanta, Borussia Monchengladbach, Atlético de Madrid. La normativa indica que no podrán enfrentarse equipos del mismo país ni clubes que ya se hayan medido en la fase de grupos por lo que será un sorteo condicionado.");
        try {
        	servidor.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
