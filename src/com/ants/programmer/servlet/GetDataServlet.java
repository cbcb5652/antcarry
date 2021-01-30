package com.ants.programmer.servlet;

import com.ants.programmer.dao.AnnouncementDao;
import com.ants.programmer.dao.ProductCategoryDao;
import com.ants.programmer.dao.ProductDao;
import com.ants.programmer.util.ItemsCFUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/dataServlet")
public class GetDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private JSONArray special = null;


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html.charset=utf-8");
        HttpSession session = request.getSession();
        String mobile = (String) session.getAttribute("mobile");

        JSONArray img = new JSONArray();

        JSONArray specialID = new JSONArray();
        JSONArray specialImage = new JSONArray();
        JSONArray specialName = new JSONArray();

        JSONArray hotID = new JSONArray();
        JSONArray hotImage = new JSONArray();
        JSONArray hotName = new JSONArray();

        JSONArray newID = new JSONArray();
        JSONArray newImage = new JSONArray();
        JSONArray newName = new JSONArray();

        if (mobile == null) {
            int specialindex = (int) (8 + Math.random() * 14);
            // 获取首页的不同分类的数据

            specialID = ProductDao.getData(specialindex, 4, "id");
            specialImage = ProductDao.getData(specialindex, 4, "filename");
            specialName = ProductDao.getData(specialindex, 4, "name");
            img.add(specialImage);
        } else {
            //猜你喜欢
            ArrayList<String> recommendList = ItemsCFUtil.getRecommendShop(mobile);
            ArrayList<String> Img = new ArrayList<String>();
            int sum = 0;
            if (recommendList.size() > 0) {
                for (String shopid : recommendList) {
                    JSONObject shop = ProductDao.selectShop(shopid);
                    if (sum > 4) {
                        break;
                    }
                    sum++;
                    if (shop.size() > 0) {
                        specialID.add(shop.getString("goodsID"));
                        Img.add(shop.getString("goodsImg"));
                        specialName.add(shop.getString("goodsName"));
                    }

                }
                img.add(Img);
            } else {
                int specialindex = (int) (8 + Math.random() * 14);
                // 获取首页的不同分类的数据
                specialID = ProductDao.getData(specialindex, 4, "id");
                specialImage = ProductDao.getData(specialindex, 4, "filename");
                specialName = ProductDao.getData(specialindex, 4, "name");
                img.add(specialImage);
            }
        }


        //最热闲置

        JSONObject Hot = ProductCategoryDao.selectProductByChildId( 4);
        if (Hot.size() > 0) {
            hotID = Hot.getJSONArray("goodsID");
            hotImage = Hot.getJSONArray("goodsImg");
            hotName = Hot.getJSONArray("goodsName");
        }


        //最新闲置
        JSONObject news = ProductCategoryDao.selectProductByNew();

        newID = news.getJSONArray("newId");
        newImage = news.getJSONArray("newImage");
        newName = news.getJSONArray("newName");


        //化妆数据渲染
        JSONArray firId = ProductDao.getData(4, 10, "id");
        JSONArray firImage = ProductDao.getData(4, 10, "filename");
        JSONArray firName = ProductDao.getData(4, 10, "name");


        //考试
        JSONArray secId = ProductDao.getData(1, 10, "id");
        JSONArray secImage = ProductDao.getData(1, 10, "filename");
        JSONArray secName = ProductDao.getData(1, 10, "name");


        //文具
        JSONArray thiId = ProductDao.getData(2, 10, "id");
        JSONArray thiImage = ProductDao.getData(2, 10, "filename");
        JSONArray thiName = ProductDao.getData(2, 10, "name");

        //衣裤
        JSONArray forsId = ProductDao.getData(3, 10, "id");
        JSONArray forsImage = ProductDao.getData(3, 10, "filename");
        JSONArray forsName = ProductDao.getData(3, 10, "name");


        //桌椅
        JSONArray fifId = ProductDao.getData(8, 10, "id");
        JSONArray fifImage = ProductDao.getData(8, 10, "filename");
        JSONArray fifName = ProductDao.getData(8, 10, "name");


        //电器
        JSONArray sixId = ProductDao.getData(6, 10, "id");//filename
        JSONArray sixImage = ProductDao.getData(6, 10, "filename");
        JSONArray sixName = ProductDao.getData(6, 10, "name");

        //组合
        JSONObject Message = new JSONObject();

        //将id赋值
        JSONArray id = new JSONArray();
        id.add(specialID);
        id.add(hotID);
        id.add(newID);
        id.add(firId);
        id.add(secId);
        id.add(thiId);
        id.add(forsId);
        id.add(fifId);
        id.add(sixId);

        //将img赋值
//		JSONArray img=new JSONArray();
        img.add(hotImage);
        img.add(newImage);
        img.add(firImage);
        img.add(secImage);
        img.add(thiImage);
        img.add(forsImage);
        img.add(fifImage);
        img.add(sixImage);

        //将name赋值
        JSONArray name = new JSONArray();
        name.add(specialName);
        name.add(hotName);
        name.add(newName);
        name.add(firName);
        name.add(secName);
        name.add(thiName);
        name.add(forsName);
        name.add(fifName);
        name.add(sixName);


        JSONObject Announcement = AnnouncementDao.selectAnnouncement("limit");

        //将数组压进json里面
        Message.put("id", id);
        Message.put("img", img);
        Message.put("name", name);
        Message.put("ID", Announcement.getJSONArray("id"));
        Message.put("title", Announcement.getJSONArray("title"));
        Message.put("content", Announcement.getJSONArray("content"));
        Message.put("time", Announcement.getJSONArray("time"));


        response.getWriter().write(JSONObject.fromObject(Message).toString());
    }

}
