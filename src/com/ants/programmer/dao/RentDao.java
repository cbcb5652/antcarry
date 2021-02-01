package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ants.programmer.bean.RentBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RentDao {
	// 插入数据
	public static int insert(RentBean rent) {
		String sql = "insert into ants_rent values(?,?,?)";
		Object[] params = { rent.getId(),rent.getStock(),rent.getStatus() };
		return BaseDao.exectuIUD(sql, params);
	}
	
	// 删除某一行数据
	public static int delete(String id) {
		String sql = "delete from ants_rent where ar_id=?";
		Object[] params = { id };
		return BaseDao.exectuIUD(sql, params);

	}
	
	public static JSONObject selectStockByShopId(String id) {
		JSONObject product = new JSONObject();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select ar_stock from ants_rent where ar_id=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					product.put("goodsStock", resultset.getString("ar_stock"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}
	
	
	//获取对应卖家手机号码的出租的商品
	public static JSONObject selectSellByUserRent(String mobile, int status) {
		JSONObject sellProduct = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();
		JSONArray fileName=new JSONArray();
		ArrayList<String> Address = new ArrayList<String>();
		ArrayList<Integer> ParentID = new ArrayList<Integer>();
		ArrayList<Integer> ChildID = new ArrayList<Integer>();
		ArrayList<String> ParentName = new ArrayList<String>();
		ArrayList<String> ChildName = new ArrayList<String>();
		ArrayList<String> Stock=new ArrayList<String>();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ants_product where ap_mobile=? and ap_status=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				statement.setInt(2, status);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("ap_id"));
					Name.add(resultset.getString("ap_name"));
					Price.add(resultset.getDouble("ap_price"));
					Introduce.add(resultset.getString("ap_introduce"));
					Ways.add(resultset.getString("ap_ways"));
					Bargin.add(resultset.getString("ap_bargin"));
					ParentID.add(resultset.getInt("apc_id"));
					ChildID.add(resultset.getInt("ap_child_id"));
					String filename=resultset.getString("ap_file_name");
					String fn[]=filename.split(";");
					for(String FileName:fn) {
						img.add(FileName);
					}
					fileName.add(img);
				}
				for (String id : ID) {
					JSONObject seller = UsersDao.selectUser(id);
					if(seller.size()>0) {//seller.getString("userAddress")!=null
						Address.add(seller.getString("userAddress"));
					}
					///
					JSONObject stock = selectStockByShopId(id);
					if(stock.size()>0) {
						Stock.add(stock.getString("goodsStock"));
					}
					
				}
				for (int parentid : ParentID) {
					ParentName.add(ProductCategoryDao.selectNameByID(parentid));
				}
				for (int childid : ChildID) {
					ChildName.add(ProductCategoryDao.selectNameByID(childid));
				}
				sellProduct.put("address", Address);
				sellProduct.put("parentClassify", ParentName);
				sellProduct.put("childClassify", ChildName);
				sellProduct.put("goodsID", ID);
				sellProduct.put("goodsName", Name);
				sellProduct.put("goodsImg", fileName);
				sellProduct.put("goodsPrice", Price);
				sellProduct.put("goodsIntroduce", Introduce);
				sellProduct.put("goodsWays", Ways);
				sellProduct.put("goodsBargin", Bargin);
				sellProduct.put("Stock", Stock);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(null, statement, null);
			}
		}
		return sellProduct;
	}
	
}
