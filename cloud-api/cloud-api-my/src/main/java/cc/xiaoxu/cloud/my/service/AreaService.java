package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.core.utils.text.StringUtils;
import cc.xiaoxu.cloud.my.dao.AreaMapper;
import cc.xiaoxu.cloud.my.entity.Area;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class AreaService extends ServiceImpl<AreaMapper, Area> {

    private final AmapManager amapManager;

    private static String fill(String addressCode) {

        // 如果 addressCode 位数不足 12，在后面补 0
        if (addressCode.length() < 12) {
            return addressCode + StringUtils.addMultiple("0", 12 - addressCode.length());
        }
        return addressCode;
    }
}