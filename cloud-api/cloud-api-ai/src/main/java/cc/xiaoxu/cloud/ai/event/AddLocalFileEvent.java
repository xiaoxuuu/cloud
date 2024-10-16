package cc.xiaoxu.cloud.ai.event;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddLocalFileEventDTO;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AddLocalFileEvent {

    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;

    @EventListener(classes = {KnowledgeAddLocalFileEventDTO.class})
    public void onApplicationEvent(KnowledgeAddLocalFileEventDTO dto) {

        // 文件读取
        Knowledge knowledge = knowledgeService.getById(dto.getKnowledgeId());
        // 读取指定位置文件
        String filePath = knowledge.getThreePartyFileId();

        // 指定文件路径
        Path path = Paths.get(filePath);

        try {
            // 读取文件内容为字符串列表
            List<String> fileContent = Files.readAllLines(path);
            // 打印文件内容
            fileContent.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO 本地文件切片
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.SECTION_READ);

        // TODO 本地文件向量化
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.VECTOR_CALC);
        knowledgeSectionService.calcVector(new IdDTO(dto.getKnowledgeId()));
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.ALL_COMPLETED);

        knowledgeService.lambdaUpdate()
                .eq(Knowledge::getId, dto.getKnowledgeId())
                .eq(Knowledge::getState, StateEnum.ENABLE.getCode())
                .update();
    }

    private static String read(String filePath) {

        // 指定文件路径
        Path path = Paths.get(filePath);

        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 追加每行内容和换行符
                fileContent.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    public static void main(String[] args) {
        String s = "/Volumes/HDD/03_Book/《超魔杀帝国(超级魔法帝国)》（校对版全本）作者：小分队长.txt";
        String read = read(s);
        System.out.println(read);
    }
}