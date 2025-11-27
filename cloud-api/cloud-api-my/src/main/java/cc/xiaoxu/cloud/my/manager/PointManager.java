package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.my.entity.*;
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
    private Map<Integer, PointTemp> pointMapCode = new HashMap<>();

    private List<PointMap> pointMapList = new ArrayList<>();
    private Map<Integer, PointMap> pointMapMap = new HashMap<>();

    private List<PointSource> pointSourceList = new ArrayList<>();
    private Map<Integer, PointSource> pointSourceMap = new HashMap<>();

    private List<PointTag> pointTagList = new ArrayList<>();
    private Map<Integer, PointTag> pointTagMap = new HashMap<>();
}