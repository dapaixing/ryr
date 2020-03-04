package api;

import dao.Image;
import dao.ImageDao;
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
        resp.setStatus(200);
        //这个代码就是把hello这个字符串放到http相应的body中了
        resp.getWriter().write("hello");
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
        image.setSize((int)fileItem.getSize());
        //手动获取一下当前日期，并转成格式化日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
        //自己构造一个路径来保存
        image.setPath("D:\\代码\\2020\\test_webapp\\src\\image\\"+image.getImageName());
        image.setMd5("11223344");
        //存入数据库
        ImageDao imageDao = new ImageDao();
        imageDao.insert(image);
        //2、获取图片的内容信息，并写入磁盘信息
        File file = new File(image.getPath());
        try {
            fileItem.write(file);
        } catch (Exception e) {
            e.printStackTrace();

            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{\"ok\": false,\"reason\":\"写入磁盘失败\"}");
        }
        //3、给客户端返回一个结果数据
        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().write("{\"ok\": true}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
