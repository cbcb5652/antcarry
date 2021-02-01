package com.ants.programmer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.googlecode.jsonplugin.annotations.JSON;

import net.sf.json.JSONObject;

public class UsersDao {

	// 判断用户是否注册过
	public static boolean isRegistered(String userid) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select * from ants_users where au_user_id=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				if (resultset.getString("au_user_id").equals(userid)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(resultset, statement, null);
		}
		return false;
	}

	// 根据商品的id来选择对应的用户
	public static JSONObject selectUser(String id) {


		JSONObject product = new JSONObject();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ants_users where au_user_id="
						+ "(select ap_mobile from ants_product where ap_id=?)";
				statement = connection.prepareStatement(sql);
				statement.setString(1, id);
				resultset = statement.executeQuery();


				while (resultset.next()) {


					product.put("userName", resultset.getString("au_user_name"));
					product.put("userImg", resultset.getString("au_photo"));
					product.put("userMobile", resultset.getString("au_mobile"));
					product.put("userWechat", resultset.getString("wechat"));
					product.put("userQQ", resultset.getString("qq"));
					product.put("userAddress", resultset.getString("au_address"));
					product.put("wcHide", resultset.getString("wechathidden"));
					product.put("qqHide", resultset.getString("qqhidden"));
					product.put("userid", resultset.getString("au_user_id"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}

	// 根据手机号码来获取对应用户的个人信息
	public static JSONObject selectMyself(String mobile) {
		JSONObject product = new JSONObject();

		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		if (connection == null) {
			connection = BaseDao.getConnection();
			try {
				String sql = "select * from ants_users where au_user_id=?";
				statement = connection.prepareStatement(sql);
				statement.setString(1, mobile);
				resultset = statement.executeQuery();
				while (resultset.next()) {
					product.put("userName", resultset.getString("au_user_name"));
					product.put("userImg", resultset.getString("au_photo"));
					product.put("userMobile", resultset.getString("au_mobile"));
					product.put("wcHide", resultset.getString("wechathidden"));
					product.put("qqHide", resultset.getString("qqhidden"));
					product.put("address", resultset.getString("au_address"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				BaseDao.closeResource(resultset, statement, null);
			}
		}

		return product;
	}

	// 插入数据
	public static void insert(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "insert into ants_users(au_user_id, au_user_name,au_password,"
					+ "au_mobile,au_photo,wechat,qq,wechathidden,qqhidden,au_address) values(?,?,?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			statement.setString(2, userid);
			statement.setString(3, password);
			statement.setString(4, userid);
			statement.setString(5, "images\\1.jpg");
			statement.setString(6, "这个人很懒，什么都没有留下！");
			statement.setString(7, "这个人很懒，什么都没有留下！");
			statement.setString(8, "false");
			statement.setString(9, "false");
			statement.setString(10, "这个人很懒，什么都没有留下！");

			statement.executeUpdate();
			System.out.println("注册成功！");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}
	
	
	// 插入学号对应的数据
	public static void insertstudent(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "insert into ants_users(au_user_id, au_user_name,au_password,"
					+ "au_mobile,au_photo,wechat,qq,wechathidden,qqhidden,au_address) values(?,?,?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			statement.setString(2, userid);
			statement.setString(3, password);
			statement.setString(4, "");
			statement.setString(5, "img\\1.jpg");
			statement.setString(6, "这个人很懒，什么都没有留下！");
			statement.setString(7, "这个人很懒，什么都没有留下！");
			statement.setString(8, "false");
			statement.setString(9, "false");
			statement.setString(10, "这个人很懒，什么都没有留下！");

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}


	// 根据用户的手机号码来获取对应用户的信息
	public static JSONObject userMessage(String userid) {
		JSONObject user = new JSONObject();
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select * from ants_users where au_user_id=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				user.put("name", resultset.getString("au_user_name"));
				user.put("mobile", resultset.getString("au_mobile"));
				user.put("wechat", resultset.getString("wechat"));
				user.put("QQ", resultset.getString("qq"));
				user.put("address", resultset.getString("au_address"));
				user.put("img", resultset.getString("au_photo"));
				user.put("wcHide", resultset.getString("wechathidden"));
				user.put("qqHide", resultset.getString("qqhidden"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(resultset, statement, null);
		}
		return user;
	}

	// 更新用户的个人信息
	public static void update(String userid, String name, String wechat, String qq, String address, String wechathidden,
			String qqhidden) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "update ants_users set au_user_name=?, wechat=?, qq=?,au_address=?,wechathidden=?,qqhidden=? where au_user_id=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, wechat);
			statement.setString(3, qq);
			statement.setString(4, address);
			statement.setString(5, wechathidden);
			statement.setString(6, qqhidden);
			statement.setString(7, userid);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}

	// 更改个人用户的密码
	public static void changePassword(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "update ants_users set au_password=? where au_user_id=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, password);
			statement.setString(2, userid);
			statement.executeUpdate();
			System.out.println("修改成功！");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}

	// 更改个人用户的头像
	public static void changePhoto(String userid, String photo) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "update ants_users set an_photo=? where au_user_id=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, photo);
			statement.setString(2, userid);
			statement.executeUpdate();
			System.out.println("保存成功！");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
	}

	public static boolean Login(String userid, String password) {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			connection = BaseDao.getConnection();
			String sql = "select  * from ants_users where  au_user_id=? and  au_password=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userid);
			statement.setString(2, password);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				if (resultset.getString("au_user_id").equals(userid)
						&& resultset.getString("au_password").equals(password)) {
					return true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(null, statement, null);

		}
		return false;
	}
	
}
