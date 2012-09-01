package com.famenu.qrcodeBinder;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.famenu.qrcodeBinder.QrBinderDatabase.CatastroficStorageException;

public class TagToUrlRedirectServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6520854327193777727L;
	private static final String DEFAULT_REDIRECT = "http://google.com";

	private QrBinderDatabase database;

	public TagToUrlRedirectServlet(QrBinderDatabase database) {
		this.database=database;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try{		
			String tag=req.getRequestURI().substring(1); 		// strip away the first slash

			String url=null;
			url=database.getUrlFromTag(tag);

			if(url==null){
				url=DEFAULT_REDIRECT;
			}

			resp.sendRedirect(resp.encodeRedirectURL(url));
		} catch (CatastroficStorageException e) {
			resp.sendError(500, "uh hu. Chiamate le Scimmie! il db sta fumando.");
		}

	}

}
