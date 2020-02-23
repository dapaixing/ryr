import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String ,Object> hashmap = new HashMap<>();
        hashmap.put("name","曹操");
        hashmap.put("skill1","剑气");
        hashmap.put("skill2","三段斩");
        hashmap.put("skill3","加攻击并吸血");
        hashmap.put("skill4","加攻速");

        //通过map转换成一个JSON结构的字符串
        //1、创建一个gson对象
        Gson gson = new GsonBuilder().create();
        //2、使用toJson方法把键值对结构转换成JSON字符串
        String s = gson.toJson(hashmap);
        System.out.println(s);
    }
}
