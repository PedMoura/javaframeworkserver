package server;

public class ResponseClass {

	
	public boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }
    
    public String Echo(String id) {

    	if(isNumeric(id)) {
    		return "user with id number " + id ;
    	}else {
    		return "Echo " + id;
    	}
    }
    
    public static String Dynamic(){
    	return "Change me!!!!!!!!!!!!!!!!!!!!!!!!";
    }

}
