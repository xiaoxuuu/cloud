package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.ModelInfo;
import cc.xiaoxu.cloud.ai.service.ModelInfoService;
import cc.xiaoxu.cloud.bean.ai.vo.ModelInfoVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "模型管理")
@RequestMapping("/model_info")
public class ModelInfoController {

    private final ModelInfoService modelInfoService;

    // TODO 向量模型配置
    @PostMapping(value = "/list")
    @Operation(summary = "模型列表")
    public List<ModelInfoVO> list() {

        List<ModelInfo> modelList = modelInfoService.list();
        return modelList.stream()
                .sorted(Comparator.comparing(ModelInfo::getCompany, Comparator.reverseOrder()).thenComparing(ModelInfo::getSort, Comparator.naturalOrder()))
                .map(this::toModelInfoVO)
                .toList();
    }

    private ModelInfoVO toModelInfoVO(ModelInfo modelInfo) {

        ModelInfoVO vo = new ModelInfoVO();
        BeanUtils.populate(modelInfo, vo);
        return vo;
    }
}