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

import server.DynamicRouteLoader.TestClassLoader;

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
        		pathToJar = "Userjar/" + name;
        	}else {
        		pathToJar = "Customjar/" + name;
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
	    Class<?> cls = DynamicRouteLoader.class;
	    cls = new TestClassLoader().loadCustomClass(classtoload);
		URL[] urls={ cls.getProtectionDomain().getCodeSource().getLocation() };
		ClassLoader delegateParent = cls.getClassLoader().getParent();
		try(URLClassLoader cl=new URLClassLoader(urls, delegateParent)) {
			Class<?> reloaded = cl.loadClass(cls.getName());
		    System.out.println("---------------------------------------------loaded custom method------------------------------------");
		    return reloaded;
		}
	    
	}
}

