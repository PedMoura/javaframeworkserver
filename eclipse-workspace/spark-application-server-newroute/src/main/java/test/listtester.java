package test;

import java.util.ArrayList;
import java.util.List;

public class listtester {

	static List<String> userlist = new ArrayList<String>();

	public static void main(String[] args){
		userlist.add(0, "pedro");
		System.out.println(userlist.get(0));
	}
}
