package server;

import static spark.Spark.*;

import org.apache.log4j.BasicConfigurator;


/*source code sparkjava
 * logger
 * modificar o metodo echo para uma classe diferente
 *
*/
public class Main {
	
	
	public static void configurator() {
		BasicConfigurator.configure();
	}

	
	public static void main(String[] args){
    	configurator();
    	/*new DynamicThread().start();*/ // starting the loading thread
    	ResponseClass res_options = new ResponseClass(); 
    	
    	/*
    	 * ROUTES
    	 */
    	get("/hello", (req, res) -> "Hello World");
        get("/echo/:id", (req,res) -> {
            return res_options.Echo(req.params(":id"));
        }); 
        //Dynamic Route - the method is loaded in runtime
        get("/Dynamic", (req,res) -> {
        	Class<?> newClass = DynamicRouteLoader.loader();
        	return newClass.getMethod("Dynamic").invoke(newClass.newInstance());       	
        });        		  
    }
}
