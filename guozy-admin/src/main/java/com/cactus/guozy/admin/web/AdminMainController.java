package com.cactus.guozy.admin.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cactus.guozy.api.endpoint.AppEndpoint;
import com.cactus.guozy.api.wrapper.ErrorMsgWrapper;
import com.cactus.guozy.common.cms.Asset;
import com.cactus.guozy.common.cms.AssetService;
import com.cactus.guozy.common.cms.AssetStorageService;
import com.cactus.guozy.common.config.RuntimeEnvConfigService;
import com.cactus.guozy.common.file.FileService;
import com.cactus.guozy.common.json.JsonResponse;
import com.cactus.guozy.core.domain.Category;
import com.cactus.guozy.core.domain.Order;
import com.cactus.guozy.core.domain.Shop;
import com.cactus.guozy.core.dto.GenericWebResult;
import com.cactus.guozy.core.service.AppSettingService;
import com.cactus.guozy.core.service.CatalogService;
import com.cactus.guozy.core.service.OrderService;
import com.cactus.guozy.profile.domain.User;

@Controller
public class AdminMainController extends AbstractAdminController{
	protected static final Logger LOG = LoggerFactory.getLogger(AppEndpoint.class);
	
	@Resource(name = "fileService")
	protected FileService fileService;

	@Resource(name = "assetStorageService")
	protected AssetStorageService ass;
	
	@Resource(name = "assetService")
	protected AssetService assetService;
	
	
	@Resource(name="appSettingService")
	protected AppSettingService appSettingService;
	
	@Resource(name="catalogService")
	protected CatalogService catalogService;
	

	@Resource(name="orderService")
	protected OrderService orderService;
	
	@RequestMapping(value = { "/appnotification"}, method = RequestMethod.GET)
	public String appnotification(Model model) {
		setModelAttributes(model, "tuisong");
		return "appnotification";
	}
	
	@RequestMapping(value = {"/appsettings"}, method = RequestMethod.GET)
	public String appsettings(Model model) {
		model.addAttribute("aboutus", appSettingService.findAboutusUrl());
		model.addAttribute("fruits", appSettingService.findAllFruitCommonSense());
		
		setModelAttributes(model, "appsettings");
		return "appsettings";
	}
	
	@RequestMapping(value = {"/appsettings/aboutus"}, method = RequestMethod.POST)
	public void aboutus(@RequestParam("url") String url, HttpServletResponse resp) {
		appSettingService.saveAboutusUrl(url);
		new JsonResponse(resp).with("status", "200").with("data", "ok").done();
	}
	
	@RequestMapping(value = {"/category"}, method = RequestMethod.GET)
	public String category(Model model) {
		List<Shop> shops = catalogService.findAllShops();
		List<List<Category>> categories=new ArrayList<>();
		for(int i=0;i<shops.size();i++){
			categories.add(catalogService.findCategories(shops.get(i).getId()));
		}
		model.addAttribute("shops", shops);
		model.addAttribute("categories", categories);
		setModelAttributes(model, "leimuguanli");
		return "category";
	}
	
	@RequestMapping(value = {"/shop"}, method = RequestMethod.GET)
	public String shop(Model model) {
		List<Shop> shops = catalogService.findAllShops();
		model.addAttribute("shops", shops);
		
		setModelAttributes(model, "dianpuguanli");
		return "shop";
	}
	
	
	@RequestMapping(value = {"/saler"}, method = RequestMethod.GET)
	public String saler(Model model) {
		List<Shop> shops = catalogService.findAllShops();
		model.addAttribute("shops", shops);
		List<User> salers=catalogService.findSalersByShopId(1l);
		model.addAttribute("salers", salers);
		
		setModelAttributes(model, "dianyuanguanli");
		return "saler";
	}
	
	
	@RequestMapping(value = {"/ordershop"}, method = RequestMethod.GET)
	public String ordershop(Model model) {
		List<Shop> shops = catalogService.findAllShops();
		model.addAttribute("shops", shops);
		List<User> salers=catalogService.findSalersByShopId(1l);
		model.addAttribute("salers", salers);
		setModelAttributes(model, "dingdanguanli");
		return "ordershop";
	}
	
	
	@RequestMapping(value = {"/salerlist"}, method = RequestMethod.GET)
	public String salerlist(@RequestParam("sid")String sid,HttpServletResponse resp,Model model) {
		resp.setHeader("x-frame-options", "sameorigin");
		List<User> salers=catalogService.findSalersByShopId(Long.parseLong(sid));
		model.addAttribute("salers", salers);
		model.addAttribute("sid", sid);
		return "salerlist";
	}
	
