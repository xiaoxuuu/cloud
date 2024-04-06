package cc.xiaoxu.cloud.core.task;

import cc.xiaoxu.cloud.core.decode.DecodeInterceptor;
import cc.xiaoxu.cloud.core.service.EncodeTableService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class EncodeTableTask {

    @Value("${cloud.confusion:false}")
    private Boolean confusionEnable;

    @Resource
    private EncodeTableService encodeTableService;

    @Resource
    private DecodeInterceptor decodeInterceptor;

    @PostConstruct
    public void init() {

        // 根据配置判断是否需要加密数据库信息，此功能会自动单向加密数据库，请谨慎使用
        if (confusionEnable) {
            encodeTableService.encodeTable();
            decodeInterceptor.setConfusionEnable(confusionEnable);
        }
    }
}