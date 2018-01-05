package java;

import route.OutputController;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class Server {
    public static void main(String[] args) throws Exception {

        ClassReload.setJar("/home/pedro/Desktop/classloader/java-spark-dynamic-reload/target/ServerClassReload-1.0-SNAPSHOT.jar");

        Spark.get("/", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                return ClassReload.response(OutputController.class.getCanonicalName());
            }
        });

        Spark.get("/test", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                return ClassReload.response("route.NewOutputController");
            }
        });

    }

}