	@RequestMapping(value = {"/orderlist"}, method = RequestMethod.GET)
	public String orderlist(@RequestParam("sid")Long sid,HttpServletResponse resp,Model model) {
		resp.setHeader("x-frame-options", "sameorigin");
		List<Order> orders=orderService.findOrdersCompleted(1001l);
		model.addAttribute("orders", orders);
		model.addAttribute("sid", sid);
		return "orderlist";
	}
	
	@RequestMapping(value = {"/order"}, method = RequestMethod.GET)
	public String order(@RequestParam("orderId")Long orderId,HttpServletResponse resp,Model model) {
		resp.setHeader("x-frame-options", "sameorigin");
		Order order=orderService.findOrderById(orderId);
		model.addAttribute("orders", order);
		return "order";
	}
	
	@RequestMapping(value = {"/goods"}, method = RequestMethod.GET)
	public String goods(Model model) {
		List<Shop> shops = catalogService.findAllShops();
		model.addAttribute("shops", shops);
		
		setModelAttributes(model, "shangpinguanli");
		return "goods";
	}
	
	
	@RequestMapping(value = { "/user"}, method = RequestMethod.GET)
	public String user(Model model) {
		model.addAttribute("users", userService.findAllUsers());
		
		setModelAttributes(model, "yonghuguanli");
		return "user";
	}
	
	
	@RequestMapping(value = "/{userId}/update", method = RequestMethod.GET)
	public GenericWebResult updateUserInfo(HttpServletRequest request,
			@RequestParam("nickname") String name, @PathVariable("userId") Long userId) {

//		// 1、頭像
//		if (file != null) {
//			Map<String, String> properties = new HashMap<>();
//			properties.put("module", "profile");
//			properties.put("resourceId", userId.toString());
//
//			LOG.debug("开始头像资源存储.");
//			Asset asset = assetService.createAssetFromFile(file, properties);
//
//			if (asset == null) {
//				return GenericWebResult.error("500").withData(ErrorMsgWrapper.error("unknownError").withMsg("服务器内部错误"));
//			}
//
//			try {
//				ass.storeAssetFile(file, asset);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			userService.updateAvatarUrl(userId, asset.getUrl());
//		}
//		LOG.debug("结束头像资源存储.");
		//2.昵称
		if(TextUtils.isEmpty(name)){
			return GenericWebResult.error("-1").withData(ErrorMsgWrapper.error("datanull").withMsg("昵称不能为空"));
		}
		
		if (userService.updateNickname(userId, name)) {
			return GenericWebResult.success("ok");
		}
		return GenericWebResult.error("200");
	}
	
	@RequestMapping(value = "/{userId}/update2", method = RequestMethod.GET)
	public GenericWebResult updateUserInfo1(HttpServletRequest request,
			@RequestParam("nickname") String name, @PathVariable("userId") Long userId) {
		//2.昵称
		if(TextUtils.isEmpty(name)){
			return GenericWebResult.error("-1").withData(ErrorMsgWrapper.error("datanull").withMsg("昵称不能为空"));
		}
		
		if (userService.updateNickname(userId, name)) {
			return GenericWebResult.success("ok");
		}
		return GenericWebResult.error("200");
	}
}