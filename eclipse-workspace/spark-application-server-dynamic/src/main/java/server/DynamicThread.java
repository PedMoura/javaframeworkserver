package server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import test.Test;

public class DynamicThread extends Thread{

    static class TestClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals("server.ResponseClass")) {
                try {
                    InputStream is = DynamicThread.class.getClassLoader().getResourceAsStream("server/ResponseClass.class");
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
    
	public void run(){
		for(;;) {
	        Class cls = null;
			try {
				cls = new TestClassLoader().loadClass("server.ResponseClass");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	        Object obj = null;
			try {
				obj = cls.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				cls.getMethod("Dynamic").invoke(obj);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        System.out.println("---------------------------------------------------------------------Loaded----------------------------------------");
		}
	}
}
