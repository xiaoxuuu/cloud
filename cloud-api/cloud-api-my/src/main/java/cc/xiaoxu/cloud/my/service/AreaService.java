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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class AreaService extends ServiceImpl<AreaMapper, Area> {

    private final AmapManager amapManager;

    public Map<String, String> getLocation(List<String> addressCode) {

        List<Area> areaList = lambdaQuery().in(Area::getCode, addressCode).list();
        return areaList.stream()
                .peek(area -> {
                    String location = null;
                    if ("1".equals(area.getLevel())) {
                        location = amapManager.getPoiLocation(area.getName() + "政府", null);
                    } else if ("2".equals(area.getLevel())) {
                        location = amapManager.getPoiLocation(area.getName() + "政府", area.getName());
                    } else if ("3".equals(area.getLevel())) {
                        Area one = lambdaQuery().eq(Area::getShortCode, area.getShortCode().substring(0, 4)).one();
                        location = amapManager.getPoiLocation(one.getName() + area.getName() + "政府", one.getName());
                    }
                    area.setLocation(location);
                    lambdaUpdate()
                            .eq(Area::getId, area.getId())
                            .set(Area::getLocation, location)
                            .set(Area::getUpdateTime, System.currentTimeMillis())
                            .update();
                })
                .collect(Collectors.toMap(Area::getCode, Area::getLocation));
    }

    private static String fill(String addressCode) {

        // 如果 addressCode 位数不足 12，在后面补 0
        if (addressCode.length() < 12) {
            return addressCode + StringUtils.addMultiple("0", 12 - addressCode.length());
        }
        return addressCode;
    }
}