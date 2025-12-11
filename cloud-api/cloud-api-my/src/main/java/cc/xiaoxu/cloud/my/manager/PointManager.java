package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.vo.PointTagVO;
import cc.xiaoxu.cloud.my.entity.Area;
import cc.xiaoxu.cloud.my.entity.PointMap;
import cc.xiaoxu.cloud.my.entity.PointSource;
import cc.xiaoxu.cloud.my.entity.PointTemp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Service
public class PointManager {

    private List<Area> areaList = new ArrayList<>();
    private Map<Integer, Area> areaMap = new HashMap<>();

    private List<PointTemp> pointList = new ArrayList<>();
    private Map<Integer, PointTemp> pointMap = new HashMap<>();
    private Map<String, PointTemp> pointMapCode = new HashMap<>();

    private List<PointMap> pointMapList = new ArrayList<>();
    private Map<Integer, PointMap> pointMapMap = new HashMap<>();

    private List<PointSource> pointSourceList = new ArrayList<>();
    private Map<Integer, PointSource> pointSourceMap = new HashMap<>();

    private List<PointTagVO> pointTagList = new ArrayList<>();
    private Map<Integer, PointTagVO> pointTagMap = new HashMap<>();

    // 下列数据为聚合调整数据
    private List<PointTagVO> pointTagUsedList = new ArrayList<>();
}