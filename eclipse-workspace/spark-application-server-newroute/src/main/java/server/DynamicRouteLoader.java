package server;

import java.io.IOException;
import java.io.InputStream;

public class DynamicRouteLoader {
	
	static class TestClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals("server.ResponseClass")) {
                try {
                    InputStream is = DynamicRouteLoader.class.getClassLoader().getResourceAsStream("server/ResponseClass.class");
                    byte[] buf = new byte[10000];
                    int len = is.read(buf);
                    return defineClass(name, buf, 0, len);
                } catch (IOException e) {
                    throw new ClassNotFoundException("", e);
                }
            }
            return getParent().loadClass(name);
        }
    }
	
	//public static Object loader() throws Exception {
	public static Class<?> loader() throws Exception {
	    Class<?> cls = new TestClassLoader().loadClass("server.ResponseClass");
	    //Object obj = cls.newInstance();
	    //Object temp = cls.getMethod("Dynamic").invoke(obj);
	    System.out.println("---------------------------------------------loaded Dynamic method------------------------------------");
	    //return temp;
	    return cls;
	    
	}
}

