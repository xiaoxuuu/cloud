package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.my.dao.AreaMapper;
import cc.xiaoxu.cloud.my.entity.Area;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class AreaService extends ServiceImpl<AreaMapper, Area> {

    private final AmapManager amapManager;

    public void updateLocation() {

        List<Area> areaList = lambdaQuery().isNull(Area::getLocation).list();
        for (Area area : areaList) {
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
        }
    }
}