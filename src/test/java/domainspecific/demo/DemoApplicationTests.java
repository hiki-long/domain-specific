package domainspecific.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.project.Application;
import com.company.project.dao.ItemMapper;
import com.company.project.model.Wishlist;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
class DemoApplicationTests {

    @Resource
    ItemMapper itemMapper;
    @Test
    void contextLoads() {

        String uuid = "1b33953f-fa6f-40ce-a6e0-7b11259fde7f";
        int oldversion =0;
        int newversion =1;
        int remain =3;
//        itemMapper.updateByVersion(remain,newversion,uuid,oldversion);
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
