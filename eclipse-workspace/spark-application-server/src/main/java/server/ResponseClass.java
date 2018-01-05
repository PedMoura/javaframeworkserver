package server;

public class ResponseClass {

    public static boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }
    
    public static String Echo(String id) {
    	if(isNumeric(id)) {
    		return "user with id number " + id;
    	}else {
    		return "Echo " + id;
    	}
    }
}
