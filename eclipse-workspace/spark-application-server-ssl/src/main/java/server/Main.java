package server;

import static spark.Spark.*;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;

public class Main {
	
	public static void configurator() {
		BasicConfigurator.configure();
	}

	
	public static void main(String[] args){
    	configurator();
    	ResponseClass res_options = new ResponseClass(); 

    	secure("/home/pedro/eclipse-workspacenkeystore.jks", "asint2017", null, null);
    	
    	/*
    	 * ROUTES
    	 */
    	get("/", (req,res) -> {
    		res.redirect("/hello");
    		return "";
    	});
    	get("/hello", (req, res) -> "Hello World "+ req.session().id());
        get("/echo/:id", (req,res) -> {
            return res_options.Echo(req.params(":id"));
        }); 
        //Dynamic Route - the method is loaded during runtime
        get("/Dynamic", (req,res) -> {
        	Class<?> newClass = DynamicRouteLoader.loader();
        	return newClass.getMethod("Dynamic").invoke(newClass.newInstance());       	
        });        	
        get("/session", (req,res) ->{
			return "session " + req.session().id() + " created at " + Date(req.session().creationTime());
        });
        /*
        get("/ip", (req,res) ->{
        	return req.ip() + " requested " + req.url();
        });
        get("/stop", (req,res) ->{
        	stop();
        	return "";
        });*/
    }


	private static Object Date(long creationTime) {
		Date date = new Date(creationTime);
		return date;
	}
}
