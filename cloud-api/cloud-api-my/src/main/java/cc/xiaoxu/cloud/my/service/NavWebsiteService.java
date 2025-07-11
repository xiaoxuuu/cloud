package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.NavWebsitePageDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteAddVO;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteSearchVO;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteShowVO;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.date.DateUtils;
import cc.xiaoxu.cloud.my.dao.NavWebsiteMapper;
import cc.xiaoxu.cloud.my.entity.NavWebsite;
import cc.xiaoxu.cloud.my.entity.NavWebsiteIcon;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class NavWebsiteService extends ServiceImpl<NavWebsiteMapper, NavWebsite> {

    @Resource
    private NavWebsiteIconService navWebsiteIconService;

    /**
     * 数据缓存
     */
    @Setter
    private List<NavWebsite> navList = new ArrayList<>();

    /**
     * 搜索
     */
    public List<NavWebsiteShowVO> search(NavWebsiteSearchVO vo) {

        return navList.stream()
                // 关键字
                .filter(k -> {
                    if (StringUtils.isNotBlank(vo.getKeyword())) {
                        return containsValue(k.getShortName(), vo.getKeyword()) || containsValue(k.getWebsiteName(), vo.getKeyword()) ||
                                containsValue(k.getUrl(), vo.getKeyword()) || containsValue(k.getDescription(), vo.getKeyword());
                    }
                    return true;
                })
                // 转换类型
                .map(this::tran)
                // 按访问次数倒序
                .sorted(Comparator.comparing(NavWebsiteShowVO::getVisitNum, Comparator.reverseOrder())
                        // 按 id 倒序
                        .thenComparing(NavWebsiteShowVO::getLastAvailableTime, Comparator.reverseOrder()))
                .limit(15)
                .toList();
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
    private NavWebsiteShowVO tran(NavWebsite navWebsite) {

        NavWebsiteShowVO vo = new NavWebsiteShowVO();
        BeanUtils.copyProperties(navWebsite, vo);
        vo.setLastVisitDesc(getLastVisitDesc(navWebsite.getLastAvailableTime()));

        NavWebsiteIcon navWebsiteIcon = navWebsiteIconService.getNavIconMap().getOrDefault(navWebsite.getIconId(), new NavWebsiteIcon());
        vo.setIconType(navWebsiteIcon.getType());
        // 翻译网站图标
        vo.setIcon(navWebsiteIcon.getIcon());
        return vo;
    }

    private String getLastVisitDesc(Date date) {

        if (null == date) {
            return "从未成功";
        }
        long currentTimeMillis = System.currentTimeMillis();

        long oldDateMillis = DateUtils.toLocalDateTime(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long l = currentTimeMillis - oldDateMillis;
        if (l < 1000 * 60 * 60) {
            return "刚刚";
        }
        if (l < 1000 * 60 * 60 * 24 * 3) {
            return "近日";
        }
        if (l < 1000 * 60 * 60 * 24 * 7) {
            return "近一周";
        }
        if (l < 1000L * 60 * 60 * 24 * 30) {
            return "近一月";
        }
        if (l < 1000L * 60 * 60 * 24 * 365) {
            return "近一年";
        }
        return "超过一年";
    }

    /**
     * 添加访问次数
     */
    public void addVisitNum(IdDTO dto) {

        for (NavWebsite navWebsite : navList) {
            if (navWebsite.getId().equals(dto.getId())) {
                navWebsite.setVisitNum(navWebsite.getVisitNum() + 1);
            }
        }
        this.baseMapper.updateNum(Integer.parseInt(dto.getId()));
    }

    public Page<NavWebsiteShowVO> pages(NavWebsitePageDTO dto) {

        Page<NavWebsite> entityPage = getBaseMapper().pages(dto, new Page<>(dto.getCurrent(), dto.getSize()));
        return PageUtils.getPage(entityPage, this::tran);
    }

    public void add(NavWebsiteAddVO vo) {

        NavWebsite navWebsite = new NavWebsite();
        navWebsite.setShortName(vo.getShortName());
        navWebsite.setWebsiteName(vo.getWebsiteName());
        navWebsite.setUrl(vo.getUrl());
        navWebsite.setDescription(vo.getDescription());
        navWebsite.setIconId(vo.getIconId());
        navWebsite.setLastAvailableTime(DateUtils.toDate(LocalDateTime.now()));
        navWebsite.setVisitNum(0);
        navWebsite.setState(StateEnum.ENABLE.getCode());
        navWebsite.setCreateTime(DateUtils.toDate(LocalDateTime.now()));
        save(navWebsite);
    }
}