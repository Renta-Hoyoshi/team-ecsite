package jp.co.internous.cony.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jp.co.internous.cony.model.domain.TblCart;
import jp.co.internous.cony.model.domain.dto.CartDto;
import jp.co.internous.cony.model.form.CartForm;
import jp.co.internous.cony.model.mapper.TblCartMapper;
import jp.co.internous.cony.model.session.LoginSession;

@Controller
@RequestMapping("/cony/cart")
public class CartController {
	
	@Autowired
	TblCartMapper tblCartMap;
	
	@Autowired
	LoginSession loginSession;
	
	Gson gson = new Gson();

	@RequestMapping("/")
	public String index(Model model) {
		boolean loginFrag = loginSession.getLoginFrag();
		int userId = 0;
		
		if(loginFrag == true) {
			userId = loginSession.getUserId();
		} else {
			userId = loginSession.getTempUserId();
		}
		
		List<CartDto> cartList = tblCartMap.findByUserId(userId);
		model.addAttribute("cartList", cartList);
		model.addAttribute("loginSession", loginSession);
		
		return "cart";
	}
	
	@RequestMapping("/add")
	public String addCart(CartForm cartForm, Model model) {
		
		boolean loginFrag = loginSession.getLoginFrag();
		int userId = 0;
		
		if(loginFrag == true) {
			userId = loginSession.getUserId();
		} else {
			userId = loginSession.getTempUserId();
		}
		
		if(tblCartMap.findByUserIdAndProductId(userId , cartForm.getProductId()) > 0 ) {
			tblCartMap.update(cartForm.getProductId(), userId, cartForm.getProductCount());
		} else {
			TblCart tblCart = new TblCart();
			tblCart.setUserId(userId);
			tblCart.setProductId(cartForm.getProductId());
			tblCart.setProductCount(cartForm.getProductCount());
			tblCartMap.insert(tblCart);
		}
		List<CartDto> cartList = tblCartMap.findByUserId(userId);
		model.addAttribute("cartList", cartList);
		model.addAttribute("loginSession", loginSession);
		return "cart";
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public String deleteCart(@RequestBody String jsonCartIdArray) {
		
		JsonObject jsonObj = gson.fromJson(jsonCartIdArray, JsonObject.class);
		JsonArray jsonAry = jsonObj.get("cartIdArray").getAsJsonArray();
		
		//cartIdの配列から順にidを取り出し削除
		for(int i = 0; i < jsonAry.size(); i++) {
			String jsonCartIdStr = jsonAry.get(i).toString();
			String cartIdStr = gson.fromJson(jsonCartIdStr, String.class);
			tblCartMap.deleteByCartId(Integer.parseInt(cartIdStr));
		}
		
		return "deleted";
	}
}

