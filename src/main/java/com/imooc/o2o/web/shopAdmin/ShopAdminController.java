package com.imooc.o2o.web.shopAdmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/shopadmin",method={RequestMethod.GET})
/**
 * 主要用来解析路由并转发到相应的html中
 * @author renjie
 *
 */
public class ShopAdminController {
	@RequestMapping(value="/shopoperation")
	public String shopOperation() {
		//转发至店铺注册/编辑页面
		return "shop/shopoperation";
	}
	
	@RequestMapping(value="/shoplist")
	public String shopList() {
		//转发至店铺列表页面
		return "shop/shoplist";
	}
	
	@RequestMapping(value="/shopmanagement")
	public String shopManagement() {
		//转发至店铺管理页面
		return "shop/shopmanagement";
	}
	
	@RequestMapping(value="/productcategorymanagement",method = RequestMethod.GET)
	public String productManagement() {
		//转发到商品类别管理页面
		return "shop/productcategorymanagement";
	}
	
	@RequestMapping(value="/productoperation",method = RequestMethod.GET)
	public String productOperation() {
		//转发到商品类别管理页面
		return "shop/productoperation";
	}
}
