package server;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

public class Main {
	
	static List<String> userlist = new ArrayList<String>();
	static int routeIndex = 0;
	
	public static void configurator() {
		BasicConfigurator.configure();
	}

	
	public static void main(String[] args){
    	configurator();
    	ResponseClass res_options = new ResponseClass();
    	//port(4567);
    	secure("/home/pedro/eclipse-workspace/keystore.jks", "asint2017", null, null);//ssl
    	
    	/*
    	 * ROUTES
    	 */
    	get("/", (req,res) -> {
    		res.redirect("/hello");
    		return "";
    	});
    	get("/hello", (req, res) -> "Hello World "); //hello world
    	
    	get("/hello/:username", (req,res) -> { //redirect to hello world and store the user
    		ResponseClass.adduser(req.params("username"));
    		res.redirect("/hello");
    		return null;
    	});
    	
    	get("/xmltry/:section", (request, response) -> { // returns section as xml
    	    response.type("text/xml");
    	    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>" + request.params("section") + "</xml>";
    	});
    	
        get("/echo/:id", (req,res) -> { // checks if id is numeric or not and returns accordingly
            return res_options.Echo(req.params(":id"));
        }); 
        
        
        get("/Dynamic", (req,res) -> { //Dynamic Route - the method is loaded during runtime
        	Class<?> newClass = DynamicRouteLoader.loader();
        	return newClass.getMethod("Dynamic").invoke(newClass.newInstance());       	
        });        	
        
        get("/session", (req,res) ->{ // returns sessions id and session's inception 
			return "session " + req.session().id() + " created at " + Date(req.session().creationTime());
        });
        
        get("users/:user", (req,res) -> { // if the user already accessed the /hello with the username returns the users' list
        	String returnvalue = ResponseClass.getuserlist(req.params("user"));
        	return returnvalue; 
        });
        
        get("/ip", (req,res) ->{ // get the client ip
        	return req.ip() + " requested " + req.url();
        });/*
        get("/stop", (req,res) ->{ // stops the server
        	stop();
        	return "";
        });*/
        get("/teste", (req,res) -> {
        	get("/"+routeIndex, (req1,res1) ->{
        		routeIndex++;
        		return "hello";
        	});
        	return "okapa";
        });
        
    }


	private static Object Date(long creationTime) {
		Date date = new Date(creationTime);
		return date;
	}
}
