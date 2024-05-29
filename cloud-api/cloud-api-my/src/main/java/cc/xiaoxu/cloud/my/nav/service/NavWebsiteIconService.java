package cc.xiaoxu.cloud.my.nav.service;

import cc.xiaoxu.cloud.core.bean.enums.StateEnum;
import cc.xiaoxu.cloud.my.nav.bean.mysql.NavWebsiteIcon;
import cc.xiaoxu.cloud.my.nav.dao.mysql.NavWebsiteIconMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Service
public class NavWebsiteIconService extends ServiceImpl<NavWebsiteIconMapper, NavWebsiteIcon> {

    /**
     * 数据缓存
     */
    private Map<String, NavWebsiteIcon> navIconMap = new HashMap<>();

    public void setNavIconMap(List<NavWebsiteIcon> navIconList) {
        this.navIconMap = navIconList.stream().collect(Collectors.toMap(NavWebsiteIcon::getId, a -> a));
    }

    /**
     * 查询全量数据
     */
    public List<NavWebsiteIcon> getList() {

        LambdaQueryWrapper<NavWebsiteIcon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NavWebsiteIcon::getState, StateEnum.ENABLE.getCode());
        List<NavWebsiteIcon> list = this.list(queryWrapper);
        log.debug("查询到 {} 条已知网站图标...", list.size());
        return list;
    }
}