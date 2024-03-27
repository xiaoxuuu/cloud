package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.my.bean.es.Document;
import cc.xiaoxu.cloud.my.dao.es.DocumentMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "es", description = "ElasticSearch")
@RestController
@RequestMapping("/es")
public class TestUseEeController {

    @Resource
    private DocumentMapper documentMapper;

    @GetMapping("/createIndex")
    public Boolean createIndex() {
        // 初始化 -> 创建索引
        return documentMapper.createIndex();
    }

    @GetMapping("/insert")
    public Integer insert() {
        // 初始化 -> 新增数据
        Document document = new Document();
        document.setId("1234567abcdefg");
        document.setTitle("人");
        document.setContent("技术过硬");
        return documentMapper.insert(document);
    }

    @GetMapping("/search")
    public List<Document> search() {
        // 查询出所有标题为老汉的文档列表
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, "老汉")
                .and(i -> i.eq(Document::getId, 1).eq(Document::getContent, "推车"))
                .or()
                .and(i -> i.eq(Document::getId, 2).eq(Document::getContent, "推土"));
        String source = documentMapper.getSource(wrapper);
        System.out.println(source);
        return documentMapper.selectList(wrapper);
    }
}