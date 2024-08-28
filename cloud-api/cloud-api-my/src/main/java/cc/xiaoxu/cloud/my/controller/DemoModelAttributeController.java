package cc.xiaoxu.cloud.my.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@Tag(name = "demo modelAttribute 演示")
@RestController("/demo/test")
@SessionAttributes("myModel")
public class DemoModelAttributeController {

    // 将数据存储在会话中，使用该数据时，执行方法（仅一次）
    @ModelAttribute("myModel")
    public MyModel storeData() {
        System.out.println("storeData");
        MyModel myModel = new MyModel();
        myModel.setName("John");
        myModel.setAge(30);
        return myModel;
    }

    // 从会话中获取数据
    @GetMapping("/getData")
    public String getData(HttpSession httpSession, ModelMap modelMap) {

        System.out.println(modelMap.get("myModel"));
        return ((MyModel) modelMap.get("myModel")).getName();
    }

    @Data
    public static class MyModel {

        private String name;
        private Integer age;
    }
}