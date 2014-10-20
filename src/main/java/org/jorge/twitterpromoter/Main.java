package org.jorge.twitterpromoter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jorge.twitterpromoter.io.Ticker;

public class Main {

    /**
     * @param args The args to the main method
     */
    public static void main(String[] args) throws Exception {
        String webPort = System.getenv("PORT");
        if ((webPort == null) || webPort.isEmpty()) {
            webPort = "8080";
        }

        Server server = new Server(Integer.valueOf(webPort));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        System.out.print("Requesting server start...");
        server.start();
        System.out.println("done.");

        System.out.println("Before initing");
        Ticker.getInstance().init();
        System.out.println("After initing");

        server.join();
    }
}
