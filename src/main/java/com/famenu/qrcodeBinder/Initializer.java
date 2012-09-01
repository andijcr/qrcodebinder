package com.famenu.qrcodeBinder;

import java.net.URI;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.famenu.qrcodeBinder.pApi.TagsManagmentServlet;

public class Initializer {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {

		QrBinderDatabase database=new QrBinderDatabase(new URI(System.getenv("DATABASE_URL")));
		database.initSchema();
		
		ServletContextHandler broad = new ServletContextHandler(ServletContextHandler.SESSIONS);
        	broad.setContextPath("/");
//        	broad.addServlet(new ServletHolder(new IndexServlet()),"/index");
        	broad.addServlet(new ServletHolder(new SimplePing("/ping")),"/ping");
        	broad.addServlet(new ServletHolder(new TagToUrlRedirectServlet(database)),"/*");
        	
    	ServletContextHandler privateApi = new ServletContextHandler(ServletContextHandler.SESSIONS);
        	privateApi.setContextPath("/papi");
//        	privateApi.addServlet(new ServletHolder(new SimplePing("/tags")),"/tags");
        	privateApi.addServlet(new ServletHolder(new TagsManagmentServlet(database)), "/tags");
        
        ContextHandlerCollection contexts=new ContextHandlerCollection(); 
        	contexts.setHandlers(new Handler[]{broad, privateApi});

        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        server.setHandler(contexts);
        server.start();
        server.join();   
        }

}
