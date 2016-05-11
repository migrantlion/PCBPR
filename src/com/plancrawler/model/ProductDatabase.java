package com.plancrawler.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ProductDatabase implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<Product> products = new LinkedList<Product>();
}
