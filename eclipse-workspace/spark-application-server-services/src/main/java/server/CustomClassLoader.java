package server;

import test.Test;

import java.io.IOException;
import java.io.InputStream;

public class CustomClassLoader {

	static class TestClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals("server.dynamic")) {
                try {
                    InputStream is = CustomClassLoader.class.getClassLoader().getResourceAsStream("server/ResponseClass.class");
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
}
