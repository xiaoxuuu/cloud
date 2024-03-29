package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.core.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.cache.CacheService;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.my.bean.constant.CacheConstant;
import cc.xiaoxu.cloud.my.bean.es.NavWebsiteEs;
import cc.xiaoxu.cloud.my.bean.mysql.NavWebsite;
import cc.xiaoxu.cloud.my.bean.mysql.NavWebsiteIcon;
import cc.xiaoxu.cloud.my.bean.vo.NavWebsiteAddVisitNumVO;
import cc.xiaoxu.cloud.my.bean.vo.NavWebsiteSearchVO;
import cc.xiaoxu.cloud.my.bean.vo.NavWebsiteShowVO;
import cc.xiaoxu.cloud.my.dao.es.NavWebsiteEsMapper;
import cc.xiaoxu.cloud.my.dao.mysql.NavWebsiteMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.OrderByParam;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class NavWebsiteService extends ServiceImpl<NavWebsiteMapper, NavWebsite> {

    @Resource
    private NavWebsiteIconService navWebsiteIconService;

    @Resource
    private NavWebsiteEsMapper navWebsiteEsMapper;

    @Resource
    private CacheService cacheService;

    /**
     * 数据缓存
     */
    @Setter
    private List<NavWebsite> navList = new ArrayList<>();

    /**
     * 搜索
     */
    public List<NavWebsiteShowVO> search(NavWebsiteSearchVO vo) {

        List<NavWebsiteEs> list = EsWrappers.lambdaChainQuery(navWebsiteEsMapper)
                .like(NavWebsiteEs::getShortName, vo.getKeyword()).or()
                .like(NavWebsiteEs::getWebsiteName, vo.getKeyword()).or()
                .like(NavWebsiteEs::getUrl, vo.getKeyword()).or()
                .like(NavWebsiteEs::getDescription, vo.getKeyword()).or()
                .like(NavWebsiteEs::getLabel, vo.getKeyword()).or()
                .like(NavWebsiteEs::getType, vo.getKeyword()).or()
                .like(NavWebsiteEs::getRemark, vo.getKeyword()).or()
//                .orderBy(true, false, NavWebsiteEs::getVisitNum, NavWebsiteEs::getId)
                .orderBy(true, getOrder())
                .limit(30)
                .list();
        Map<String, NavWebsiteIcon> map = cacheService.getCacheObject(CacheConstant.NAV_ICON_MAP);
        return list.stream()
                // 转换类型
                .map(k -> tran(k, map))
                .toList();

    }

    private List<OrderByParam> getOrder() {

        OrderByParam id = new OrderByParam();
        id.setOrder("_id");
        id.setSort("DESC");

        OrderByParam visitNum = new OrderByParam();
        visitNum.setOrder("visitNum");
        visitNum.setSort("DESC");

        return List.of(visitNum, id);
    }

    /**
     * 模糊匹配
     */
    private boolean containsValue(String value, String keyword) {

        if (StringUtils.isBlank(value)) {
            return false;
        }
        String valueLowerCase = value.toLowerCase();
        String keywordLowerCase = keyword.toLowerCase();
        return valueLowerCase.contains(keywordLowerCase);
    }

    /**
     * 查询全量数据
     */
    public List<NavWebsite> getList() {

        LambdaQueryWrapper<NavWebsite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NavWebsite::getState, StateEnum.ENABLE.getCode());
        List<NavWebsite> list = this.list(queryWrapper);
        log.debug("查询到 {} 条已知网站收藏...", list.size());
        return list;
    }

    /**
     * 翻译 bean
     */
    private NavWebsiteShowVO tran(NavWebsiteEs navWebsite, Map<String, NavWebsiteIcon> map) {

        NavWebsiteShowVO vo = new NavWebsiteShowVO();
        BeanUtils.copyProperties(navWebsite, vo);
        vo.setTypeSet(Set.of(Optional.ofNullable(navWebsite.getType()).orElse("").split(",")));
        vo.setLabelSet(Set.of(Optional.ofNullable(navWebsite.getLabel()).orElse("").split(",")));
        vo.setLastVisitDesc(getLastVisitDesc(navWebsite.getLastAvailableTime()));
        NavWebsiteIcon navWebsiteIcon = map.getOrDefault(navWebsite.getIconId(), new NavWebsiteIcon());
        vo.setIconType(navWebsiteIcon.getType());
        // 翻译网站图标
        vo.setIcon(navWebsiteIcon.getIcon());
        return vo;
    }

    private String getLastVisitDesc(String date) {

        if (StringUtils.isBlank(date)) {
            return "从未成功";
        }
        long currentTimeMillis = System.currentTimeMillis();
        long oldDateMillis = DateUtils.stringToLocalDateTime(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long l = currentTimeMillis - oldDateMillis;
        if (l < 1000 * 60 * 60) {
            return "刚刚";
        }
        if (l < 1000 * 60 * 60 * 24) {
            return "一天内";
        }
        if (l < 1000 * 60 * 60 * 24 * 3) {
            return "三天内";
        }
        if (l < 1000 * 60 * 60 * 24 * 7) {
            return "一周内";
        }
        if (l < 1000L * 60 * 60 * 24 * 30) {
            return "30天内";
        }
        if (l < 1000L * 60 * 60 * 24 * 365) {
            return "一年内";
        }
        return "超过一年";
    }

    /**
     * 添加访问次数
     */
    public void addVisitNum(NavWebsiteAddVisitNumVO vo) {

        for (NavWebsite navWebsite : navList) {
            if (navWebsite.getId().equals(vo.getId())) {
                navWebsite.setVisitNum(navWebsite.getVisitNum() + 1);
            }
        }
        this.baseMapper.updateNum(vo.getId());
    }
}