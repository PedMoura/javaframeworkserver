package server;

import static spark.Spark.*;
import org.apache.log4j.BasicConfigurator; 

/*source code sparkjava
 * logger
 * modificar o metodo echo para uma classe diferente
 
*/
public class Main {
	
	public static void configurator() {
		BasicConfigurator.configure();
	}
		
    public static void main(String[] args) {
    	configurator();
    	get("/hello", (req, res) -> "Hello World");
        get("/echo/:id", (req,res) -> ResponseClass.Echo(req.params(":id")));
    }
    

}