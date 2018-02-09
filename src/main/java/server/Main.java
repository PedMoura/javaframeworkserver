package server;

import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
/*
 * html com lista de classes e de endpoints
 * ler do json o metodo a executar
 * */
public class Main {
	
	static List<String> userlist = new ArrayList<String>();
	static int routeIndex = 0;
	static List<Class<?>> objarray = new ArrayList<Class<?>>();
	
	public static void configurator() {
		BasicConfigurator.configure();
	}

	
	public static void main(String[] args){
    	configurator();
    	ResponseClass res_options = new ResponseClass();
    	//port(4567);
    	secure("deploy/keystore.jks", "asint2017", null, null);//ssl
    	staticFiles.location("/public");
    	
        File uploadDir = new File("Customjar");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist
    	/*
    	 * ROUTES
    	 */

    	get("/hello", (request, response) -> "Hello World "); //hello world
    	
    	get("template-example", (req, res) -> {
    	    Map<String, Object> model = new HashMap<>();
    	    return new VelocityTemplateEngine().render(
    	        new ModelAndView(model, "/public/Classlist.html")
    	    );
    	});
    	
    	get("/hello/:username", (request,response) -> { //redirect to hello world and store the user
    		ResponseClass.adduser(request.params("username"));
    		response.redirect("/hello");
    		return null;
    	});
    	
    	get("/xmltry/:section", (request, response) -> { // returns section as xml
    	    response.type("text/xml");
    	    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>" + request.params("section") + "</xml>";
    	});
    	
        get("/echo/:id", (request,response) -> { // checks if id is numeric or not and returns accordingly
            return res_options.Echo(request.params(":id"));
        }); 
        
        
        get("/dynamic", (request,response) -> { //Dynamic Route - the method is loaded during runtime
        	Class<?> newClass = DynamicRouteLoader.Loader("ResponseClass");
        	return newClass.getMethod("Dynamic").invoke(newClass.newInstance());       	
        });        	
        
        get("/session", (request,response) ->{ // returns sessions id and session's inception 
			return "session " + request.session().id() + " created at " + Date(request.session().creationTime());
        });
        
        get("users/:user", (request,response) -> { // if the user already accessed the /hello with the username returns the users' list
        	String returnvalue = ResponseClass.getuserlist(request.params("user"));
        	return returnvalue; 
        });
        
        get("/ip", (request,response) ->{ // get the client ip
        	return request.ip() + " request " + request.url();
        });/*
        get("/stop", (request,response) ->{ // stops the server
        	return "Server has stopped";
        	stop();
        	
        });*/
        get("/teste", (request,response) -> {
        	get("/"+routeIndex, (request1,response1) ->{
        		routeIndex++;
        		return "hello";
        	});
        	return "okapa";
        });
        
        get("/createobject", (request,response) ->{//ler from jar a class e nao deste package
        	//Class<?> newClass = DynamicRouteLoader.Loader("loadfromjar.Class1");
        	Class<?> newClass = DynamicRouteLoader.CustomLoader("Serverjar.jar");
        	objarray.add(newClass);
        	
        	//pede upload do json com o metodo
        	get("/obj/:n", (request1,response1) ->{
            	int index = Integer.parseInt(request1.params(":n"));
            	if(index < objarray.size()) {
      			  return "<body>"+
      		    		"<h2>Please upload a json file with the method that you want to execute</h2>"+
      		    		"<hr>"
      		    		+"<form method='post' enctype='multipart/form-data'>" 
	        			+ "    <input type='file' name='uploaded_file' accept=''>" 
	        			+ "    <button>Upload file</button>"
	        			+ "</form>"
	      			    + "</body>";
            	}else {
            		return "no such object";
            	}
        	});
        	
        	//le o metodo atraves do json e corre o respectivo metodo
        	post("/obj/:n", (request1,response1) ->{
        		Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        		JSONParser parser = new JSONParser();
        		String method = null;
				
        		request1.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
    					
    			try (InputStream input = request1.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                    String filename = getFileName(request1.raw().getPart("uploaded_file"));
                	Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);       
                    
                    //Directoria para guardar os ficheiros upload
            		Path destination = Paths.get("Customjar/" + filename);
                    Files.copy(tempFile, destination, StandardCopyOption.REPLACE_EXISTING);
                    
            		Object obj = parser.parse(new FileReader("Customjar/"+filename));
            		JSONObject jsonObject = (JSONObject) obj;
            		Files.delete(destination);

            		method = (String) jsonObject.get("Method");
                    tempFile.toFile().delete();
               
                    
                }
    			int index = Integer.parseInt(request1.params(":n"));
            	if(index < objarray.size()) {
            		return objarray.get(index).getMethod(method).invoke(objarray.get(index).newInstance());
            	}else {
            		return "no such object";
            	}
    			//return "You uploaded this file: " + getFileName(request1.raw().getPart("uploaded_file")) + " with method: " + method; 
        		
        	});

        	return "class " + newClass.getName()
			+ " loaded and stored on index = " + (objarray.size()-1);
        });

        get("/uploadclass", (req, res) ->
			  "<form method='post' enctype='multipart/form-data'>" // note the enctype
			+ "    <input type='file' name='uploaded_file' accept=''>" // make sure to call getPart using the same "name" in the post
			+ "    <button>Upload file</button>"
			+ "</form>"
		);
		
