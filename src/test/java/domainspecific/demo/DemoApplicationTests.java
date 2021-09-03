package domainspecific.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.project.model.Wishlist;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        Itemm itemm = new Itemm();
        itemm.setNum(1);
        itemm.setStr("b0da8ca9-d935-4455-8d06-d9c10b1f8bc4");
        String result = JSONObject.toJSONString(itemm);
        System.out.println(result);
    }

}
class Itemm{
    int num;
    String id;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getStr() {
        return id;
    }

    public void setStr(String str) {
        this.id = str;
    }
}
