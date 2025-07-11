package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.my.dao.PointSourceMapper;
import cc.xiaoxu.cloud.my.entity.PointSource;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointSourceService extends ServiceImpl<PointSourceMapper, PointSource> {

}