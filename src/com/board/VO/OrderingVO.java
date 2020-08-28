package com.board.VO;

import java.sql.Date;

public class OrderingVO {
	private int ordering_no;
	private int pro_no;
	private int choice_no;
	private int ordering_num;
	private int ordering_price;
	private String client_id;
	private String ordering_request;
	private String ordering_payment;
	private Date ordering_date;
	private String ordering_status;
	private int ordering_confirm;
	public int getOrdering_no() {
		return ordering_no;
	}
	public void setOrdering_no(int ordering_no) {
		this.ordering_no = ordering_no;
	}
	public int getPro_no() {
		return pro_no;
	}
	public void setPro_no(int pro_no) {
		this.pro_no = pro_no;
	}
	public int getChoice_no() {
		return choice_no;
	}
	public void setChoice_no(int choice_no) {
		this.choice_no = choice_no;
	}
	public int getOrdering_num() {
		return ordering_num;
	}
	public void setOrdering_num(int ordering_num) {
		this.ordering_num = ordering_num;
	}
	public int getOrdering_price() {
		return ordering_price;
	}
	public void setOrdering_price(int ordering_price) {
		this.ordering_price = ordering_price;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getOrdering_request() {
		return ordering_request;
	}
	public void setOrdering_request(String ordering_request) {
		this.ordering_request = ordering_request;
	}
	public String getOrdering_payment() {
		return ordering_payment;
	}
	public void setOrdering_payment(String ordering_payment) {
		this.ordering_payment = ordering_payment;
	}
	public Date getOrdering_date() {
		return ordering_date;
	}
	public void setOrdering_date(Date ordering_date) {
		this.ordering_date = ordering_date;
	}
	public String getOrdering_status() {
		return ordering_status;
	}
	public void setOrdering_status(String ordering_status) {
		this.ordering_status = ordering_status;
	}
	public int getOrdering_confirm() {
		return ordering_confirm;
	}
	public void setOrdering_confirm(int ordering_confirm) {
		this.ordering_confirm = ordering_confirm;
	}
	
	
	
}
