package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.my.dao.AreaMapper;
import cc.xiaoxu.cloud.my.entity.Area;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class AreaService extends ServiceImpl<AreaMapper, Area> {

    private final AmapManager amapManager;

    @SneakyThrows
    public void updateLocation() {

        List<Area> areaList = lambdaQuery()
                .isNull(Area::getLocation)
                .eq(Area::getState, StateEnum.ENABLE.getCode())
                .list();
        log.error("更新：" + areaList.size());
        for (Area area : areaList) {
            Thread.sleep(1000);
            String location = null;
            String poiName = null;
            if ("1".equals(area.getLevel())) {
                poiName = area.getName() + "政府";
                location = amapManager.getPoiLocation(poiName, null);
            } else if ("2".equals(area.getLevel())) {
                Area province = lambdaQuery().eq(Area::getShortCode, area.getShortCode().substring(0, 2)).one();
                if (null == province) {
                    lambdaUpdate()
                            .eq(Area::getId, area.getId())
                            .set(Area::getState, StateEnum.DELETE.getCode())
                            .set(Area::getUpdateTime, LocalDateTime.now())
                            .update();
                    log.error("未查询到省：" + poiName);
                    continue;
                }
                poiName = province.getName() + area.getName() + "政府";
                location = amapManager.getPoiLocation(poiName, area.getName());
            } else if ("3".equals(area.getLevel())) {
                Area province = lambdaQuery().eq(Area::getShortCode, area.getShortCode().substring(0, 2)).one();
                Area city = lambdaQuery().eq(Area::getShortCode, area.getShortCode().substring(0, 4)).one();
                if (null == city && null == province) {
                    lambdaUpdate()
                            .eq(Area::getId, area.getId())
                            .set(Area::getState, StateEnum.DELETE.getCode())
                            .set(Area::getUpdateTime, LocalDateTime.now())
                            .update();
                    log.error("未查询到省市：" + poiName);
                    continue;
                }
                String cityName = null == city ? null : city.getName();
                poiName = (province.getName() + cityName + area.getName() + "政府").replace("null", "");
                location = amapManager.getPoiLocation(poiName, cityName);
            }
            log.error("更新 " + area.getCode() + " " + poiName);
            area.setLocation(location);
            if (null == location) {
                lambdaUpdate()
                        .eq(Area::getId, area.getId())
                        .set(Area::getState, StateEnum.LOCK.getCode())
                        .set(Area::getUpdateTime, LocalDateTime.now())
                        .update();
            } else {
                lambdaUpdate()
                        .eq(Area::getId, area.getId())
                        .set(Area::getLocation, location)
                        .set(Area::getUpdateTime, LocalDateTime.now())
                        .update();
            }
        }
    }
}