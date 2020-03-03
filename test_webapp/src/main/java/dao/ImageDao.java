package dao;

import common.JavaImageServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ImageDao {
    /*
    把 image对象插入到数据库中
     */
    public void insert(Image image){
        //1、获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2、创建并拼装SQL语句
        String sql = "insert into image_table values(null,?,?,?,?,?,?)";
        PreparedStatement statement = null;
        try {
             statement = connection.prepareStatement(sql);
            statement.setString(1,image.getImageName());
            statement.setInt(2,image.getSize());
            statement.setString(3,image.getUploadTime());
            statement.setString(4,image.getContentType());
            statement.setString(5,image.getPath());
            statement.setString(6,image.getMd5());

            //3、执行Sql语句
            int ret = statement.executeUpdate();
            if (ret != 1){
                //出现问题，抛出异常
                throw  new JavaImageServerException("插入数据库出错！");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JavaImageServerException e) {
            e.printStackTrace();
        }finally {
            //4、关闭连接和statement对象
            DBUtil.close(connection,statement,null);
        }
    }

    /*
    查找数据库中的所有图片的信息
     */
    public List<Image> selectAll(){
        List<Image> images = new ArrayList<>();
        //1、获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2、构造SQL语句
        String sql = "select * from image_table";
        //3、执行SQL语句
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        //4、处理结果集
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                Image image = new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                images.add(image);
            }
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //5、关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }
    /*
    查找数据库中的指定图片的信息
     */
    public Image selectOne(int imageId){
        //1、获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2、构造SQL语句
        String sql = "select * from image_table where imageId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        //3、执行SQL语句
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            resultSet = statement.executeQuery();
            //4、处理结果集
            if (resultSet.next()){
                Image image = new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //5、关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }
    /*
    删除数据库中的指定图片的信息
     */
    public void delete(int imageId){
        //1、获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2、拼装SQL语句
        String sql = "delete from image_table where imageId = ?";
        //3、执行SQL语句
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            int ret = statement.executeUpdate();
            if (ret!=1){
                throw new JavaImageServerException("删除数据库操作失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JavaImageServerException e) {
            e.printStackTrace();
        }finally {
            //4、关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
    }

    public static void main(String[] args) {
//        Image image = new Image();
//        image.setImageName("1.png");
//        image.setSize(100);
//        image.setUploadTime("20200301");
//        image.setContentType("image/png");
//        image.setPath("./data/1.png");
//        image.setMd5("11223344");
//        ImageDao imageDao = new ImageDao();
//        imageDao.insert(image);
        //2、测试查找所有图片信息
//        ImageDao imageDao = new ImageDao();
//        List<Image> images = imageDao.selectAll();
//        System.out.println(images);

        //3、测试查找指定图片信息
//        ImageDao imageDao = new ImageDao();
//        System.out.println(imageDao.selectOne(1));
        //4、删除指定图片
        ImageDao imageDao = new ImageDao();
        imageDao.delete(1);
    }
}