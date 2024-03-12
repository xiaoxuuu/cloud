package cc.xiaoxu.cloud.api.demo.valueError;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = {"/value"})
public class SdkController {

    @GetMapping(value = {"/get"}, name = "自研AI分析结果")
    public String get() {
        return SdkUtil.getDir();
    }
}