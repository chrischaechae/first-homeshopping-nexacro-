package com.board.controller;

import com.board.VO.BoardVO;
import com.board.VO.ChoiceVO;
import com.board.VO.ClientVO;
import com.board.VO.ImgVO;
import com.board.VO.OrderingVO;
import com.board.VO.ProductVO;
import com.board.dao.BoardDao;
import com.board.paging.Paging;
import com.nexacro.xapi.data.ColumnHeader;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.DataTypes;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.VariableList;
import com.nexacro.xapi.tx.HttpPlatformRequest;
import com.nexacro.xapi.tx.HttpPlatformResponse;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformType;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ListController {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(getClass());
	private int pageSize = 5;;
	private int blockCount = 10;
		
	@Autowired
	private BoardDao boardDao;

	@RequestMapping({ "/board/list.do" })
	public void board(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
			@RequestParam(value = "keyField", defaultValue = "") String keyField,
			@RequestParam(value = "keyWord", defaultValue = "") String keyWord) throws PlatformException{
		
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		System.out.println(keyWord);
		System.out.println(keyField);
					// DataSet
					DataSet ds = new DataSet("product");			
				    
				    // DataSet Column setting
					ds.addColumn("no", DataTypes.INT, 256);
					ds.addColumn("pro_no", DataTypes.INT, 256);
   				    ds.addColumn("pro_name", DataTypes.STRING, 256);
				    ds.addColumn("pro_price", DataTypes.STRING,  256);
				    ds.addColumn("pro_img", DataTypes.STRING,  700);
				    
					// DAO
					ProductVO productVO;
					HashMap<String, Object> map = new HashMap();
					String pagingHtml = "";
					map.put("keyField", keyField);
					map.put("keyWord", keyWord);
					
					int count = this.boardDao.getCount(map);					
									
				   // strErrorMsg  = 0;
					List<ProductVO> list = null;
					list=this.boardDao.list(map);
				    
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   productVO = new ProductVO();
					   productVO = list.get(i);
					    
					   ds.set(row, "no", i+1);
					   ds.set(row, "pro_no", productVO.getPro_no());
					   	ds.set(row, "pro_name", productVO.getPro_name());
					    ds.set(row, "pro_price", productVO.getPro_price());
					    ds.set(row, "pro_img", productVO.getPro_img());			
				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	
	@RequestMapping({ "/board/addproduct.do" })
	public void addproduct(HttpServletRequest request, HttpServletResponse response) throws PlatformException{
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					DataSet ds = pdata.getDataSet("addproduct");
					
					// DAO
					ProductVO productVO;
					productVO = new ProductVO();
					
					productVO.setPro_name(ds.getString(0, "pro_name"));
					productVO.setPro_price(ds.getInt(0, "pro_price"));
					productVO.setPro_detail(ds.getString(0, "pro_detail")); 
												
					boardDao.addproduct(productVO);
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
		
	}
	@RequestMapping({ "/board/upload.do" })
	public void upload(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_name",required=false) String pro_name) throws PlatformException, UnsupportedEncodingException{
	String chkType = request.getHeader("Content-Type");
	//System.out.println(chkType);
	if( chkType == null )
	 return;
	request.setCharacterEncoding("utf-8");
	String contextRealPath = request.getSession().getServletContext().getRealPath("/");
	
	String PATH = request.getParameter("PATH");
	String savePath = contextRealPath + PATH;
	int maxSize = 500 * 1024 * 1024; // 최대 업로드 파일 크기 500MB(메가)로 제한
	PlatformData resData = new PlatformData();
	VariableList resVarList = resData.getVariableList();
	String sMsg = " A ";
	try {
	 
	 MultipartRequest multi = new MultipartRequest(request, savePath, maxSize, "utf-8", new DefaultFileRenamePolicy());
	 Enumeration files = multi.getFileNames(); // 파일명 모두 얻기
	 
	 sMsg += "B ";
	 DataSet ds = new DataSet("Dataset00");
	 
	 ds.addColumn(new ColumnHeader("fileName", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileSize", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileType", DataTypes.STRING));
	 
	 sMsg += "C ";
	 String fileName="";
	 
	 String orisFName="";
	 String gdpath="";
	 //while (files.hasMoreElements()) {
	  sMsg += "D ";
	  String name = (String)files.nextElement();
	  fileName= multi.getFilesystemName(name);//파일이름
	
	  orisFName=multi.getOriginalFileName(name);
	  String type = multi.getContentType(name);
	  File f = multi.getFile(name);
	  int row = ds.newRow();
	  ds.set(row, "fileName", fileName);
	  ds.set(row, "fileType", type);
	  
	  HashMap<String, Object> map = new HashMap();
		map.put("pro_name",pro_name);
		map.put("oriimg",fileName);
		boardDao.uploadimg(map);
	  if (f != null)
	  {
	   String size = Long.toString(f.length()/1024)+"KB";
	   ds.set(row, "fileSize", size);
	  }  
	  sMsg += row +" ";
	 //}
	 resData.addDataSet(ds);
	 resVarList.add("ErrorCode", 200);
	 //resVarList.add("ErrorMsg", savePath+"/"+fileName);
	 resVarList.add("ErrorMsg", fileName);
	} catch (Exception e) {
	 resVarList.add("ErrorCode", -1);
	 resVarList.add("ErrorMsg", sMsg + " " + e);
	}
	HttpPlatformResponse res = new HttpPlatformResponse(response);
	res.setData(resData);
	res.sendData();
	
	}
	@RequestMapping({ "/board/iupload.do" })
	public void iupload(HttpServletRequest request, HttpServletResponse response) throws PlatformException, UnsupportedEncodingException{
	String chkType = request.getHeader("Content-Type");
	//System.out.println(chkType);
	if( chkType == null )
	 return;
	request.setCharacterEncoding("utf-8");
	String contextRealPath = request.getSession().getServletContext().getRealPath("/");
	
	String PATH = request.getParameter("PATH");
	String savePath = contextRealPath + PATH;
	int maxSize = 500 * 1024 * 1024; // 최대 업로드 파일 크기 500MB(메가)로 제한
	PlatformData resData = new PlatformData();
	VariableList resVarList = resData.getVariableList();
	String sMsg = " A ";
	try {
	 
	 MultipartRequest multi = new MultipartRequest(request, savePath, maxSize, "utf-8", new DefaultFileRenamePolicy());
	 Enumeration files = multi.getFileNames(); // 파일명 모두 얻기
	 
	 sMsg += "B ";
	 DataSet ds = new DataSet("Dataset00");
	 
	 ds.addColumn(new ColumnHeader("fileName", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileSize", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileType", DataTypes.STRING));
	 
	 sMsg += "C ";
	 String fileName="";
	 
	 String orisFName="";
	 String gdpath="";
	 //while (files.hasMoreElements()) {
	  sMsg += "D ";
	  String name = (String)files.nextElement();
	  fileName= multi.getFilesystemName(name);//파일이름
	  orisFName=multi.getOriginalFileName(name);
	  String type = multi.getContentType(name);
	  File f = multi.getFile(name);
	  int row = ds.newRow();
	  ds.set(row, "fileName", fileName);
	  ds.set(row, "fileType", type);
	  
	  HashMap<String, Object> map = new HashMap();
		map.put("img_name",fileName);
		boardDao.imgupload(map);
	  if (f != null)
	  {
	   String size = Long.toString(f.length()/1024)+"KB";
	   ds.set(row, "fileSize", size);
	  }  
	  sMsg += row +" ";
	 //}
	 resData.addDataSet(ds);
	 resVarList.add("ErrorCode", 200);
	 //resVarList.add("ErrorMsg", savePath+"/"+fileName);
	 resVarList.add("ErrorMsg", fileName);
	} catch (Exception e) {
	 resVarList.add("ErrorCode", -1);
	 resVarList.add("ErrorMsg", sMsg + " " + e);
	}
	HttpPlatformResponse res = new HttpPlatformResponse(response);
	res.setData(resData);
	res.sendData();
	
	}
	@RequestMapping({ "/board/pro_detail.do" })
	public void detail(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("dproduct");			
		
	    // DataSet Column setting
	    ds.addColumn("pro_no", DataTypes.INT,  256);
	    ds.addColumn("pro_name", DataTypes.STRING,  256);
	    ds.addColumn("pro_price", DataTypes.INT, 256);
	    ds.addColumn("pro_detail", DataTypes.STRING, 600);
	    ds.addColumn("pro_img", DataTypes.STRING, 600);
	    
		
		ProductVO bean=null;
		
		bean=boardDao.pro_detail(gpro_no);
		
		int row = ds.newRow();
		
		
	    ds.set(row, "pro_no", bean.getPro_no());
	    ds.set(row, "pro_name", bean.getPro_name());
	    ds.set(row, "pro_price",bean.getPro_price());
	    ds.set(row, "pro_detail",bean.getPro_detail());
	    ds.set(row, "pro_img",bean.getPro_img());
	    
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}	
	@RequestMapping(value="/board/pro_edit.do")
	public void edit(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws IOException, PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("eproduct");			
	    
	    // DataSet Column setting
	    ds.addColumn("pro_no", DataTypes.INT,  256);
	    ds.addColumn("pro_name", DataTypes.STRING,  256);
	    ds.addColumn("pro_price", DataTypes.INT, 256);
	    ds.addColumn("pro_detail", DataTypes.STRING, 600);
	    ds.addColumn("pro_img", DataTypes.STRING, 600);
		ProductVO bean=null;
		
		bean=boardDao.pro_detail(gpro_no);
		
		int row = ds.newRow();
			
		ds.set(row, "pro_no", bean.getPro_no());
	    ds.set(row, "pro_name", bean.getPro_name());
	    ds.set(row, "pro_price",bean.getPro_price());
	    ds.set(row, "pro_detail",bean.getPro_detail());
	    ds.set(row, "pro_img",bean.getPro_img());
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping(value="/board/delpro.do")
	public void delpro(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws PlatformException, IOException{
		PlatformData pdata = new PlatformData();
		int nErrorCode = 0;
		String strErrorMsg = "START";
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
				
					boardDao.delpro(gpro_no);
	
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				   	
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
	    
		}
	@RequestMapping(value="/board/dimg.do")
	public void dimg(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws IOException, PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("dimg");			
	    
	    // DataSet Column setting
	    ds.addColumn("img_name", DataTypes.STRING,  256);
	   
		ImgVO bean=null;
		
		bean=boardDao.dimg(gpro_no);
		
		int row = ds.newRow();
			
		ds.set(row, "img_name", bean.getImg_name());
	   
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/pro_update.do" })
	public void pro_update(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws PlatformException{
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					DataSet ds = pdata.getDataSet("eproduct");
					
					// DAO
					ProductVO productVO;
					productVO = new ProductVO();
					
					productVO.setPro_no(gpro_no);
					productVO.setPro_name(ds.getString(0, "pro_name"));
					productVO.setPro_price(ds.getInt(0, "pro_price"));
					productVO.setPro_detail(ds.getString(0, "pro_detail")); 
												
					boardDao.pro_update(productVO);
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
		
	}
	@RequestMapping(value="/board/edit_img.do")
	public void edit_img(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws IOException, PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("img");			
	    
	    // DataSet Column setting
	    ds.addColumn("pro_no", DataTypes.INT,  256);
	    ds.addColumn("pro_img", DataTypes.STRING, 600);
		ProductVO bean=null;
		
		bean=boardDao.pro_detail(gpro_no);
		
		int row = ds.newRow();
			
		ds.set(row, "pro_no", bean.getPro_no());
	    ds.set(row, "pro_img",bean.getPro_img());
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping(value="/board/edit_img1.do")
	public void edit_img1(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws IOException, PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("img1");			
	    
	    // DataSet Column setting
	    ds.addColumn("pro_no", DataTypes.INT,  256);
	    ds.addColumn("img_name", DataTypes.STRING, 600);
		ImgVO bean=null;
		
		bean=boardDao.dimg(gpro_no);
		
		int row = ds.newRow();
			
		ds.set(row, "pro_no", bean.getPro_no());
	    ds.set(row, "img_name",bean.getImg_name());
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/reupload.do" })
	public void reupload(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) String pro_no) throws PlatformException, UnsupportedEncodingException{
	String chkType = request.getHeader("Content-Type");
	//System.out.println(chkType);
	if( chkType == null )
	 return;
	request.setCharacterEncoding("utf-8");
	String contextRealPath = request.getSession().getServletContext().getRealPath("/");
	
	String PATH = request.getParameter("PATH");
	String savePath = contextRealPath + PATH;
	int maxSize = 500 * 1024 * 1024; // 최대 업로드 파일 크기 500MB(메가)로 제한
	PlatformData resData = new PlatformData();
	VariableList resVarList = resData.getVariableList();
	String sMsg = " A ";
	try {
	 
	 MultipartRequest multi = new MultipartRequest(request, savePath, maxSize, "utf-8", new DefaultFileRenamePolicy());
	 Enumeration files = multi.getFileNames(); // 파일명 모두 얻기

	 
	 sMsg += "B ";
	 DataSet ds = new DataSet("Dataset00");
	 
	 ds.addColumn(new ColumnHeader("fileName", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileSize", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileType", DataTypes.STRING));
	 
	 sMsg += "C ";
	 String fileName="";
	 String orisFName="";
	 String gdpath="";
	 while (files.hasMoreElements()) {
	  sMsg += "D ";
	  String name = (String)files.nextElement();
	  String webpath="http://localhost:8078/nexacro/upload/";
	  fileName= multi.getFilesystemName(name);//파일이름
	  orisFName=multi.getOriginalFileName(name);
	  String type = multi.getContentType(name);
	  File f = multi.getFile(name);
	  int row = ds.newRow();
	  ds.set(row, "fileName", fileName);
	  ds.set(row, "fileType", type);
	  gdpath=webpath+fileName;
	  
	  
	  HashMap<String, Object> map = new HashMap();
		map.put("pro_no",pro_no);
		map.put("oriimg",fileName);
		boardDao.reuploadimg(map);
	  if (f != null)
	  {
	   String size = Long.toString(f.length()/1024)+"KB";
	   ds.set(row, "fileSize", size);
	  }  
	  sMsg += row +" ";
	 }
	 resData.addDataSet(ds);
	 resVarList.add("ErrorCode", 200);
	 //resVarList.add("ErrorMsg", savePath+"/"+fileName);
	 resVarList.add("ErrorMsg", fileName);
	} catch (Exception e) {
	 resVarList.add("ErrorCode", -1);
	 resVarList.add("ErrorMsg", sMsg + " " + e);
	}
	HttpPlatformResponse res = new HttpPlatformResponse(response);
	res.setData(resData);
	res.sendData();
	
	}
	@RequestMapping({ "/board/rreupload.do" })
	public void rreupload(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) String pro_no) throws PlatformException, UnsupportedEncodingException{
	String chkType = request.getHeader("Content-Type");
	//System.out.println(chkType);
	if( chkType == null )
	 return;
	request.setCharacterEncoding("utf-8");
	String contextRealPath = request.getSession().getServletContext().getRealPath("/");
	
	String PATH = request.getParameter("PATH");
	String savePath = contextRealPath + PATH;
	int maxSize = 500 * 1024 * 1024; // 최대 업로드 파일 크기 500MB(메가)로 제한
	PlatformData resData = new PlatformData();
	VariableList resVarList = resData.getVariableList();
	String sMsg = " A ";
	try {
	 
	 MultipartRequest multi = new MultipartRequest(request, savePath, maxSize, "utf-8", new DefaultFileRenamePolicy());
	 Enumeration files = multi.getFileNames(); // 파일명 모두 얻기

	 
	 sMsg += "B ";
	 DataSet ds = new DataSet("Dataset00");
	 
	 ds.addColumn(new ColumnHeader("fileName", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileSize", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileType", DataTypes.STRING));
	 
	 sMsg += "C ";
	 String fileName="";
	 String orisFName="";
	 String gdpath="";
	 while (files.hasMoreElements()) {
	  sMsg += "D ";
	  String name = (String)files.nextElement();
	  String webpath="http://localhost:8078/nexacro/upload/";
	  fileName= multi.getFilesystemName(name);//파일이름
	  orisFName=multi.getOriginalFileName(name);
	  String type = multi.getContentType(name);
	  File f = multi.getFile(name);
	  int row = ds.newRow();
	  ds.set(row, "fileName", fileName);
	  ds.set(row, "fileType", type);
	  gdpath=webpath+fileName;
	  
	  
	  HashMap<String, Object> map = new HashMap();
		map.put("pro_no",pro_no);
		map.put("img_name",fileName);
		boardDao.rreuploadimg(map);
	  if (f != null)
	  {
	   String size = Long.toString(f.length()/1024)+"KB";
	   ds.set(row, "fileSize", size);
	  }  
	  sMsg += row +" ";
	 }
	 resData.addDataSet(ds);
	 resVarList.add("ErrorCode", 200);
	 //resVarList.add("ErrorMsg", savePath+"/"+fileName);
	 resVarList.add("ErrorMsg", fileName);
	} catch (Exception e) {
	 resVarList.add("ErrorCode", -1);
	 resVarList.add("ErrorMsg", sMsg + " " + e);
	}
	HttpPlatformResponse res = new HttpPlatformResponse(response);
	res.setData(resData);
	res.sendData();
	
	}
	@RequestMapping(value="/board/delfile.do",method=RequestMethod.POST)
	public void delfile(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="delfile",required=false) String pro_img) throws PlatformException, IOException{
		PlatformData pdata = new PlatformData();
		String pro_img1=new String(pro_img.getBytes("ISO-8859-1"),"utf-8");
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					String folder="C:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\admin\\upload\\";
					String file=folder+pro_img1;
					File delfile=new File(file);
					
						if(delfile.exists()){
							delfile.delete();
						}
						
	
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				   	
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
	    
		}
	@RequestMapping({ "/board/choicelist.do" })
	public void choicelist(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws PlatformException{
		
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					// DataSet
					DataSet ds = new DataSet("choice");			
				    
				    // DataSet Column setting
					ds.addColumn("choice_no", DataTypes.INT, 256);
				    ds.addColumn("choice_size", DataTypes.STRING,  700);
				    ds.addColumn("choice_color", DataTypes.STRING,  700);
				    ds.addColumn("choice_stock", DataTypes.INT,  700);
				    
					// DAO
					ChoiceVO choiceVO;
					HashMap<String, Object> map = new HashMap();
					map.put("pro_no",gpro_no);
				   // strErrorMsg  = 0;
					List<ChoiceVO> list = null;
					list=this.boardDao.choicelist(map);
				   				   
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   choiceVO = new ChoiceVO();
					   choiceVO = list.get(i);
					    
						ds.set(row, "choice_color", choiceVO.getChoice_color());
					   	ds.set(row, "choice_no", choiceVO.getChoice_no());
					    ds.set(row, "choice_size", choiceVO.getChoice_size());
					    ds.set(row, "choice_stock", choiceVO.getChoice_stock());
					   
				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/addchoice.do" })
	public void addchoice(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="pro_no",required=false) int gpro_no) throws PlatformException{
		System.out.println("con");
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					DataSet ds = pdata.getDataSet("addchoice");
					
					// DAO
					ChoiceVO choiceVO;
					choiceVO = new ChoiceVO();
					
					choiceVO.setPro_no(gpro_no);
					choiceVO.setChoice_size(ds.getString(0, "choice_size"));;
					choiceVO.setChoice_color(ds.getString(0, "choice_color"));
					choiceVO.setChoice_stock(ds.getInt(0, "choice_stock")); 
												
					boardDao.addchoice(choiceVO);
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
		
	}
	@RequestMapping({"/board/editchoice.do"})
	public void choice_edit(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="choice_no",required=false) int gchoice_no) throws IOException, PlatformException{
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("echoice");			
	    
	    // DataSet Column setting
	    ds.addColumn("choice_size", DataTypes.STRING,  256);
	    ds.addColumn("choice_color", DataTypes.STRING,  256);
	    ds.addColumn("choice_stock", DataTypes.INT, 256);
		ChoiceVO bean=null;
		
		bean=boardDao.choice_edit(gchoice_no);
		int row = ds.newRow();
			
		ds.set(row, "choice_size", bean.getChoice_size());
	    ds.set(row, "choice_color", bean.getChoice_color());
	    ds.set(row, "choice_stock",bean.getChoice_stock());
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/updatechoice.do" })
	public void updatechoice(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="choice_no",required=false) int gchoice_no) throws PlatformException{
		System.out.println(gchoice_no);
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					DataSet ds = pdata.getDataSet("echoice");
					
					// DAO
					ChoiceVO choiceVO;
					choiceVO = new ChoiceVO();
					
					choiceVO.setChoice_no(gchoice_no);
					choiceVO.setChoice_size(ds.getString(0, "choice_size"));
					choiceVO.setChoice_color(ds.getString(0, "choice_color"));
					choiceVO.setChoice_stock(ds.getInt(0, "choice_stock")); 
												
					boardDao.updatechoice(choiceVO);
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
		
	}
	@RequestMapping(value="/board/delchoice.do")
	public void delchoice(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="choice_no",required=false) int gchoice_no) throws PlatformException{
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		
					// receive client request
					// not need to receive
				    
					// create HttpPlatformRequest for receive data from client
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					boardDao.delchoice(gchoice_no);
					
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    
			
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/clientlist.do" })
	public void clientlist(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
			@RequestParam(value = "keyField", defaultValue = "") String keyField,
			@RequestParam(value = "keyWord", defaultValue = "") String keyWord) throws PlatformException{
		
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					// DataSet
					DataSet ds = new DataSet("client");			
				    
				    // DataSet Column setting
					ds.addColumn("no", DataTypes.INT, 256);
					ds.addColumn("client_no", DataTypes.INT, 256);
   				    ds.addColumn("client_name", DataTypes.STRING, 256);
				    ds.addColumn("client_id", DataTypes.STRING,  256);
				    ds.addColumn("client_phone", DataTypes.STRING,  700);
					
					// DAO
				    ClientVO clientVO;
					HashMap<String, Object> map = new HashMap();
					map.put("keyField", keyField);
					map.put("keyWord", keyWord);					
				   // strErrorMsg  = 0;
					List<ClientVO> list = null;
					list=this.boardDao.clientlist(map);
				   				   
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   clientVO = new ClientVO();
					   clientVO = list.get(i);
					   ds.set(row, "no", i+1);
						ds.set(row, "client_no", clientVO.getClient_no());
					   	ds.set(row, "client_name", clientVO.getClient_name());
					    ds.set(row, "client_id", clientVO.getClient_id());
					    ds.set(row, "client_phone", clientVO.getClient_phone());
				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({"/board/clientdetail.do"})
	public void clientdetail(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="client_no",required=false) int gclient_no) throws IOException, PlatformException{
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("dclient");			
	    
	    // DataSet Column setting
		ds.addColumn("client_no", DataTypes.INT, 256);
	    ds.addColumn("client_name", DataTypes.STRING,  256);
	    ds.addColumn("client_id", DataTypes.STRING,  256);
	    ds.addColumn("client_pw", DataTypes.STRING, 256);
	    ds.addColumn("client_phone", DataTypes.STRING, 256);
	    ds.addColumn("client_address", DataTypes.STRING, 256);
		ClientVO bean=null;
		
		bean=boardDao.clientdetail(gclient_no);
		int row = ds.newRow();
			
		ds.set(row, "client_no", bean.getClient_no());
	    ds.set(row, "client_name", bean.getClient_name());
	    ds.set(row, "client_id",bean.getClient_id());
	    ds.set(row, "client_pw",bean.getClient_pw());
	    ds.set(row, "client_phone",bean.getClient_phone());
	    ds.set(row, "client_address",bean.getClient_address());
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/orderlist.do" })
	public void orderlist(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
			@RequestParam(value = "keyField", defaultValue = "") String keyField,
			@RequestParam(value = "keyWord", defaultValue = "") String keyWord) throws PlatformException{
		
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					// DataSet
					DataSet ds = new DataSet("order");			
				    
				    // DataSet Column setting
					ds.addColumn("no", DataTypes.INT, 256);
					ds.addColumn("ordering_no", DataTypes.INT, 256);
					ds.addColumn("ordering_date", DataTypes.DATE, 256);
				    ds.addColumn("client_id", DataTypes.STRING,  256);
				    ds.addColumn("pro_no", DataTypes.INT,  700);
				    ds.addColumn("ordering_status", DataTypes.STRING,  700);
				    ds.addColumn("ordering_confirm", DataTypes.STRING,  700);
					// DAO
				    OrderingVO orderingVO;
					HashMap<String, Object> map = new HashMap();
					map.put("keyField", keyField);
					map.put("keyWord", keyWord);				
				   // strErrorMsg  = 0;
					List<OrderingVO> list = null;
					list=this.boardDao.orderlist(map);
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   orderingVO = new OrderingVO();
					   orderingVO = list.get(i);
					   ds.set(row, "no", i+1);
						ds.set(row, "ordering_no", orderingVO.getOrdering_no());
					   	ds.set(row, "ordering_date", orderingVO.getOrdering_date());
					    ds.set(row, "client_id", orderingVO.getClient_id());
					    ds.set(row, "pro_no", orderingVO.getPro_no());
					    ds.set(row, "ordering_status", orderingVO.getOrdering_status());
					    if(orderingVO.getOrdering_confirm()==0){
					    	ds.set(row, "ordering_confirm", "수취대기");
					    }else{
					    	ds.set(row, "ordering_confirm", "수취완료");
					    }   
					   

				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({"/board/orderdetail.do"})
	public void orderdetail(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="ordering_no",required=false) int gordering_no) throws IOException, PlatformException{

		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("dorder");			
	    
	    // DataSet Column setting
		ds.addColumn("ordering_no", DataTypes.INT, 256);
	    ds.addColumn("pro_no", DataTypes.INT,  256);
	    ds.addColumn("choice_no", DataTypes.INT,  256);
	    ds.addColumn("ordering_num", DataTypes.INT, 256);
	    ds.addColumn("ordering_price", DataTypes.INT, 256);
	    ds.addColumn("client_id", DataTypes.STRING, 256);
	    ds.addColumn("ordering_request", DataTypes.STRING, 256);
	    ds.addColumn("ordering_payment", DataTypes.STRING, 256);
	    ds.addColumn("ordering_date", DataTypes.DATE, 256);
	    ds.addColumn("ordering_status", DataTypes.STRING, 256);
	    ds.addColumn("ordering_confirm", DataTypes.STRING, 256);
		OrderingVO bean=null;
		
		bean=boardDao.orderdetail(gordering_no);
		int row = ds.newRow();
			
		ds.set(row, "ordering_no", bean.getOrdering_no());
	    ds.set(row, "pro_no", bean.getPro_no());
	    ds.set(row, "choice_no",bean.getChoice_no());
	    ds.set(row, "ordering_num",bean.getOrdering_num());
	    ds.set(row, "ordering_price",bean.getOrdering_price());
	    ds.set(row, "client_id",bean.getClient_id());
	    ds.set(row, "ordering_request",bean.getOrdering_request());
	    ds.set(row, "ordering_payment",bean.getOrdering_payment());
	    ds.set(row, "ordering_date",bean.getOrdering_date());
	    ds.set(row, "ordering_status",bean.getOrdering_status());
	    if(bean.getOrdering_confirm()==0){
	    	ds.set(row, "ordering_confirm","수취대기");	    	
	    }else{
	    	ds.set(row, "ordering_confirm","수취확인");	 
	    }
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/orderstatus.do" })
	public void ordering_status(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="ordering_no",required=false) int gordering_no,@RequestParam(value="ordering_status",required=false) String ordering_status) throws PlatformException, UnsupportedEncodingException{
		PlatformData pdata = new PlatformData();
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					//DataSet ds = pdata.getDataSet("eproduct");
					
					// DAO
					OrderingVO orderingVO;
					orderingVO = new OrderingVO();
					
					orderingVO.setOrdering_no(gordering_no);
					orderingVO.setOrdering_status(ordering_status);
												
					boardDao.ordering_status(orderingVO);
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
		
	}
	@RequestMapping({ "/board/iorderlist.do" })
	public void iorderlist(@RequestParam(value="client_id",required=false) String gclient_id,HttpServletRequest request, HttpServletResponse response) throws PlatformException{
		System.out.println(gclient_id);
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					// DataSet
					DataSet ds = new DataSet("iorder");			
				    
				    // DataSet Column setting
					ds.addColumn("ordering_no", DataTypes.INT, 256);
					ds.addColumn("ordering_date", DataTypes.DATE, 256);
				    ds.addColumn("pro_no", DataTypes.INT,  700);
				    ds.addColumn("ordering_status", DataTypes.STRING,  700);
				    ds.addColumn("ordering_confirm", DataTypes.STRING,  700);
					// DAO
				    OrderingVO orderingVO;
					HashMap<String, Object> map = new HashMap();
					map.put("client_id", gclient_id);
					
				   // strErrorMsg  = 0;
					List<OrderingVO> list = null;
					list=this.boardDao.iorderlist(map);
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   orderingVO = new OrderingVO();
					   orderingVO = list.get(i);
					    
						ds.set(row, "ordering_no", orderingVO.getOrdering_no());
					   	ds.set(row, "ordering_date", orderingVO.getOrdering_date());
					    ds.set(row, "pro_no", orderingVO.getPro_no());
					    ds.set(row, "ordering_status", orderingVO.getOrdering_status());
					    if(orderingVO.getOrdering_confirm()==0){
					    	ds.set(row, "ordering_confirm", "수취대기");
					    }else{
					    	ds.set(row, "ordering_confirm", "수취완료");
					    }   
					   

				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
}
