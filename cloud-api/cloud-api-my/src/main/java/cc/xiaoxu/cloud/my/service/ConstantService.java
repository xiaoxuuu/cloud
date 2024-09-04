package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.my.dao.ConstantMapper;
import cc.xiaoxu.cloud.my.entity.Constant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
public class ConstantService extends ServiceImpl<ConstantMapper, Constant> {

}