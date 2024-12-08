package cc.xiaoxu.cloud.ai.event;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.ai.service.LocalApiService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddLocalFileEventDTO;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AddLocalFileEvent {

    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;
    private final LocalApiService localApiService;

    @EventListener(classes = {KnowledgeAddLocalFileEventDTO.class})
    public void onApplicationEvent(KnowledgeAddLocalFileEventDTO dto) {

        // 文件读取
        Knowledge knowledge = knowledgeService.getById(dto.getKnowledgeId());
        log.debug("读取文件：{}", knowledge.getName());
        // 读取指定位置文件
        String content = read(knowledge.getThreePartyFileId());
        log.debug("读取文件长度：{}", content.length());

        // 本地文件切片
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.SECTION_READ);
        // 发起请求
        List<String> textList;
        textList = localApiService.split(content);
        // 数据入库
        knowledgeSectionService.insertNewData(dto.getKnowledgeId(), textList, dto.getTenant());

        // 本地文件向量化
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.VECTOR_CALC);
        knowledgeSectionService.calcVector(new IdDTO(dto.getKnowledgeId()));
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.ALL_COMPLETED);

        knowledgeService.lambdaUpdate()
                .set(Knowledge::getState, StateEnum.ENABLE.getCode())
                .eq(Knowledge::getId, dto.getKnowledgeId())
                .eq(Knowledge::getState, StateEnum.PROGRESSING.getCode())
                .update();
    }

    private static String read(String filePath) {

        StringBuilder fileContent = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath);
             // 指定 GBK 编码
//             InputStreamReader isr = new InputStreamReader(fis, "GBK");
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             // 使用 BufferedReader 逐行读取
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                // 追加每行内容和换行符
                fileContent.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }
}