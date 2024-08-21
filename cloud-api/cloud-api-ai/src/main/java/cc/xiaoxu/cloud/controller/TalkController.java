package cc.xiaoxu.cloud.controller;

import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import cc.xiaoxu.cloud.service.ALiYunService;
import cc.xiaoxu.cloud.service.KnowledgeSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "聊天服务")
@RequestMapping("/talk")
public class TalkController {

    private final KnowledgeSectionService knowledgeSectionService;
    private final ALiYunService aLiYunService;

    @PostMapping("/ask")
    @Operation(summary = "提问")
    public String ask(@Valid @RequestBody AskDTO vo) {

        // 问题转为向量
        String vector = aLiYunService.vector(vo.getQuestion());

        // 取出相似度数据
        List<KnowledgeSectionVO> similarityData = knowledgeSectionService.getBaseMapper().getSimilarityData(vector, vo.getSimilarity(), vo.getSimilarityContentNum());
        log.info("向量：{}", JsonUtils.toString(similarityData));

        // 提问
        return "";
    }
}