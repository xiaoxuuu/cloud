package cc.xiaoxu.cloud.core.utils.enums.inter;

/**
 * <p>枚举描述接口，暂时无用</p>
 *
 * @author 小徐
 * @since 2022/12/30 10:39
 */
public interface EnumDescInterface {

    /**
     * <p>增强 api 描述</p>
     *
     * @return 构建的 api 文档描述
     */
    String enhanceApiDesc();

    /**
     * 默认的文档实现
     *
     * @param name 名称
     * @param desc 描述
     * @return 名称(描述)
     */
    default String enhanceApiDesc(String name, String desc) {
        return name + "(" + desc + ")";
    }
}