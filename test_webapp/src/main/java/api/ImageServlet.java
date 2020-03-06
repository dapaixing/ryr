package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //考虑查看所有图片属性和查看指定图片属性
        //通过URL中带有imageId参数来进行区分
        //存在imageId查看指定图片属性，否则就查看所有图片属性
        //例如：URL/image?imageId=100
        //imageId的值就是"100"
        //如果URL中不存在imageId那么返回null
        String imageId = req.getParameter("imageId");
        if (imageId == null || imageId.equals("")){
            //查看所有图片属性
            selectAll(req,resp);
        }else {
            //查看指定图片属性
            selectOne(imageId,resp);
        }
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=utf-8");
        //1、创建一个ImageDao对象
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(Integer.parseInt(imageId));
        //2、使用gson把查到的数据转换成json格式，并写回给响应对象
        Gson gson = new GsonBuilder().create();
        String jsonData = gson.toJson(image);
        resp.getWriter().write(jsonData);
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=utf-8");
        //创建一个ImageDao对象，并查找数据库
        ImageDao imageDao = new ImageDao();
        List<Image> images = imageDao.selectAll();
        //把查找到的结果转成JSON格式的字符串，并写回给resp对象
        Gson gson = new GsonBuilder().create();
        //  jsonData就是一个json格式的字符串了，就和之前约定的格式是一样的
        //  gson帮助我们自动完成了大量的格式转换工作
        //  只要把之前的相关字段都约定成统一命名，下面的操作就可以一步到位的完成整个转换
        String jsonData = gson.toJson(images);
        resp.getWriter().write(jsonData);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、获取图片的属性信息，并存入数据库
        //a）创建一个factory对象和upload对象,为了获取图片属性所做的工作
        //  固定逻辑
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        //b）通过upload对象进一步解析请求（解析HTTP请求中奇怪的body中的内容）
        // FileItem就代表一个上传的文件对象
        //理论上来说，HTTP支持一个请求中同时上传多个文件
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            //出现异常说明解析错误
            e.printStackTrace();
            //告诉客户端出现的具体的错误是啥
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{\"ok\": false,\"reason\":\"请求解析意外\"}");
            return;
        }
        //c）把FileItem中的属性提取出来，转换成Image对象，才能存到数据库中
        //      当前只考虑一张图片的情况
        FileItem fileItem = items.get(0);
        Image image = new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int) fileItem.getSize());
        //手动获取一下当前日期，并转成格式化日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
        //自己构造一个路径来保存，引入时间戳是为了让文件路径能够唯一
        image.setPath("D:\\代码\\2020\\test_webapp\\src\\image\\" + System.currentTimeMillis() + "" + image.getMd5());
        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
        //存入数据库
        ImageDao imageDao = new ImageDao();
        //看数据库中是否存在相同Md5值的图片，不存在，返回null
        Image existImage = imageDao.selectByMd5(image.getMd5());
        imageDao.insert(image);
        //2、获取图片的内容信息，并写入磁盘信息
        if (existImage == null) {
            File file = new File(image.getPath());
            try {
                fileItem.write(file);
            } catch (Exception e) {
                e.printStackTrace();

                resp.setContentType("application/json; charset=utf-8");
                resp.getWriter().write("{\"ok\": false,\"reason\":\"写入磁盘失败\"}");
            }
            //3、给客户端返回一个结果数据
//        resp.setContentType("application/json; charset=utf-8");
//        resp.getWriter().write("{\"ok\": true}");
            resp.sendRedirect("index.html");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、先获取到请求中的imageId
        String imageId = req.getParameter("imageId");
        if (imageId == null || imageId.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"解析请求失败\"}");
            return;
        }
        //2、创建ImageDao对象，查看到该图片对象对应的相关属性（这是为了知道这个图片对应的文件路径）
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(Integer.parseInt(imageId));
        if (image == null){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reason\":\"imageId在数据库中不存在\"}");
            return;
        }
        //3、删除数据库中的记录
        imageDao.delete(Integer.parseInt(imageId));
        //4、删除本地磁盘文件
        File file = new File(image.getPath());
        file.delete();
        resp.setStatus(200);
        resp.getWriter().write("{\"ok\":true}");
    }
}
