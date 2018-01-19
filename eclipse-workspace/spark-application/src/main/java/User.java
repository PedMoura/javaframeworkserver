import static spark.Spark.*;

import org.apache.log4j.BasicConfigurator;
/*source code sparkjava
 * logger
 * modificar o metodo echo para uma classe diferente
 
*/
public class User {
	
	public static void configurator() {
		BasicConfigurator.configure();
	}
		
    public static void main(String[] args) {
    	configurator();
        get("/hello", (req, res) -> "Hello World");
        get("/echo/:id", (req,res) -> echo(req.params(":id")));
    }
    
    public static boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }
    
    private static String echo(String id) {
    	if(isNumeric(id)) {
    		return "user with id number " + id;
    	}else {
    		return "Echo " + id;
    	}
    }
}