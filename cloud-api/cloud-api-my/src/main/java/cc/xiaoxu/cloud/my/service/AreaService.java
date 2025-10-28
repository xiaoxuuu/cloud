package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.AreaTreeVO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<AreaTreeVO> tree() {

        List<Area> treeList = baseMapper.tree();
        return buildTree(treeList);
    }

    public List<AreaTreeVO> buildTree(List<Area> treeList) {
        // 1. 边界检查：如果数据为空，直接返回空列表
        if (treeList == null || treeList.isEmpty()) {
            return new ArrayList<>();
        }
        // 2. 预处理：将所有 Area 转换为 AreaTreeVO，并存储在 Map 中方便快速查找
        // Key: code, Value: AreaTreeVO
        Map<String, AreaTreeVO> voMap = new HashMap<>(treeList.size());

        for (Area area : treeList) {
            AreaTreeVO vo = new AreaTreeVO();
            vo.setName(area.getName());
            vo.setCode(area.getCode());
            // 初始化子列表，避免后续空指针
            vo.setChildrenList(new ArrayList<>());

            voMap.put(area.getCode(), vo);
        }
        // 3. 构建树形结构
        List<AreaTreeVO> result = new ArrayList<>();
        for (Area area : treeList) {
            AreaTreeVO currentVo = voMap.get(area.getCode());
            String level = area.getLevel();
            if ("1".equals(level)) {
                //如果是省级（Level 1），直接作为根节点加入结果集
                result.add(currentVo);
            } else if ("2".equals(level)) {
                // 如果是市级（Level 2），需要找到它的父节点
                // 逻辑：130100 (石家庄) -> 父节点代码应该是 130000 (河北)
                // 截取前两位 + "0000"
                if (area.getCode() != null && area.getCode().length() >= 2) {
                    String parentCode = area.getCode().substring(0, 2) + "0000";

                    AreaTreeVO parentVo = voMap.get(parentCode);
                    if (parentVo != null) {
                        // 将当前节点加入到父节点的 childrenList 中
                        parentVo.getChildrenList().add(currentVo);
                    }
                }
            }
        }
        return result;
    }
}