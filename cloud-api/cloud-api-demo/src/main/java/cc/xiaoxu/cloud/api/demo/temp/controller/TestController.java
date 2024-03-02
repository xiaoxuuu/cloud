package cc.xiaoxu.cloud.api.demo.temp.controller;

import cc.xiaoxu.cloud.api.demo.temp.bean.AiBoxHeartBeat;
import cc.xiaoxu.cloud.api.demo.temp.bean.R;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;

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

    // curl -X POST http://172.19.70.7:10001/ai/aibox/heartbeat -d '{ "DeviceId" : "1" }'
    @RequestMapping(value = {"/heartbeat"}, name = "盒子设备心跳")
    public R<Void> heartbeat(HttpServletRequest request) {

        try {
            String json = getBody(request);
            log.info("┌── 收到盒子设备心跳");
            if (StringUtils.isNotEmpty(json) && StringUtils.contains(json, "DeviceId")) {
                if (json.endsWith("=")) {
                    json = json.substring(0, json.length() - 1);
                }

                json = URLUtil.decode(json);
                AiBoxHeartBeat aiBoxHeartBeat = JsonUtils.parse(json, AiBoxHeartBeat.class);
                log.info("└── 盒子设备心跳结果：{}", JsonUtils.toString(aiBoxHeartBeat));
            } else {
                log.error("└── 盒子设备心跳读取错误：{}", json);
            }
        } catch (Exception var4) {
            log.error("└── 盒子设备心跳处理异常：", var4);
        }
        return R.ok();
    }

    @RequestMapping(value = {"/camera/heartbeat"}, name = "视频源心跳")
    public R<Void> cameraHeartBeat(HttpServletRequest request) {
        try {
            String json = getBody(request);
            log.info("┌── 视频源心跳：{}", json);
            if (StringUtils.isNotEmpty(json) && StringUtils.contains(json, "DeviceId")) {
                if (json.endsWith("=")) {
                    json = json.substring(0, json.length() - 1);
                }

                json = URLUtil.decode(json);
                AiBoxHeartBeat aiBoxHeartBeat = JsonUtils.parse(json, AiBoxHeartBeat.class);
                log.info("└── 视频源心跳结果：{}", JsonUtils.toString(aiBoxHeartBeat));
            } else {
                log.error("└── 视频源心跳读取错误：{}", json);
            }
        } catch (Exception var4) {
            log.error("└── 视频源心跳处理异常：", var4);
        }

        return R.ok();
    }

    public static String getBody(ServletRequest request) {
        try (final BufferedReader reader = request.getReader()) {
            return IoUtil.read(reader);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}