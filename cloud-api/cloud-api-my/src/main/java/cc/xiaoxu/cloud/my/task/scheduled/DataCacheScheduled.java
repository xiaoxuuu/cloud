package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.*;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.entity.*;
import cc.xiaoxu.cloud.my.manager.DataCacheManager;
import cc.xiaoxu.cloud.my.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DataCacheScheduled {

    @Resource
    private AreaService areaService;

    @Resource
    private PointService pointService;

    @Resource
    private PointSourceService pointSourceService;

    @Resource
    private PointTagService pointTagService;

    @Resource
    private PointSourceAuthorService pointSourceAuthorService;

    @Resource
    private DataCacheManager dataCacheManager;

    @Resource
    private UserService userService;

    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        // 地区
        updateAreaList();
        // 标签
        updatePointTagList();
        // 来源作者
        updatePointSourceAuthorList();
        // 来源
        updatePointSourceList();
        // 点位
        updatePointList();
        // 来源作者 - 使用中
        updatePointSourceAuthorListUsed();
        // 用户
        updateUserList();
    }

    private void updatePointSourceAuthorListUsed() {

        Set<Integer> authorIdSet = dataCacheManager.getPointList().stream().map(PointFullVO::getAuthorIdSet).flatMap(Collection::stream).collect(Collectors.toSet());
        List<PointSourceAuthorVO> list = dataCacheManager.getPointSourceAuthorList().stream()
                .filter(k -> authorIdSet.contains(k.getId()))
                .sorted(Comparator.comparing(PointSourceAuthorVO::getId))
                .toList();
        dataCacheManager.setPointSourceAuthorListAll(list);
        log.info("查询到 {} 条使用中来源作者数据...", list.size());
    }

    public void updatePointTagList() {

        List<PointTagVO> list = pointTagService.lambdaQuery()
                // 无效数据排除
                .eq(PointTag::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream()
                .map(this::toPointTagVO)
                .sorted(Comparator.comparing(PointTagVO::getSort))
                .toList();
        Map<Integer, PointTagVO> areaMap = list.stream().collect(Collectors.toMap(PointTagVO::getId, a -> a));
        dataCacheManager.setPointTagList(list);
        dataCacheManager.setPointTagMap(areaMap);
        log.info("查询到 {} 条标签数据...", list.size());
    }

    private PointTagVO toPointTagVO(PointTag entity) {
        PointTagVO vo = new PointTagVO();
        vo.setId(entity.getId());
        vo.setColor(entity.getColor());
        vo.setTagName(entity.getTagName());
        vo.setCategory(entity.getCategory());
        vo.setSort(entity.getSort());
        return vo;
    }

    public void updateAreaList() {

        List<Area> areaList = areaService.lambdaQuery()
                // 无效数据排除
                .eq(Area::getState, StateEnum.ENABLE.getCode())
                .list();
        Map<Integer, Area> areaMap = areaList.stream().collect(Collectors.toMap(a -> Integer.parseInt(a.getCode()), a -> a));
        dataCacheManager.setAreaList(areaList);
        dataCacheManager.setAreaMap(areaMap);
        log.info("查询到 {} 条地点数据...", areaList.size());
    }

    public void updatePointList() {

        List<PointFullVO> pointList = pointService.lambdaQuery()
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 无效数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list()
                .stream()
                .map(this::toPointFullVO)
                .toList();

        // 区县数据
        Map<Integer, Area> areaMap = dataCacheManager.getAreaMap();
        for (PointFullVO vo : pointList) {
            Area area = areaMap.get(vo.getAddressCode());
            if (area == null || area.getLocation()==null || !area.getLocation().contains(",")) {
                continue;
            }
            String[] lo = area.getLocation().split(",");
            double distance = calculateDistance(Double.parseDouble(vo.getLongitude()), Double.parseDouble(vo.getLatitude()),
                    Double.parseDouble(lo[1]), Double.parseDouble(lo[0]));
            vo.setDistance(distance);
        }

        Map<String, PointFullVO> pointMapCode = pointList.stream().collect(Collectors.toMap(PointFullVO::getCode, a -> a));
        dataCacheManager.setPointList(pointList);
        dataCacheManager.setPointMapCode(pointMapCode);

        // 精简参数
        Map<String, PointShowVO> pointShowMapCode = pointList.stream()
                .map(k -> (PointShowVO) BeanUtils.populate(k, PointShowVO.class))
                .collect(Collectors.toMap(PointShowVO::getCode, a -> a));
        dataCacheManager.setPointShowMapCode(pointShowMapCode);
        log.info("查询到 {} 条点位数据...", pointList.size());
    }

    /**
     * 计算两个经纬度点之间的距离（单位：公里）
     * 使用 Haversine 公式
     *
     * @param lat1 点1纬度
     * @param lon1 点1经度
     * @param lat2 点2纬度
     * @param lon2 点2经度
     * @return 距离（公里）
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371; // 地球半径（公里）

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    public void updatePointSourceList() {

        List<PointSourceVO> pointSourceList = pointSourceService.lambdaQuery()
                // 无效数据排除
                .eq(PointSource::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream().map(this::toPointSourceVO)
                .sorted(Comparator.comparing(PointSourceVO::getId))
                .toList();
        Map<Integer, PointSourceVO> pointSourceMap = pointSourceList.stream().collect(Collectors.toMap(PointSourceVO::getId, a -> a));
        dataCacheManager.setPointSourceList(pointSourceList);
        dataCacheManager.setPointSourceMap(pointSourceMap);
        log.info("查询到 {} 条点位来源数据...", pointSourceList.size());
    }

    private PointFullVO toPointFullVO(Point point) {

        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(point, vo);

        // 来源
        if (StringUtils.isNotBlank(point.getSourceIdList())) {
            List<PointSourceVO> list = Arrays.stream(vo.getSourceIdList().split(","))
                    .map(k -> dataCacheManager.getPointSourceMap().get(Integer.parseInt(k)))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(k -> k.getType().getSortingWeight() + k.getId()))
                    .toList();
            vo.setSourceList(list.stream().map(this::toPointSourceShowVO).toList());
            vo.setSourceIdSet(list.stream().map(PointSourceVO::getId).collect(Collectors.toSet()));
            vo.setAuthorIdSet(list.stream().map(PointSourceVO::getAuthorId).collect(Collectors.toSet()));
        } else {
            vo.setSourceList(Collections.emptyList());
            vo.setSourceIdSet(Collections.emptySet());
            vo.setAuthorIdSet(Collections.emptySet());
        }

        // 标签
        if (StringUtils.isNotBlank(point.getTagIdList())) {
            List<PointTagVO> list = Arrays.stream(point.getTagIdList().split(","))
                    .map(k -> dataCacheManager.getPointTagMap().get(Integer.parseInt(k)))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(PointTagVO::getSort))
                    .toList();
            vo.setTagList(list.stream().map(this::toPointTagShowVO).toList());
            vo.setTagIdSet(list.stream().map(PointTagVO::getId).collect(Collectors.toSet()));
        } else {
            vo.setTagList(Collections.emptyList());
            vo.setTagIdSet(Collections.emptySet());
        }

        // 电话
        if (StringUtils.isNotBlank(point.getTelephone())) {
            vo.setTelList(Arrays.stream(point.getTelephone().split(",")).toList());
        } else {
            vo.setTelList(Collections.emptyList());
        }



        // 模糊匹配字段
        List<String> searchValueList = new ArrayList<>();
        searchValueList.add(vo.getPointFullName());

        // 省市区
        Area areaProvince = dataCacheManager.getAreaMap().get(replaceDigit(vo.getAddressCode(), 3, 4, 0));
        if (null != areaProvince) {
            searchValueList.add(areaProvince.getName());
        }
        Area areaCity = dataCacheManager.getAreaMap().get(replaceDigit(vo.getAddressCode(), 5, 2, 0));
        if (null != areaCity) {
            searchValueList.add(areaCity.getName());
        }
        Area area = dataCacheManager.getAreaMap().get(vo.getAddressCode());
        if (null != area) {
            searchValueList.add(area.getName());
        }

        searchValueList.add(vo.getAddress());
        searchValueList.add(vo.getDescribe());
        searchValueList.addAll(vo.getTagList().stream().map(PointTagShowVO::getTagName).toList());
        searchValueList.addAll(vo.getSourceList().stream().map(PointSourceShowVO::getTitle).toList());
        searchValueList.addAll(vo.getSourceList().stream().map(PointSourceShowVO::getAuthorName).toList());
        vo.setSearchValue(String.join(",", searchValueList));
        return vo;
    }

    public static void main(String[] args) {
        long originalNum = 510101L;
        // 替换第5、6位（startPos=5，replaceLen=2）为0
        System.out.println("替换后数字：" + replaceDigit(originalNum, 3, 4, 0)); // 输出：510100
        System.out.println("替换后数字：" + replaceDigit(originalNum, 5, 2, 0)); // 输出：510100
    }

    /**
     * 替换数字指定位置的数字为指定值
     * @param originalNum 原始数字
     * @param startPos 要替换的起始位置（从1开始数，比如第5位传5）
     * @param replaceLen 替换的位数
     * @param replaceNum 替换的数字（如00对应传0）
     * @return 替换后的数字
     */
    public static int replaceDigit(long originalNum, int startPos, int replaceLen, long replaceNum) {
        // 转字符串
        String numStr = String.valueOf(originalNum);
        // 校验起始位置合法性（startPos从1开始，对应索引startPos-1）
        if (startPos < 1 || startPos + replaceLen - 1 > numStr.length()) {
            throw new IllegalArgumentException("替换位置超出数字长度");
        }
        // 计算起始索引（位置转索引：pos-1）
        int startIdx = startPos - 1;
        // 生成替换的字符串（如replaceNum=0，replaceLen=2 → "00"）
        String replaceStr = String.format("%0" + replaceLen + "d", replaceNum);

        // 执行替换
        String replacedStr = numStr.substring(0, startIdx)
                + replaceStr
                + numStr.substring(startIdx + replaceLen);

        // 转回数字
        return Integer.parseInt(replacedStr);
    }

    private PointSourceShowVO toPointSourceShowVO(PointSourceVO pointSourceVO) {

        PointSourceShowVO vo = new PointSourceShowVO();
        PointSourceAuthorVO pointSourceAuthorVO = dataCacheManager.getPointSourceAuthorMap().get(pointSourceVO.getAuthorId());
        if (pointSourceAuthorVO != null) {
            vo.setAuthorName(pointSourceAuthorVO.getName());
        }
        vo.setType(pointSourceVO.getType());
        vo.setTitle(pointSourceVO.getTitle());
        vo.setUrl(pointSourceVO.getUrl());
        return vo;
    }

    private PointTagShowVO toPointTagShowVO(PointTagVO pointTagVO) {

        PointTagShowVO vo = new PointTagShowVO();
        vo.setTagName(pointTagVO.getTagName());
        vo.setColor(pointTagVO.getColor());
        return vo;
    }

    public void updatePointSourceAuthorList() {

        List<PointSourceAuthorVO> list = pointSourceAuthorService.lambdaQuery()
                // 无效数据排除
                .eq(PointSourceAuthor::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream()
                .map(this::toPointSourceAuthorVO)
                .sorted(Comparator.comparing(PointSourceAuthorVO::getId))
                .toList();
        Map<Integer, PointSourceAuthorVO> authorMap = list.stream().collect(Collectors.toMap(PointSourceAuthorVO::getId, a -> a));
        dataCacheManager.setPointSourceAuthorList(list);
        dataCacheManager.setPointSourceAuthorMap(authorMap);
        log.info("查询到 {} 条来源作者数据...", list.size());
    }

    private PointSourceAuthorVO toPointSourceAuthorVO(PointSourceAuthor entity) {
        PointSourceAuthorVO vo = new PointSourceAuthorVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setTiktokUrl(entity.getTiktokUrl());
        vo.setRedbookUrl(entity.getRedbookUrl());
        vo.setBilibiliUrl(entity.getBilibiliUrl());
        vo.setContent(entity.getContent());
        return vo;
    }

    private PointSourceVO toPointSourceVO(PointSource entity) {
        PointSourceVO vo = new PointSourceVO();
        vo.setId(entity.getId());
        vo.setAuthorId(entity.getAuthorId());
        vo.setType(entity.getType());
        vo.setContent(entity.getContent());
        vo.setTitle(entity.getTitle());
        vo.setUrl(entity.getUrl());
        return vo;
    }

    public void updateUserList() {

        List<User> list = userService.lambdaQuery()
                // 无效数据排除
                .eq(User::getState, StateEnum.ENABLE.getCode())
                .list();
        Map<String, User> userMap = list.stream().collect(Collectors.toMap(User::getOpenId, a -> a));
        dataCacheManager.setUserList(list);
        dataCacheManager.setUserMap(userMap);
        log.info("查询到 {} 条用户数据...", list.size());
    }
}