package com.board.dao;

import com.board.VO.BoardVO;
import com.board.VO.ChoiceVO;
import com.board.VO.ClientVO;
import com.board.VO.ImgVO;
import com.board.VO.OrderingVO;
import com.board.VO.ProductVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract interface BoardDao {
	
	public abstract List<ProductVO> list(Map<String, Object> paramMap);

	public abstract int getCount(Map<String, Object> paramMap);

	public abstract void addproduct(ProductVO productVO);

	public abstract void uploadimg(HashMap<String, Object> map);

	public abstract ProductVO pro_detail(int pro_no);

	public abstract void pro_update(ProductVO productVO);

	public abstract void reuploadimg(HashMap<String, Object> map);

	public abstract List<ChoiceVO> choicelist(HashMap<String, Object> map);

	public abstract void addchoice(ChoiceVO choiceVO);

	public abstract ChoiceVO choice_edit(int gchoice_no);

	public abstract void updatechoice(ChoiceVO choiceVO);

	public abstract void delchoice(int gchoice_no);

	public abstract List<ClientVO> clientlist(HashMap<String, Object> map);

	public abstract ClientVO clientdetail(int gclient_no);

	public abstract List<OrderingVO> orderlist(HashMap<String, Object> map);

	public abstract OrderingVO orderdetail(int gordering_no);

	public abstract void ordering_status(OrderingVO orderingVO);

	public abstract List<OrderingVO> iorderlist(HashMap<String, Object> map);

	public abstract void imgupload(HashMap<String, Object> map);

	public abstract ImgVO dimg(int gpro_no);

	public abstract void rreuploadimg(HashMap<String, Object> map);

	public abstract void delpro(int gpro_no);



}
