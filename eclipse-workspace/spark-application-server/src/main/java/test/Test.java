package test;

import java.io.IOException;
import java.io.InputStream;

public class Test {

    static class TestClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals("test.Test1")) {
                try {
                    InputStream is = Test.class.getClassLoader().getResourceAsStream("test/Test1.class");
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

    public static void main(String[] args) throws Exception {
        for (;;) {
            Class cls = new TestClassLoader().loadClass("test.Test1");
            Object obj = cls.newInstance();
            cls.getMethod("Echo").invoke(obj);
            Thread.sleep(5000);
        }
    }
}