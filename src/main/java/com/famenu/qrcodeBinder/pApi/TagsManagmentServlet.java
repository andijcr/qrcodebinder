package com.famenu.qrcodeBinder.pApi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.famenu.qrcodeBinder.QrBinderDatabase;
import com.famenu.qrcodeBinder.QrBinderDatabase.CatastroficStorageException;
import com.famenu.qrcodeBinder.QrBinderDatabase.DepletedStorageException;
import com.famenu.qrcodeBinder.pApi.HashIdGenerator.BoundariesExcededException;

public class TagsManagmentServlet extends HttpServlet {

	private static final long serialVersionUID = -3358814214643876334L;

	final static String P_KEY="pkey";
	final static String P_KEY_VALUE="giorgioadoraannusarescoiattoliindecomposizione";

	final static String PARAM_TAGS_QUANTITY="tquantity";
	final static String PARAM_TAG="ttag";
	final static String PARAM_URL="turl";

	final static String OK="ok";
	final static String NO="no";

	public class KeyValidationException extends Exception{};

	private HashIdGenerator generator;
	private QrBinderDatabase database;

	public TagsManagmentServlet(QrBinderDatabase database){
		this.generator=new HashIdGenerator();
		this.database=database;
	}

	private void validateOrDie(HttpServletRequest req) throws KeyValidationException{
		if(!P_KEY_VALUE.equals(req.getHeader(P_KEY)))
			throw new KeyValidationException();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			validateOrDie(req);

			int tags_requested;
			String numTags=req.getParameter(PARAM_TAGS_QUANTITY);
			tags_requested=Integer.parseInt(numTags);

			int start;
			start=database.getLastHashId();

			ArrayList<String> tags=new ArrayList<String>(tags_requested);
			for(int i=0;i<tags_requested ; i++){
				start++; //lastHashId salva dove siamo arrivati l'ultima volta
				tags.add(generator.translate(start));
			}

			database.insertNewTags(tags);
			database.insertLastHashId(start);

			PrintWriter out=resp.getWriter();

			for(String t:tags){
				out.println(t);
			}

		} catch (KeyValidationException e){ 
			resp.sendError(401, "Pussa Via. chi minchia sei?");
		} catch (NumberFormatException e){
			resp.sendError(400, PARAM_TAGS_QUANTITY + " deve essere un int positivo. cojone.");
		} catch (CatastroficStorageException e){
			resp.sendError(500, "Le scimmie stanno gia al lavoro su db");
		} catch (BoundariesExcededException e) {
			resp.sendError(500, "FFFfffffUuuuuuu ho finito i numeri!");
		} catch (DepletedStorageException e) {
			resp.sendError(500, "Cacchio, credo che abbiamo finito lo spazio!");
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			validateOrDie(req);

			String tag=req.getParameter(PARAM_TAG), url=req.getParameter(PARAM_URL);
			if(tag!=null && url!=null && database.isTagAssociable(tag)){

				url=URLDecoder.decode(url, "ISO-8859-1");

				if(!url.startsWith("http://")){
					if(!url.startsWith("/")){
						url="http://".concat(url);
					}else{
						url="http:/".concat(url);
					}
				}

				database.associateTagUrl(tag, url);

				resp.getWriter().println(OK);
			}else{
				resp.getWriter().println(NO);
			}
		} catch (KeyValidationException e){ 
			resp.sendError(401, "Pussa Via. chi minchia sei?");
		} catch (CatastroficStorageException e){
			resp.sendError(500, "Le scimmie stanno gia al lavoro su db");
		} catch ( UnsupportedEncodingException e){
			resp.sendError(500, "aaaaaa!");
		}

	}


}
