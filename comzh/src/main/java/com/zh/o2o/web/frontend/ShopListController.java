package com.zh.o2o.web.frontend;

import com.zh.o2o.dto.ShopExecution;
import com.zh.o2o.entity.Area;
import com.zh.o2o.entity.Shop;
import com.zh.o2o.entity.ShopCategory;
import com.zh.o2o.service.AreaService;
import com.zh.o2o.service.ShopCategoryService;
import com.zh.o2o.service.ShopService;
import com.zh.o2o.util.HttpSrevletRuquestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class ShopListController {
    @Autowired
    private AreaService areaService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private ShopService shopService;

    @RequestMapping(value = "/listshopspageinfo",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listShopPageInfo(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        long parentId = HttpSrevletRuquestUtil.getLong(request,"parentId");
        List<ShopCategory> shopCategoryList = null;
        if (parentId != -1){
            try {
                ShopCategory shopCategoryCondition = new ShopCategory();
                ShopCategory parent = new ShopCategory();
                parent.setShopCategoryId(parentId);
                shopCategoryCondition.setParent(parent);
                shopCategoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition);

            }catch (Exception e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }
        }else {
            try {
                shopCategoryList = shopCategoryService.getShopCategoryList(null);
            }catch (Exception e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }


        }
        modelMap.put("shopCategoryList",shopCategoryList);
        List<Area> areaList = null;
        try {
            areaList = areaService.getAreaList();
            modelMap.put("areaList",areaList);
            modelMap.put("success",true);
            return modelMap;
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMSg",e.getMessage());
        }
        return modelMap;
    }

    @RequestMapping(value = "/listshops", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShops(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpSrevletRuquestUtil.getInt(request, "pageIndex");
        int pageSize = HttpSrevletRuquestUtil.getInt(request, "pageSize");
        if ((pageIndex > -1) && (pageSize > -1)) {
            long parentId = HttpSrevletRuquestUtil.getLong(request, "parentId");
            long shopCategoryId = HttpSrevletRuquestUtil.getLong(request,
                    "shopCategoryId");
            long areaId = HttpSrevletRuquestUtil.getLong(request, "areaId");
            String shopName = HttpSrevletRuquestUtil.getString(request,
                    "shopName");
            Shop shopCondition = compactShopCondition4Search(parentId,
                    shopCategoryId, areaId, shopName);
            ShopExecution se = shopService.getShopList(shopCondition,
                    pageIndex, pageSize);
            modelMap.put("shopList", se.getShopList());
            modelMap.put("count", se.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex");
        }

        return modelMap;
    }

    private Shop compactShopCondition4Search(long parentId,
                                             long shopCategoryId, long areaId, String shopName) {
        Shop shopCondition = new Shop();
        if (parentId != -1L) {
            ShopCategory parentCategory = new ShopCategory();
            parentCategory.setShopCategoryId(parentId);
            shopCondition.setParentCategory(parentCategory);
        }
        if (shopCategoryId != -1L) {
            ShopCategory shopCategory = new ShopCategory();
            shopCategory.setShopCategoryId(shopCategoryId);
            shopCondition.setShopCategory(shopCategory);
        }
        if (areaId != -1L) {
            Area area = new Area();
            area.setAreaId(areaId);
            shopCondition.setArea(area);
        }

        if (shopName != null) {
            shopCondition.setShopName(shopName);
        }
        shopCondition.setEnableStatus(1);
        return shopCondition;
    }
}
