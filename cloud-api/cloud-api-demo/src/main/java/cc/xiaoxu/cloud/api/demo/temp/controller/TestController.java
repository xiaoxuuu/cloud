package cc.xiaoxu.cloud.api.demo.temp.controller;

import cc.xiaoxu.cloud.api.demo.temp.bean.R;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(path = {"/ai/aibox"})
public class TestController {

    @RequestMapping(value = {"/analyze"}, name = "AI分析结果")
    public R<Void> analyze(@RequestParam(name = "StructData", required = false) String structData, @RequestParam(name = "ImageData", required = false) MultipartFile imageData, HttpServletRequest request) {
//        log.error("================================================================================================");
//        log.info("接收到 AI 分析结果 StructData：{}", structData);
//        log.info("接收到 AI 分析结果图片大小：{}", imageData != null ? imageData.getSize() : 0L);
        if (imageData != null && imageData.getSize() > 0L) {
//            log.info("接收到 AI 分析结果图片名：{}", imageData.getOriginalFilename());
        }
//        log.error("================================================================================================");
        return R.ok();
    }

    @RequestMapping(value = {"/research"}, name = "自研AI分析结果")
    public R<Void> research(@RequestParam(name = "StructData", required = false) String structData, @RequestParam(name = "ImageData", required = false) MultipartFile imageData, HttpServletRequest request) {
        String nowTime = DateUtils.getNowTime();
        log.error("┌── 接收到数据：" + nowTime);
        log.info("├── StructData：{}", structData);
        log.info("├── ImageData 图片大小：{}", imageData != null ? imageData.getSize() : 0L);
        if (imageData != null && imageData.getSize() > 0L) {
            log.info("├── ImageData 图片名：{}", imageData.getOriginalFilename());
        } else {
            log.info("├── ImageData 图片名不存在");
        }
        log.error("└── " + nowTime);
        return R.ok();
    }
}