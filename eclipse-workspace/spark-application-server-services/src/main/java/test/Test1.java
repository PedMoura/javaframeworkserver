package test;

public class Test1 {
	public String id = "2";

	/*
  public void hello() {
        System.out.println("Hello !");
  }
 */
	public static boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }
    
    public static void echo(String id) {
    	
    	if(isNumeric(id)) {
    		System.out.println("Hello " + id + "!");
    	}else {
    		System.out.println("Hello s!");
    	}
    }
    
    public static void dynamic() {
    	
		System.out.println("Change me!");
    	
    }
}
