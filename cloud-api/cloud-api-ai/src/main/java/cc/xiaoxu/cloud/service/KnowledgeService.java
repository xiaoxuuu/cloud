package cc.xiaoxu.cloud.service;

import cc.xiaoxu.cloud.dao.KnowledgeMapper;
import cc.xiaoxu.cloud.entity.Knowledge;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KnowledgeService extends ServiceImpl<KnowledgeMapper, Knowledge> {

}