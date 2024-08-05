package cc.xiaoxu.cloud.doc.annotation;

import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintViolationCreationContext;
import org.hibernate.validator.internal.engine.path.PathImpl;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>枚举文档注解</p>
 * <p>可将枚举属性展示在 swagger 文档上</p>
 * <p>使用 Integer 与 Enum 等类型接收参数时，传参如果不符合类型，会导致反序列化失败，所以建议使用 String 接收参数</p>
 * <p>接收参数后可使用 {@link cc.xiaoxu.cloud.core.utils.enums.EnumUtils#getByClass(Object, Class) XEnumUtils.getByClass()} 将参数转换为所需枚举</p>
 *
 * @author 小徐
 * @since 2022/12/9 11:42
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Constraint(validatedBy = SchemaEnum.EnumValueValidator.class)
public @interface SchemaEnum {

    /* 文档与校验公用参数 */

    /**
     * 支持的枚举类列表，支持多个枚举同时展示，需继承{@link EnumInterface EnumInterface}
     */
    Class<? extends Enum<? extends EnumInterface<?>>> clazz();

    /* 校验相关参数 */

    /**
     * 是否开启入参校验
     */
    boolean verification() default true;

    /**
     * 错误提示，可使用 {name}、{enums} 占位符自定义
     */
    String message() default "{name}枚举值错误，可选值为[{enums}]";

    /**
     * 用于分组校验
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class EnumValueValidator implements ConstraintValidator<SchemaEnum, Object> {

        private SchemaEnum schemaEnum;

        @Override
        public void initialize(SchemaEnum enumerationValidator) {
            this.schemaEnum = enumerationValidator;
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {

            if (!schemaEnum.verification()) {
                return true;
            }

            if (Objects.isNull(value)) {
                value = "";
            }
            // 是否必须
//            if (!schemaEnum.required()) {
//                if (StringUtils.isEmpty((String) value)) {
//                    return true;
//                }
//            }

            EnumInterface<?>[] interfaceConstants = (EnumInterface<?>[]) schemaEnum.clazz().getEnumConstants();
            Enum<? extends EnumInterface<?>>[] enumConstants = schemaEnum.clazz().getEnumConstants();

////            Set<String> allowableValueSet = new HashSet<>(Arrays.asList(schemaEnum.allowableValues()));
////            // 获取可用枚举
////            List<EnumInterface<?>> allowableInterface = getInterfaceConstants(interfaceConstants, allowableValueSet);
////            List<Enum<? extends EnumInterface<?>>> allowableEnum = getInterfaceConstants(enumConstants, allowableValueSet);
//
//            // 校验
//            if (XEnumUtils.containsByValue(value, allowableInterface)) {
//                return true;
//            }
//            for (Enum<? extends EnumInterface<?>> anEnum : allowableEnum) {
//                if (anEnum.name().equals(value)) {
//                    return true;
//                }
//            }
//
//            // 构建失败数据
//            setErrorEnumPlaceholderValue(allowableInterface, context);
            return false;
        }

        /**
         * 设置错误提示消息中的 enum 和 name 占位符值
         *
         * @param allowableValueList 可选值数组
         * @param context            context
         */
        private void setErrorEnumPlaceholderValue(List<EnumInterface<?>> allowableValueList, ConstraintValidatorContext context) {

            String message = schemaEnum.message();
            // message 如果不包含 name 和 enums 占位符，则直接返回
            if (!message.contains("{name}") && !message.contains("{enums}")) {
                return;
            }

            String enumsPlaceholderValue = allowableValueList.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            // 添加枚举值占位符值参数，校验失败的时候可用
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.addMessageParameter("enums", enumsPlaceholderValue);
            String fieldName = getFieldName(context);
//            hibernateContext.addMessageParameter("name", fieldName + schemaEnum.value());
        }

        private String getFieldName(ConstraintValidatorContext context) {
            if (context instanceof ConstraintValidatorContextImpl) {
                ConstraintValidatorContextImpl contextImpl = (ConstraintValidatorContextImpl) context;
                List<ConstraintViolationCreationContext> constraintViolationCreationContexts = contextImpl.getConstraintViolationCreationContexts();
                if (constraintViolationCreationContexts.size() != 0) {
                    PathImpl path = constraintViolationCreationContexts.get(0).getPath();
                    return StringUtils.isNotEmpty(path.toString()) ? ("[" + path + "] ") : "";
                }
            }
            return "";
        }

        private List<EnumInterface<?>> getInterfaceConstants(EnumInterface<?>[] enumConstants, Set<String> allowableValueSet) {
            return Arrays.stream(enumConstants)
                    .filter(k -> {
                        if (CollectionUtils.isEmpty(allowableValueSet)) {
                            return true;
                        }
                        for (String allowableValue : allowableValueSet) {
                            if (EnumUtils.contains(k, allowableValue)) {
                                return true;
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
        }

        private List<Enum<? extends EnumInterface<?>>> getInterfaceConstants(Enum<? extends EnumInterface<?>>[] enumConstants, Set<String> allowableValueSet) {
            return Arrays.stream(enumConstants)
                    .filter(k -> {
                        if (CollectionUtils.isEmpty(allowableValueSet)) {
                            return true;
                        }
                        return allowableValueSet.contains(k.name());
                    }).collect(Collectors.toList());
        }
    }
}