package com.famenu.qrcodeBinder.pApi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.famenu.qrcodeBinder.QrBinderDatabase;

public class TagsManagmentServlet extends HttpServlet {

	private static final long serialVersionUID = -3358814214643876334L;

	final static String P_KEY="pkey";
	final static String P_KEY_VALUE="giorgioadoraannusarescoiattoliindecomposizione";

	final static String PARAM_TAGS_QUANTITY="tquantity";

	private HashIdGenerator generator;
	private QrBinderDatabase database;
	
	public TagsManagmentServlet(QrBinderDatabase database){
		this.generator=new HashIdGenerator();
	}
	
	private boolean validate(HttpServletRequest req){
		return P_KEY_VALUE.equals(req.getHeader(P_KEY));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(!validate(req)){
			resp.sendError(401, "Pussa Via. chi minchia sei?");
			return;
		}
		
		String numTags=req.getParameter(PARAM_TAGS_QUANTITY);
		if(numTags==null){
			return;
		}
		
		int tags_requested=Integer.parseInt(numTags);
		
		
		
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}


}
