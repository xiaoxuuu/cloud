package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.ModelInfoMapper;
import cc.xiaoxu.cloud.ai.entity.ModelInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ModelInfoService extends ServiceImpl<ModelInfoMapper, ModelInfo> {

}