package cc.xiaoxu.cloud.core.utils.random.person.control;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NameControl implements ControlInterface {

    /**
     * 默认脱敏字
     */
    public static final String DEFAULT_DESENSITIZATION_NAME = "某";

    /**
     * 使用单字姓
     */
    private boolean useSurname;

    /**
     * 使用复姓
     */
    private boolean useDoubleSurname;

    /**
     * 使用单字名
     */
    private boolean useName;

    /**
     * 使用双字名
     */
    private boolean useDoubleName;

    /**
     * 脱敏长度
     */
    private int desensitizationLength;

    /**
     * 脱敏字
     */
    private String desensitizationName;

    private NameControl() {
        this.useSurname = true;
        this.useDoubleSurname = false;
        this.useName = true;
        this.useDoubleName = true;
        this.desensitizationLength = 0;
        this.desensitizationName = DEFAULT_DESENSITIZATION_NAME;
    }

    public static NameControl of() {
        return new NameControl();
    }

    /**
     * 设置是否使用单字姓
     *
     * @param useSurname 是否使用单字姓
     * @return this
     */
    public NameControl useSurname(Boolean useSurname) {
        this.useSurname = useSurname;
        return this;
    }

    /**
     * 设置是否使用复姓
     *
     * @param useDoubleSurname 是否使用复姓
     * @return this
     */
    public NameControl useDoubleSurname(Boolean useDoubleSurname) {
        this.useDoubleSurname = useDoubleSurname;
        return this;
    }

    /**
     * 设置是否使用单字名
     *
     * @param useName 是否使用单字名
     * @return this
     */
    public NameControl useName(Boolean useName) {
        this.useName = useName;
        return this;
    }

    /**
     * 设置是否使用双字名
     *
     * @param useDoubleName 是否使用双字名
     * @return this
     */
    public NameControl useDoubleName(Boolean useDoubleName) {
        this.useDoubleName = useDoubleName;
        return this;
    }

    /**
     * 设置脱敏长度
     *
     * @param desensitizationLength 脱敏长度
     * @return this
     */
    public NameControl desensitizationLength(int desensitizationLength) {
        this.desensitizationLength = desensitizationLength;
        return this;
    }

    /**
     * 设置脱敏字
     *
     * @param desensitizationName 脱敏字
     * @return this
     */
    public NameControl desensitizationName(String desensitizationName) {
        this.desensitizationName = desensitizationName;
        return this;
    }

    @Override
    public String check() {

        if (desensitizationLength < 0) {
            return "脱敏长度最短不能低于 0";
        }
        if (desensitizationLength >= 1 && null == desensitizationName) {
            return "设置脱敏长度后，脱敏字不能为空";
        }
        if (!useSurname && !useDoubleSurname) {
            return "单姓复姓不能同时关闭";
        }
        if (!useName && !useDoubleName) {
            return "单字名双字名不能同时关闭";
        }
        return null;
    }
}