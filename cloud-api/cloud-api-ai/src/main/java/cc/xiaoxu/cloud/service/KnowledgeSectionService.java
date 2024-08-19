package cc.xiaoxu.cloud.service;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.dao.KnowledgeSectionMapper;
import cc.xiaoxu.cloud.entity.KnowledgeSection;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeSectionService extends ServiceImpl<KnowledgeSectionMapper, KnowledgeSection> {

    private final ALiYunService aLiYunService;

    public boolean rebuild(IdDTO dto) {

        // 读取页数
        // 每次处理 1000 条数据
        return false;
    }
}