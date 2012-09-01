package com.famenu.qrcodeBinder;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;

@SuppressWarnings("serial")
public class SimplePing extends HttpServlet {

	String scope;
	
	public SimplePing(String scope){
		this.scope=scope;
	}
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.getWriter().print("Server alive from " + 
        						req.getLocalAddr() + 
        						"!\n" +
        						"the relative path is "+
        						req.getRequestURI() +
        						"\n" +
        						"my scope is " + this.scope +
        						"\n" +
        						System.getProperty("user.dir"));
    }

}
