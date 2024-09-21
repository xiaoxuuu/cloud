package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.NavWebsitePageDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteAddVisitNumVO;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteSearchVO;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteShowVO;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.PageUtils;
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

import java.time.ZoneId;
import java.util.*;

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
                        return containsValue(k.getShortName(), vo.getKeyword()) ||
                                containsValue(k.getWebsiteName(), vo.getKeyword()) ||
                                containsValue(k.getUrl(), vo.getKeyword()) ||
                                containsValue(k.getDescription(), vo.getKeyword());
                    }
                    return true;
                })
                // 转换类型
                .map(this::tran)
                // 按照类型过滤
                .filter(k -> {
                    if (StringUtils.isNotBlank(vo.getType())) {
                        return k.getTypeSet().contains(vo.getType());
                    }
                    return true;
                })
                // 按照标签过滤
                .filter(k -> {
                    if (StringUtils.isNotBlank(vo.getLabel())) {
                        return k.getLabelSet().contains(vo.getLabel());
                    }
                    return true;
                })
                // 按访问次数倒序
                .sorted(Comparator.comparing(NavWebsiteShowVO::getVisitNum, Comparator.reverseOrder())
                        // 按 id 倒序
                        .thenComparing(NavWebsiteShowVO::getId, Comparator.reverseOrder()))
                .limit(30)
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
        vo.setTypeSet(Set.of(Optional.ofNullable(navWebsite.getType()).orElse("").split(",")));
        vo.setLabelSet(Set.of(Optional.ofNullable(navWebsite.getLabel()).orElse("").split(",")));
        vo.setLastVisitDesc(getLastVisitDesc(navWebsite.getLastAvailableTime()));
        NavWebsiteIcon navWebsiteIcon = navWebsiteIconService.getNavIconMap().getOrDefault(navWebsite.getIconId(), new NavWebsiteIcon());
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
        this.baseMapper.updateNum(Integer.parseInt(vo.getId()));
    }

    public Page<NavWebsiteShowVO> pages(NavWebsitePageDTO dto) {

        Page<NavWebsite> entityPage = getBaseMapper().pages(dto, new Page<>(dto.getCurrent(), dto.getSize()));
        return PageUtils.getPage(entityPage, this::tran);
    }
}