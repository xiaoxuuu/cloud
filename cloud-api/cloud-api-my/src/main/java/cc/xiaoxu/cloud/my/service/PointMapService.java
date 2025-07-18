package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.my.dao.PointMapMapper;
import cc.xiaoxu.cloud.my.entity.PointMap;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointMapService extends ServiceImpl<PointMapMapper, PointMap> {

}