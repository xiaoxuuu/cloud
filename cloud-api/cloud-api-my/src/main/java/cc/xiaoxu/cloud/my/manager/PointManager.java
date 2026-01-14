package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.vo.*;
import cc.xiaoxu.cloud.my.entity.Area;
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

    private List<PointTagVO> pointTagList = new ArrayList<>();
    private Map<Integer, PointTagVO> pointTagMap = new HashMap<>();

    private List<PointSourceAuthorVO> pointSourceAuthorList = new ArrayList<>();
    private Map<Integer, PointSourceAuthorVO> pointSourceAuthorMap = new HashMap<>();

    private List<PointSourceAuthorVO> pointSourceAuthorListAll = new ArrayList<>();

    private List<PointSourceVO> pointSourceList = new ArrayList<>();
    private Map<Integer, PointSourceVO> pointSourceMap = new HashMap<>();

    private List<PointFullVO> pointList = new ArrayList<>();
    private Map<String, PointFullVO> pointMapCode = new HashMap<>();
    private Map<String, PointShowVO> pointShowMapCode = new HashMap<>();
}