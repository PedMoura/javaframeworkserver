package test;

public class Test1 {
	public String id = "2";

	/*
  public void hello() {
        System.out.println("Hello !");
  }
 */
	public boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }
    
    public void Echo() {
    	if(isNumeric(id)) {
    		System.out.println("Hello 2!");
    	}else {
    		System.out.println("Hello s!");
    	}
    }
}
