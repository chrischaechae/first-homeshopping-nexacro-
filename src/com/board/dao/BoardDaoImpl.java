package com.board.dao;

import com.board.VO.BoardVO;
import com.board.VO.ChoiceVO;
import com.board.VO.ClientVO;
import com.board.VO.ImgVO;
import com.board.VO.OrderingVO;
import com.board.VO.ProductVO;
import com.board.dao.BoardDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Component;

@Component
public class BoardDaoImpl extends SqlSessionDaoSupport implements BoardDao {
	
	public List<ProductVO> list(Map<String, Object> map) {
		List<ProductVO> list = getSqlSession().selectList("boardList", map);
		return list;
	}

	public int getCount(Map<String, Object> map) {
		return ((Integer) getSqlSession().selectOne("boardCount", map)).intValue();
	}

	@Override
	public void addproduct(ProductVO productVO) {
		getSqlSession().insert("addproduct", productVO);
		
	}

	@Override
	public void uploadimg(HashMap<String, Object> map) {
		getSqlSession().update("uploadimg", map);
		
	}

	@Override
	public ProductVO pro_detail(int pro_no) {
		ProductVO bean = getSqlSession().selectOne("pro_detail", pro_no);
		return bean;
	}

	@Override
	public void pro_update(ProductVO productVO) {
		getSqlSession().update("pro_update", productVO);
		
	}

	@Override
	public void reuploadimg(HashMap<String, Object> map) {
		getSqlSession().update("reuploadimg", map);
		
	}

	@Override
	public List<ChoiceVO> choicelist(HashMap<String, Object> map) {
		List<ChoiceVO> list = getSqlSession().selectList("choicelist", map);
		return list;
	}

	@Override
	public void addchoice(ChoiceVO choiceVO) {
		getSqlSession().insert("addchoice", choiceVO);
		
	}

	@Override
	public ChoiceVO choice_edit(int gchoice_no) {
		ChoiceVO bean = getSqlSession().selectOne("choice_edit", gchoice_no);
		return bean;
	}

	@Override
	public void updatechoice(ChoiceVO choiceVO) {
		getSqlSession().insert("updatechoice", choiceVO);
		
	}

	@Override
	public void delchoice(int gchoice_no) {
		getSqlSession().delete("delchoice", gchoice_no);
	
	}

	@Override
	public List<ClientVO> clientlist(HashMap<String, Object> map) {
		List<ClientVO> list = getSqlSession().selectList("clientlist", map);
		return list;	
	}

	@Override
	public ClientVO clientdetail(int gclient_no) {
		ClientVO bean = getSqlSession().selectOne("clientdetail", gclient_no);
		return bean;
	}

	@Override
	public List<OrderingVO> orderlist(HashMap<String, Object> map) {
		List<OrderingVO> list = getSqlSession().selectList("orderlist", map);
		return list;
	}

	@Override
	public OrderingVO orderdetail(int gordering_no) {
		OrderingVO bean = getSqlSession().selectOne("orderdetail", gordering_no);
		return bean;
	}

	@Override
	public void ordering_status(OrderingVO orderingVO) {
		getSqlSession().update("ordering_status", orderingVO);
		
	}

	@Override
	public List<OrderingVO> iorderlist(HashMap<String, Object> map) {
		List<OrderingVO> list = getSqlSession().selectList("iorderlist", map);
		return list;
	}

	@Override
	public void imgupload(HashMap<String, Object> map) {
		getSqlSession().insert("imgupload", map);
		
	}

	@Override
	public ImgVO dimg(int gpro_no) {
		ImgVO bean = getSqlSession().selectOne("dimg", gpro_no);
		return bean;
	}

	@Override
	public void rreuploadimg(HashMap<String, Object> map) {
		getSqlSession().update("rreuploadimg", map);
		
	}

	@Override
	public void delpro(int gpro_no) {
		getSqlSession().delete("delpro", gpro_no);
		
	}
	
	

}
