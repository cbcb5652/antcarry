package com.ants.programmer.dao;

import com.ants.programmer.bean.ProductBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.client.transport.TransportClient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductDao {

    //升序或者降序
    public static JSONObject selectProductByLowOrHigh(String type, int status) {
        JSONObject product = new JSONObject();
        ArrayList<String> Name = new ArrayList<String>();
        ArrayList<Double> Price = new ArrayList<Double>();
        ArrayList<String> Introduce = new ArrayList<String>();
        JSONArray Img = new JSONArray();
        ArrayList<String> ID = new ArrayList<String>();
        ArrayList<String> userName = new ArrayList<String>();
        ArrayList<String> userAddress = new ArrayList<String>();
        ArrayList<String> userImg = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "";
                if (type.equals("0")) {
                    sql = "select * from ants_product where ap_status=? ";
                } else if (type.equals("1")) {
                    sql = "select * from ants_product where  ap_status=? order by ap_price ";
                } else if (type.equals("2")) {
                    sql = "select * from ants_product where  ap_status=? order by ap_price desc";
                }
                statement = connection.prepareStatement(sql);
                statement.setInt(1, status);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    ID.add(resultset.getString("ap_id"));
                    String fileName = resultset.getString("ap_file_name");
                    Price.add(resultset.getDouble("ap_price"));
                    Name.add(resultset.getString("ap_name"));
                    Introduce.add(resultset.getString("ap_introduce"));

                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                }

                for (String id : ID) {
                    JSONObject user = UsersDao.selectUser(id);
                    userName.add(user.getString("userName"));
                    userAddress.add(user.getString("userAddress"));
                    userImg.add(user.getString("userImg"));

                }
                product.put("userName", userName);
                product.put("goodsSource", userAddress);
                product.put("userImg", userImg);
                product.put("goodsID", ID);
                product.put("goodsImg", Img);
                product.put("goodsPrice", Price);
                product.put("goodsName", Name);
                product.put("goodsIntroduce", Introduce);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }
        return product;
    }


    // 根据商品价格来选择对应的商品
    public static JSONObject selectProductByPrice(double price1, double price2, int status) {
        JSONObject product = new JSONObject();
        ArrayList<String> Name = new ArrayList<String>();
        ArrayList<Double> Price = new ArrayList<Double>();
        ArrayList<String> Introduce = new ArrayList<String>();
        JSONArray Img = new JSONArray();
        ArrayList<String> ID = new ArrayList<String>();
        ArrayList<String> userName = new ArrayList<String>();
        ArrayList<String> userAddress = new ArrayList<String>();
        ArrayList<String> userImg = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select * from ants_product where ap_price>=? and ap_price<=? and ap_status=? order by ap_price";
                statement = connection.prepareStatement(sql);
                statement.setDouble(1, price1);
                statement.setDouble(2, price2);
                statement.setInt(3, status);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    ID.add(resultset.getString("ap_id"));
                    String fileName = resultset.getString("ap_file_name");
                    Price.add(resultset.getDouble("ap_price"));
                    Name.add(resultset.getString("ap_name"));
                    Introduce.add(resultset.getString("ap_introduce"));
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                }

                for (String id : ID) {
                    JSONObject user = UsersDao.selectUser(id);
                    userName.add(user.getString("userName"));
                    userAddress.add(user.getString("userAddress"));
                    userImg.add(user.getString("userImg"));

                }
                product.put("userName", userName);
                product.put("goodsSource", userAddress);
                product.put("userImg", userImg);
                product.put("goodsID", ID);
                product.put("goodsImg", Img);
                product.put("goodsPrice", Price);
                product.put("goodsName", Name);
                product.put("goodsIntroduce", Introduce);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }
        return product;
    }

    // 获取对应mobile的用户的出售/寻求的商品的总数
    public static int totalUserProducts(String mobile) {
        int Sum = 0;
        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select count(*)  from ants_product where ap_mobile=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, mobile);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    Sum = resultset.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return Sum;
    }

    // 根据对应手机号码选择此用户的其他的发布的商品
    public static JSONObject selectOthersProduct(String id) {
        JSONObject others = new JSONObject();
        ArrayList<String> ID = new ArrayList<String>();
        ArrayList<String> Name = new ArrayList<String>();
        ArrayList<Double> Price = new ArrayList<Double>();
        JSONArray Img = new JSONArray();
        ArrayList<String> Mobile = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select *  from ants_product where ap_mobile="
                        + "(select ap_mobile from ants_product where ap_id=?" + ")";
                statement = connection.prepareStatement(sql);
                statement.setString(1, id);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    ID.add(resultset.getString("ap_id"));
                    Name.add(resultset.getString("ap_name"));
                    Price.add(resultset.getDouble("ap_price"));
                    String fileName = resultset.getString("ap_file_name");
                    Mobile.add(resultset.getString("ap_mobile"));
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                }
                int total = 0;
                for (String m : Mobile) {
                    total = totalUserProducts(m);
                }

                others.put("total", total);
                others.put("otherGoodsID", ID);
                others.put("otherGoodsName", Name);
                others.put("OtherGoodsPrice", Price);
                others.put("otherGoodsImg", Img);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return others;
    }

    // 获取子类所有商品的一共的页数
    public static int totalPageByStatus(int count, int status) {
        int totalPage = 1;
        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select count(*) from ants_product where ap_status=? ";

                statement = connection.prepareStatement(sql);
                statement.setInt(1, status);
                resultset = statement.executeQuery();
                while (resultset.next()) {

                    int sum = resultset.getInt(1);      //获取数据的总数
                    if (sum % count == 0) {                //判断是否能整除
                        totalPage = sum / count;
                    } else {
                        totalPage = (sum / count) + 1;    //结果加一
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }
        return totalPage;
    }

    // 根据当前页数来选择不同状态(发布闲置，发布寻求)的商品
    public static JSONObject selectProductByStatus(int status, int currentPage, int count) {
        JSONObject product = new JSONObject();
        ArrayList<String> ID = new ArrayList<String>();
        ArrayList<String> Name = new ArrayList<String>();
        ArrayList<Double> Price = new ArrayList<Double>();
        ArrayList<String> Introduce = new ArrayList<String>();
        ArrayList<String> Ways = new ArrayList<String>();
        ArrayList<String> Bargin = new ArrayList<String>();
        ArrayList<String> Source = new ArrayList<String>();
        JSONArray Img = new JSONArray();
        ArrayList<String> UserImg = new ArrayList<String>();
        ArrayList<String> UserName = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select * from ants_product where ap_status=? limit ?,?";

                statement = connection.prepareStatement(sql);
                statement.setInt(1, status);

                statement.setInt(2, currentPage * count);
                statement.setInt(3, count);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    ID.add(resultset.getString("ap_id"));
                    Name.add(resultset.getString("ap_name"));
                    Price.add(resultset.getDouble("ap_price"));
                    Introduce.add(resultset.getString("ap_introduce"));
                    Ways.add(resultset.getString("ap_ways"));
                    Bargin.add(resultset.getString("ap_bargin"));
                    String fileName = resultset.getString("ap_file_name");
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                }

                for (String Id : ID) {
                    JSONObject user = UsersDao.selectUser(Id);
                    UserImg.add(user.getString("userImg"));
                    UserName.add(user.getString("userName"));
                    Source.add(user.getString("userAddress"));
                }

                product.put("userImg", UserImg);
                product.put("userName", UserName);
                product.put("totalPage", ProductDao.totalPageByStatus(32, status));
                product.put("goodsID", ID);
                product.put("goodsName", Name);
                product.put("goodsPrice", Price);
                product.put("goodsIntroduce", Introduce);
                product.put("goodsWays", Ways);
                product.put("goodsBargin", Bargin);
                product.put("goodsSource", Source);
                product.put("goodsImg", Img);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return product;
    }


    // 根据分类名字获取childrenID来获取商品信息
    public static JSONObject selectProductByClassNameLowOrHigh(String name, int currentPage, int count, String type) {
        JSONObject product = new JSONObject();
        ArrayList<String> ID = new ArrayList<String>();
        ArrayList<String> Name = new ArrayList<String>();
        ArrayList<Double> Price = new ArrayList<Double>();
        ArrayList<String> Introduce = new ArrayList<String>();
        ArrayList<String> Ways = new ArrayList<String>();
        ArrayList<String> Bargin = new ArrayList<String>();

        JSONArray Img = new JSONArray();
        ArrayList<String> classify = new ArrayList<String>();

        ArrayList<String> UserImg = new ArrayList<String>();
        ArrayList<String> UserName = new ArrayList<String>();
        ArrayList<String> Source = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "";
                int id = ProductCategoryDao.selectIDByName(name);
                if (id < 9) {
                    if (type.equals("1")) {
                        sql = "select * from ants_product where apc_id=? order by ap_price limit ?,?   ";
                    } else {
                        sql = "select * from ants_product where apc_id=? order by ap_price desc limit ?,? ";
                    }

                } else {
                    if (type.equals("1")) {
                        sql = "select * from ants_product where apc_child_id=(select"
                                + " apc_id from ants_product_category where apc_name= ?" + ") order by ap_price limit ?,?  ";
                    } else {
                        sql = "select * from ants_product where apc_child_id=(select"
                                + " apc_id from ants_product_category where apc_name= ?" + ") order by ap_price desc limit ?,? ";
                    }

                }

                statement = connection.prepareStatement(sql);
                if (id < 7) {
                    statement.setDouble(1, id);
                } else {
                    statement.setString(1, name);
                }

                statement.setInt(2, currentPage * count);
                statement.setInt(3, count);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    ID.add(resultset.getString("ap_id"));
                    Name.add(resultset.getString("ap_name"));
                    Price.add(resultset.getDouble("ap_price"));
                    Introduce.add(resultset.getString("ap_introduce"));
                    Ways.add(resultset.getString("ap_ways"));
                    Bargin.add(resultset.getString("ap_bargin"));
                    String fileName = resultset.getString("ap_file_name");
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                }

                for (String Id : ID) {
                    JSONObject user = UsersDao.selectUser(Id);
                    UserImg.add(user.getString("userImg"));
                    UserName.add(user.getString("userName"));
                    Source.add(user.getString("userAddress"));
                }

                classify = ProductCategoryDao.selectClassName(name);
                product.put("userImg", UserImg);
                product.put("userName", UserName);
                product.put("totalPage", ProductDao.totalPageByChildName(20, name));
                product.put("classify", classify);
                product.put("goodsID", ID);
                product.put("goodsName", Name);
                product.put("goodsPrice", Price);
                product.put("goodsIntroduce", Introduce);
                product.put("goodsWays", Ways);
                product.put("goodsSource", Source);
                product.put("goodsBargin", Bargin);
                product.put("goodsImg", Img);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return product;
    }


    // 根据分类名字获取childrenID来获取商品信息
    public static JSONObject selectProductByNameBetweenPrice(double price1, double price2, String name, int currentPage, int count) {
        JSONObject product = new JSONObject();
        ArrayList<String> ID = new ArrayList<String>();
        ArrayList<String> Name = new ArrayList<String>();
        ArrayList<Double> Price = new ArrayList<Double>();
        ArrayList<String> Introduce = new ArrayList<String>();
        ArrayList<String> Ways = new ArrayList<String>();
        ArrayList<String> Bargin = new ArrayList<String>();

        JSONArray Img = new JSONArray();
        ArrayList<String> classify = new ArrayList<String>();

        ArrayList<String> UserImg = new ArrayList<String>();
        ArrayList<String> UserName = new ArrayList<String>();
        ArrayList<String> Source = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "";
                int id = ProductCategoryDao.selectIDByName(name);
                if (id < 9) {
                    sql = "select * from ants_product where apc_id=? and ap_price>=? and ap_price<=?  limit ?,?";
                } else {
                    sql = "select * from ants_product where apc_child_id=(select"
                            + " apc_id from ants_product_category where apc_name= ?" + ") and ap_price>=? and ap_price<=? limit ?,?";
                }

                statement = connection.prepareStatement(sql);
                if (id < 7) {
                    statement.setDouble(1, id);
                    statement.setDouble(2, price1);
                    statement.setDouble(3, price2);
                } else {
                    statement.setString(1, name);
                    statement.setDouble(2, price1);
                    statement.setDouble(3, price2);
                }

                statement.setInt(4, currentPage * count);
                statement.setInt(5, count);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    ID.add(resultset.getString("ap_id"));
                    Name.add(resultset.getString("ap_name"));
                    Price.add(resultset.getDouble("ap_price"));
                    Introduce.add(resultset.getString("ap_introduce"));
                    Ways.add(resultset.getString("ap_ways"));
                    Bargin.add(resultset.getString("ap_bragin"));
                    String fileName = resultset.getString("ap_file_name");
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                }

                for (String Id : ID) {
                    JSONObject user = UsersDao.selectUser(Id);
                    UserImg.add(user.getString("userImg"));
                    UserName.add(user.getString("userName"));
                    Source.add(user.getString("userAddress"));
                }

                classify = ProductCategoryDao.selectClassName(name);
                product.put("userImg", UserImg);
                product.put("userName", UserName);
                product.put("totalPage", ProductDao.totalPageByChildName(20, name));//////////////////
                product.put("classify", classify);
                product.put("goodsID", ID);
                product.put("goodsName", Name);
                product.put("goodsPrice", Price);
                product.put("goodsIntroduce", Introduce);
                product.put("goodsWays", Ways);
                product.put("goodsSource", Source);
                product.put("goodsBargin", Bargin);
                product.put("goodsImg", Img);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return product;
    }

    // 根据分类名字获取childrenID来获取商品信息
    public static JSONObject selectProductByName(String name, int currentPage, int count) {
        JSONObject product = new JSONObject();
        ArrayList<String> ID = new ArrayList<String>();
        ArrayList<String> Name = new ArrayList<String>();
        ArrayList<Double> Price = new ArrayList<Double>();
        ArrayList<String> Introduce = new ArrayList<String>();
        ArrayList<String> Ways = new ArrayList<String>();
        ArrayList<String> Bargin = new ArrayList<String>();

        JSONArray Img = new JSONArray();
        ArrayList<String> classify = new ArrayList<String>();

        ArrayList<String> UserImg = new ArrayList<String>();
        ArrayList<String> UserName = new ArrayList<String>();
        ArrayList<String> Source = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "";
                int id = ProductCategoryDao.selectIDByName(name);
                if (id < 9) {
                    sql = "select * from ants_product where apc_id=? limit ?,?";
                } else {
                    sql = "select * from ants_product where apc_child_id=(select"
                            + " apc_id from ants_product_category where apc_name= ?" + ") limit ?,?";
                }

                statement = connection.prepareStatement(sql);
                if (id < 7) {
                    statement.setDouble(1, id);
                } else {
                    statement.setString(1, name);
                }

                statement.setInt(2, currentPage * count);
                statement.setInt(3, count);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    ID.add(resultset.getString("ap_id"));
                    Name.add(resultset.getString("ap_name"));
                    Price.add(resultset.getDouble("ap_price"));
                    Introduce.add(resultset.getString("ap_introduce"));
                    Ways.add(resultset.getString("ap_ways"));
                    Bargin.add(resultset.getString("ap_bargin"));
                    String fileName = resultset.getString("ap_file_name");
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                }

                for (String Id : ID) {
                    JSONObject user = UsersDao.selectUser(Id);
                    UserImg.add(user.getString("userImg"));
                    UserName.add(user.getString("userName"));
                    Source.add(user.getString("userAddress"));
                }

                classify = ProductCategoryDao.selectClassName(name);
                product.put("userImg", UserImg);
                product.put("userName", UserName);
                product.put("totalPage", ProductDao.totalPageByChildName(20, name));//////////////////
                product.put("classify", classify);
                product.put("goodsID", ID);
                product.put("goodsName", Name);
                product.put("goodsPrice", Price);
                product.put("goodsIntroduce", Introduce);
                product.put("goodsWays", Ways);
                product.put("goodsSource", Source);
                product.put("goodsBargin", Bargin);
                product.put("goodsImg", Img);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return product;
    }

    // 获取次要分类名单
    public static JSONObject selectClassify() {
        JSONObject product = new JSONObject();
        ArrayList<String> classify = new ArrayList<String>();

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select apc_name from ants_product_category where apc_id>=?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, 9);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    classify.add(resultset.getString("apc_name"));
                }
                product.put("classify", classify);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return product;
    }

    public static JSONObject selectShop(String id) {
        JSONObject product = new JSONObject();
        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select * from ants_product where ap_id=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, id);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    product.put("goodsID", resultset.getString("ap_id"));
                    product.put("goodsName", resultset.getString("ap_name"));
                    String fileName = resultset.getString("ap_file_name");
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    product.put("goodsImg", img);
                }


            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(null, statement, null);
            }
        }

        return product;
    }


    public static JSONObject selectProduct(String id) {
        JSONObject product = new JSONObject();
        JSONArray Img = new JSONArray();
        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                //更新数据,观看+1
                String updateSql = "update ants_product set ap_view_count = ap_view_count + 1 where ap_id = ?";
                statement = connection.prepareStatement(updateSql);
                statement.setString(1, id);
                statement.execute();

                String sql = "select * from ants_product where ap_id=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, id);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    ArrayList<String> img = new ArrayList<String>();
                    product.put("goodsID", resultset.getString("ap_id"));
                    product.put("goodsMobile", resultset.getString("ap_mobile"));
                    product.put("goodsName", resultset.getString("ap_name"));
                    product.put("goodsPrice", resultset.getDouble("ap_price"));
                    product.put("goodsIntroduce", resultset.getString("ap_introduce"));
                    product.put("goodsWays", resultset.getString("ap_ways"));
                    product.put("goodsBargin", resultset.getString("ap_bargin"));
                    product.put("parentId", resultset.getString("apc_id"));
                    product.put("childId", resultset.getString("apc_child_id"));
                    String fileName = resultset.getString("ap_file_name");
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    Img.add(img);
                    product.put("goodsImg", Img);
                    product.put("status", resultset.getString("ap_status"));
                }
                JSONObject user = UsersDao.selectUser(id);

                System.out.println(user.toString());

                product.put("userName", user.getString("userName"));
                product.put("userImg", user.getString("userImg"));
                product.put("userMobile", user.getString("userMobile"));
                product.put("userWechat", user.getString("userWechat"));
                product.put("userQQ", user.getString("userQQ"));
                product.put("userAddress", user.getString("userAddress"));
                product.put("wcHide", user.getString("wcHide"));
                product.put("qqHide", user.getString("qqHide"));
                product.put("userid", user.getString("userid"));

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return product;
    }

    // 选择对应商品id的路径
    public static String selectPath(String id) {

        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        String Path = "";
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select ap_file_name from ants_product where ap_id=?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, id);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    Path = resultset.getString("ap_file_name");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

        return Path;
    }

    // 插入数据
    public static int insert(ProductBean user) {
        String sql = "insert into ants_product (ap_id,ap_mobile,ap_name,ap_price,ap_introduce,ap_ways" +
                ",ap_bargin,apc_child_id,apc_id,ap_file_name,ap_status) values(?,?,?,?,?,?,?,?,?,?,?)";
        Object[] params = {user.getId(), user.getMobile(), user.getName(), user.getPrice(), user.getIntroduce(), user.getWays(),
                user.getBargin(), user.getApcChildId(), user.getApcId(), user.getFileName(), user.getStatus()};
        return BaseDao.exectuIUD(sql, params);
    }

    // 更新数据
    public static int update(ProductBean user) {
        // 判断文件路径是否存在
        System.out.println("fileName" + user.getFileName());
        if (user.getFileName() != null) {// 文件路径存在
            String sql = "update ants_product set ap_name=?," + "ap_price=?," + "ap_introduce=?," + "ap_ways=?,"
                    + "ap_bargin=?," + "ap_file_name=?," + "apc_id=?," + "apc_child_id=?" + " where ap_id=?";

            Object[] params = {user.getName(), user.getPrice(), user.getIntroduce(), user.getWays(), user.getBargin(),
                    user.getFileName(), user.getApcId(), user.getApcChildId(), user.getId()};
            return BaseDao.exectuIUD(sql, params);
        } else {// 文件路径不存在
            String sql = "update ants_product set ap_name=?," + "ap_price=?," + "ap_introduce=?," + "ap_ways=?,"
                    + "ap_bargin=?," + "apc_id=?," + "apc_child_id=?" + " where ap_id=?";
            Object[] params = {user.getName(), user.getPrice(), user.getIntroduce(), user.getWays(), user.getBargin(),
                    user.getApcId(), user.getApcChildId(), user.getId()};
            return BaseDao.exectuIUD(sql, params);
        }

    }

    // 删除某一行数据
    public static int delete(String id) {
        String sql = "delete from ants_product where ap_id=?";
        Object[] params = {id};
        return BaseDao.exectuIUD(sql, params);

    }

    // 获取子类所有商品的一共的页数
    public static int totalPageByChildName(int count, String name) {
        int totalPage = 1;
        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "";
                int id = ProductCategoryDao.selectIDByName(name);
                if (id < 9) {
                    sql = "select count(*) from ants_product where apc_id=?";
                } else {
                    sql = "select count(*) from ants_product where apc_child_id=(select"
                            + " apc_id from ants_product_category where apc_name= ?" + ")";
                }
                statement = connection.prepareStatement(sql);
                if (id < 9) {
                    statement.setDouble(1, id);
                } else {
                    statement.setString(1, name);
                }
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    int sum = resultset.getInt(1);        //获取商品的总数量
                    if (sum % count == 0) {
                        totalPage = sum / count;
                    } else {
                        totalPage = (sum / count) + 1;
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }
        return totalPage;
    }


    public static JSONArray getData(int childid, int count, String type) {
        JSONArray message = new JSONArray();
        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select * from ants_product where apc_child_id=? order by ap_id  desc limit ?,? ";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, childid);
                statement.setInt(2, 0);
                statement.setInt(3, count);// 浏览多少行下标0,1,2,3
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    if (type.equals("id")) {
                        message.add(resultset.getString("ap_id"));
                    } else if (type.equals("name")) {
                        message.add(resultset.getString("ap_name"));
                    } else {
                        ArrayList<String> img = new ArrayList<String>();
                        String fileName = resultset.getString("ap_file_name");
                        String fn[] = fileName.split(";");
                        for (String filename : fn) {
                            img.add(filename);
                        }
                        message.add(img);
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }
        return message;
    }

    //将数据库中的数据同步到ES中
    public static void insertAll() {
        int sum = 0;
        Connection connection = null;
        java.sql.PreparedStatement statement = null;
        ResultSet resultset = null;
        TransportClient client = ElasticSearchDao.getTransportClient();
        if (connection == null) {
            connection = BaseDao.getConnection();
            try {
                String sql = "select *  from ants_product ";
                statement = connection.prepareStatement(sql);
                resultset = statement.executeQuery();
                while (resultset.next()) {
                    sum++;
                    JSONObject product = new JSONObject();
                    ArrayList<String> img = new ArrayList<String>();
                    String id = resultset.getString("ap_id");
                    product.put("goodsID", id);
                    product.put("goodsName", resultset.getString("ap_name"));
                    product.put("goodsPrice", resultset.getDouble("ap_price"));
                    product.put("goodsIntroduce", resultset.getString("ap_introduce"));
                    product.put("goodsWays", resultset.getString("ap_status"));
                    product.put("goodsBargin", resultset.getString("ap_bargin"));
                    product.put("Status", resultset.getString("ap_status"));
                    String fileName = resultset.getString("ap_file_name");
                    String fn[] = fileName.split(";");
                    for (String filename : fn) {
                        img.add(filename);
                    }
                    product.put("goodsImg", img);
                    JSONObject user = UsersDao.selectUser(id);
                    product.put("userImg", user.getString("userImg"));
                    product.put("userName", user.getString("userName"));
                    product.put("goodsSource", user.getString("userAddress"));

                    ElasticSearchDao.insertData(product, client);
                }
                System.out.println("插入成功");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                BaseDao.closeResource(resultset, statement, null);
            }
        }

    }

    public static void main(String[] args) {
        insertAll();
    }

}
