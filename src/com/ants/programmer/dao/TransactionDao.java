package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ants.programmer.bean.TransactionBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TransactionDao {
	// 插入数据
	public static int insert(TransactionBean user) {
		String sql = "insert into ants_transaction values(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = { user.getId(),user.getMobile(), user.getName(), user.getPrice(), user.getIntroduce(), user.getWays(),
				user.getBargin(), user.getParentId(), user.getChildId(), user.getFileName(), user.getStatus() };
		return BaseDao.exectuIUD(sql, params);
	}

	// 删除某一行数据
	public static int delete(String id) {
		String sql = "delete from ants_transaction where at_id=?";
		Object[] params = { id };
		return BaseDao.exectuIUD(sql, params);

	}
	
	//根据订单号和手机号码删除信息
	public static int deleteByIdAndMobile(String id,String mobile) {
		String sql = "delete from ants_transaction where at_id=? and at_mobile=?";
		Object[] params = { id ,mobile};
		return BaseDao.exectuIUD(sql, params);

	}
	
	
	public static String  choseSellerMobile(String id,String mobile) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		String Mobile="";
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select at_mobile from ants_transaction where at_id=? and at_mobile!=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				statement.setString(2, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					Mobile = resultset.getString("at_mobile");
				}
				return Mobile;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return Mobile;
	}
	
	//根据订单号来查看属于什么分类
	public static int selectChildIDByID(String id) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		int ChildID = 0;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select apc_child_id from ants_transaction where at_id=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ChildID = resultset.getInt("apc_child_id");
				}
				return ChildID;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return ChildID;
	}

	// 根据买家/卖家的手机号码来获取对应购物车的信息
	public static JSONObject selectShopByMobile(String mobile, int status) {
		JSONObject shop = new JSONObject();
		ArrayList<String> ID = new ArrayList<String>();
		ArrayList<String> Mobile = new ArrayList<String>();
		ArrayList<String> Name = new ArrayList<String>();
		ArrayList<Double> Price = new ArrayList<Double>();
		ArrayList<String> Introduce = new ArrayList<String>();
		ArrayList<String> Ways = new ArrayList<String>();
		ArrayList<String> Bargin = new ArrayList<String>();
		JSONArray fileName=new JSONArray();
		ArrayList<String> Address = new ArrayList<String>();
		ArrayList<Integer> Status = new ArrayList<Integer>();
		
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		String sql = "";
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				sql = "select * from ants_transaction where at_mobile=? and at_status=?";
				if (status != 3) {
					sql = "select * from ants_transaction where at_mobile=? and at_status=? or at_status=?";
				}
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				if (status != 3) {
					statement.setInt(2, 1);
					statement.setInt(3, 2);
				} else {
					statement.setInt(2, status);
				}

				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					ID.add(resultset.getString("at_id"));
					Mobile.add(resultset.getString("at_mobile"));
					Name.add(resultset.getString("at_name"));
					Price.add(resultset.getDouble("at_price"));
					Introduce.add(resultset.getString("at_introduce"));
					Ways.add(resultset.getString("at_ways"));
					Bargin.add(resultset.getString("at_bargin"));
					String filename=resultset.getString("at_file_name");
					String fn[]=filename.split(";");
					for(String FileName:fn) {
						img.add(FileName);
					}
					fileName.add(img);
					Status.add(resultset.getInt("at_status"));
				}
				for (String m : Mobile) {
					JSONObject seller = UsersDao.selectMyself(m);
					Address.add(seller.getString("address"));
				}
				shop.put("address", Address);

				shop.put("goodsID", ID);
				shop.put("goodsName", Name);
				shop.put("goodsPrice", Price);
				shop.put("goodsIntroduce", Introduce);
				shop.put("goodsWays", Ways);
				shop.put("goodsBargin", Bargin);
				shop.put("goodsImg", fileName);
				shop.put("status", Status);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return shop;
	}

	// 改变交易的状态（正在交易/交易成功)
	public static void changeStatus(TransactionBean transaction) {
		String sql = "update ants_transaction set at_status=? where at_id=?";

		Object[] params = { transaction.getStatus(), transaction.getId() };
		BaseDao.exectuIUD(sql, params);
	}

	// 根据商品id来获取交易中的 商品的信息
	public static JSONObject selectTransaction(String id ,String sellermobile) {
		JSONObject product = new JSONObject();
		JSONArray Img=new JSONArray();
		
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ants_transaction where at_id=? and at_mobile=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				statement.setString(2, sellermobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					ArrayList<String> img=new ArrayList<String>();
					product.put("goodsID", resultset.getString("at_id"));
					product.put("goodsMobile", resultset.getString("at_mobile"));
					product.put("goodsName", resultset.getString("at_name"));
					product.put("goodsPrice", resultset.getDouble("at_price"));
					product.put("goodsIntroduce", resultset.getString("at_introduce"));
					product.put("goodsWays", resultset.getString("at_ways"));
					product.put("goodsBargin", resultset.getString("at_bargin"));
					product.put("parentId", resultset.getString("apc_id"));
					product.put("childId", resultset.getString("apc_child_id"));
					String fileName=resultset.getString("at_file_name");
					String fn[]=fileName.split(";");
					for(String filename:fn) {
						img.add(filename);
					}
					Img.add(img);
					product.put("goodsImg", Img);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}

}