		post("/uploadclass", (req, res) -> {
		
			Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
					
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
					
			try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                
            	Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);       
                
                //Directoria para guardar os ficheiros upload
        		Path destination = Paths.get("Customjar/" + getFileName(req.raw().getPart("uploaded_file")));
                Files.copy(tempFile, destination, StandardCopyOption.REPLACE_EXISTING);
                tempFile.toFile().delete();
                
            }
			return "You uploaded this file: " + getFileName(req.raw().getPart("uploaded_file")) ;
					
		});
		
        get("createobject/:classname", (request,response) -> {
        	Class<?> newClass = DynamicRouteLoader.CustomLoader(request.params(":classname"));
        	objarray.add(newClass);
        	//pede upload do json com o metodo
        	get("/obj/:n", (request1,response1) ->{
            	int index = Integer.parseInt(request1.params(":n"));
            	if(index < objarray.size()) {
      			  return "<body>"+
      		    		"<h2>Please upload a json file with the method that you want to execute</h2>"+
      		    		"<hr>"
      		    		+"<form method='post' enctype='multipart/form-data'>" 
	        			+ "    <input type='file' name='uploaded_file' accept=''>" 
	        			+ "    <button>Upload file</button>"
	        			+ "</form>"
	      			    + "</body>";
            	}else {
            		return "no such object";
            	}
        	});
        	//le o metodo atraves do json e corre o respectivo metodo
        	post("/obj/:n", (request1,response1) ->{
        		Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        		JSONParser parser = new JSONParser();
        		String method = null;
				
        		request1.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
    					
    			try (InputStream input = request1.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                    String filename = getFileName(request1.raw().getPart("uploaded_file"));
                	Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);       
                    
                    //Directoria para guardar os ficheiros upload
            		Path destination = Paths.get("Customjar/" + filename);
                    Files.copy(tempFile, destination, StandardCopyOption.REPLACE_EXISTING);
                    
            		Object obj = parser.parse(new FileReader("Customjar/"+filename));
            		JSONObject jsonObject = (JSONObject) obj;
            		Files.delete(destination);

            		method = (String) jsonObject.get("Method");
                    tempFile.toFile().delete();
               
                    
                }
    			int index = Integer.parseInt(request1.params(":n"));
            	if(index < objarray.size()) {
            		return objarray.get(index).getMethod(method).invoke(objarray.get(index).newInstance());
            	}else {
            		return "no such object";
            	}
    			//return "You uploaded this file: " + getFileName(request1.raw().getPart("uploaded_file")) + " with method: " + method; 
        		
        	});
        	
        	return "class " + newClass.getName()
			+ " loaded and stored on index = " + (objarray.size()-1);
        });

    	get("/getclasslist", (request,response) -> {
    		String returnval = "";
    		for(int index = 0; index < objarray.size(); index ++) {
    			Class<?> cl = objarray.get(index);
    			Method[] methods = cl.getDeclaredMethods();
    			if(index == 0) {
    				returnval += "The server has this class (";
    			}else {
    				returnval += "<br>";
    				returnval += "and this class (";
    			}
    			returnval += cl.getName() + ") stored in index = " + index + "<br>";
    			returnval += "which has the following methods: " + "<br>";
    			for (int i = 0; i < methods.length; i++) {
    				returnval += methods[i] + "<br>";
    			}
//    			returnval += objarray.get(index).getMethods() + "<br>";
    		}
    		return returnval;
    	});

    	get("/classlist", (request,response) -> {
    		String returnval = "<!DOCTYPE html>"+
    		    	"<html>"+
    		    	"<head>"+
    		    	"<title>Here you have a list of the available classes</title>"+
    		    	"</head>"+
    		    	"<body>"+
    		    		"<h2>Here you have a list of the available classes</h2>"+
    		    		"<hr>";
    		for(int index = 0; index < objarray.size(); index ++) {
    			Class<?> cl = objarray.get(index);
    			
    			if(index == 0) {
    			}else {
    				returnval += "<br>";
    			}
    			
    			returnval += cl.getName() + " stored in index = " + index + "<br>";
    			returnval += "<form action=\"https://localhost:4567/getmethods/" + index + "\">"+
    	    		    "<input type=\"submit\" value=\"Go to the methods from this class\" />" + 
    	    		    "</form>";
    		}
    		return returnval + "</body>"+
    		    	"</html>";
    	});
    	
    	get("/getmethods/:index", (request,response) -> {
    		if(Integer.parseInt(request.params(":index")) >= objarray.size()) {
    			return "no such object";
        	}
    		String returnval ="The class has the following methods: <br>";
    		Method[] methods = objarray.get(Integer.parseInt(request.params(":index"))).getDeclaredMethods();
    		for (int i = 0; i < methods.length; i++) {
				returnval += methods[i] + "<br>";
			}
    		
    		return returnval;
    	});
    	/*}
    	"<!DOCTYPE html>"+
    	"<html>"+
    	"<head>"+
    	"<title>Here you have a list of the available classes</title>"+
    	"</head>"+
    	"<body>"+
    		"<h2>Here you have a list of the available classes</h2>"+
    		"<hr>"+
    	"</body>"+
    	"</html>");*/
    }


	private static Object Date(long creationTime) {
		Date date = new Date(creationTime);
		return date;
	}
	private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
