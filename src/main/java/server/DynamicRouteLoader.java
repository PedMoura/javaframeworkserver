package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DynamicRouteLoader {
	
	static class TestClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
        	System.out.println("-------------------------------------loadclass---------------------------");
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
            if (name.equals("loadfromjar.Class1")) {
                try {
                    InputStream is = DynamicRouteLoader.class.getClassLoader().getResourceAsStream("loadfromjar/Class1.class");
                    byte[] buf = new byte[10000];
                    int len = is.read(buf);
                    return defineClass(name, buf, 0, len);
                } catch (IOException e) {
                    throw new ClassNotFoundException("", e);
                }
            }
            return getParent().loadClass(name);
        }
        public Class<?> loadCustomClass(String name) throws ClassNotFoundException, Exception {
			String pathToJar = null;
        	if(name == "Serverjar.jar") {
        		pathToJar = "/home/pedro/eclipse-workspace/sparkframeworkserver2/Userjar/" + name;
        	}else {
        		pathToJar = "/home/pedro/eclipse-workspace/sparkframeworkserver2/Customjar/" + name;
        	}

        	
			JarFile jarFile = new JarFile(pathToJar);
        	Enumeration<JarEntry> e = jarFile.entries();
        	Class<?> c = null;
        	
        	URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        	
        	URLClassLoader cl = URLClassLoader.newInstance(urls);
     
        	while (e.hasMoreElements()) {
        	    JarEntry je = e.nextElement();
        	    if(je.isDirectory() || !je.getName().endsWith(".class")){
        	        continue;
        	    }
        	    // -6 because of .class
        	    
        	    String className = je.getName().substring(0,je.getName().length()-6);
        	    className = className.replace('/', '.');
        	    System.out.println("path1 = " + pathToJar);
        	    c = cl.loadClass(className);
        	    System.out.println("path2 = " + pathToJar);
        	    break;
        	}
        	jarFile.close();
        	
        	//if(name == "Serverjar.jar") {
	        	File file = new File(pathToJar);
	            
	            if(file.delete())
	            {
	                System.out.println("File deleted successfully");
	            }
	            else
	            {
	                System.out.println("Failed to delete the file");
	            }
        	//}

        	return c;
        }
    }
	
	//public static Object loader() throws Exception {
	public static Class<?> Loader(String classtoload) throws Exception {
	    Class<?> cls;
	    
		if(classtoload == "ResponseClass") {
			cls = new TestClassLoader().loadClass("server.ResponseClass");
		}else {
			if(classtoload == "loadfromjar.Class1") {
				cls = new TestClassLoader().loadClass(classtoload);
			}else {
				cls = new TestClassLoader().loadClass("");
			}
		}
	    System.out.println("---------------------------------------------loaded method------------------------------------");
	    return cls;
	    
	}
	public static Class<?> CustomLoader(String classtoload) throws Exception {
	    Class<?> cls = null;
	    
		cls = new TestClassLoader().loadCustomClass(classtoload);
		
	    System.out.println("---------------------------------------------loaded custom method------------------------------------");
	    return cls;
	    
	}
}

