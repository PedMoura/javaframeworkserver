package server;

import java.util.ArrayList;
import java.util.List;

public class ResponseClass {

	static List<String> userlist = new ArrayList<String>();
	
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
    	return "Modificado!!!!!!";
    }

    public static void adduser(String username) {
    	userlist.add(username);
    	return;
    }
    
    public static String getuserlist(String username) {
    	
    	String returnvalue = "Users that accessed this website: ";
    	if(userlist.contains(username) ){
    		for(int i=0; i<userlist.size(); i++){
                returnvalue += userlist.get(i);
                if(userlist.size() > 1 && i < userlist.size()-1) {
             	   returnvalue += ", ";
                }
            }
    	}else {
    		returnvalue = "Sorry " + username + " you are not authorized" ;
    	}
		return returnvalue;
    }
}
