package cc.xiaoxu.cloud.core.annotation;

import cc.xiaoxu.cloud.core.utils.enums.EnumInterface;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>枚举文档构建器</p>
 *
 * @author 小徐
 * @since 2023/7/18 14:32
 */
@Configuration
public class EnumCustomizer implements PropertyCustomizer {

    @Value("${knife4j.enable:false}")
    private Boolean knife4j;

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Schema<?> customize(Schema schema, AnnotatedType annotatedType) {

        Annotation[] ctxAnnotations = annotatedType.getCtxAnnotations();
        for (Annotation ctxAnnotation : ctxAnnotations) {

            // knife4j 兼容
            if (knife4j) {
                String description = schema.getDescription();
                String title = schema.getTitle();
                if (null == description || description.isEmpty()) {
                    schema.description(title);
                } else {
                    if (null != title && !title.isEmpty()) {
                        schema.description(schema.getTitle() + "," + description);
                    }
                }
            }

            // SchemaEnum 枚举处理
            SchemaEnum schemaEnum = AnnotationUtils.getAnnotation(ctxAnnotation, SchemaEnum.class);
            if (null == schemaEnum) {
                // 此处只处理包含 SchemaEnum 注解的字段
                continue;
            }

            // 可使用列表
            Set<String> allowList = CollectionUtils.isEmpty(schema.getEnum()) ? new HashSet<>() : new HashSet<String>(schema.getEnum());
            Class<? extends Enum<? extends EnumInterface<?>>> clazz = schemaEnum.clazz();
            Enum<? extends EnumInterface<?>>[] enums = clazz.getEnumConstants();
            Object[] enumArray = Arrays.stream(enums)
                    .filter(k -> {
                        if (CollectionUtils.isEmpty(schema.getEnum())) {
                            return true;
                        }
                        return allowList.contains(k.name());
                    })
                    .toArray();
            schema.setEnum(Arrays.stream(enumArray).map(String::valueOf).toList());

            // 描述
            String enumDesc = (StringUtils.isNotBlank(schema.getDescription()) ? "," : "") + "枚举释义: %s";
            List<String> descList = EnumUtils.getDesc(clazz, enumArray);
            if (CollectionUtils.isNotEmpty(descList)) {
                enumDesc = String.format(enumDesc, String.join(",", descList));
            }
            schema.description(schema.getDescription() + enumDesc);
        }
        return schema;
    }
}