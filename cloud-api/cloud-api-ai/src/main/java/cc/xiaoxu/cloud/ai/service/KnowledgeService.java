package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileTypeEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KnowledgeService extends ServiceImpl<KnowledgeMapper, Knowledge> {

    public void addFile(String fileName, String fileId) {

        Knowledge knowledge = new Knowledge();
        knowledge.setType(KnowledgeTypeEnum.FILE.getCode());
        knowledge.setName(fileName);
        knowledge.setAdditionalInfo(fileId);
        knowledge.setStatus(ALiFileTypeEnum.INIT.getCode());
        save(knowledge);
    }
}