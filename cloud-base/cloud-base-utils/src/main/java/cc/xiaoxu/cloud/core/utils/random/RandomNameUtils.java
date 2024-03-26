package cc.xiaoxu.cloud.core.utils.random;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>生成随机姓名</p>
 *
 * @author 小徐
 * @since 2023/5/6 10:39
 */
public class RandomNameUtils {

    /**
     * 默认脱敏字
     */
    private static final String DEFAULT_DESENSITIZATION_NAME = "某";

    /**
     * 姓
     */
    private static final String[] SURNAME = {"李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴", "徐", "孙",
            "胡", "朱", "高", "林", "何", "郭", "马", "罗", "梁", "宋", "郑", "谢", "韩", "唐", "冯", "于", "董", "萧",
            "程", "曹", "袁", "邓", "许", "傅", "沈", "曾", "彭", "吕", "苏", "卢", "蒋", "蔡", "贾", "丁", "魏", "薛",
            "叶", "阎", "余", "潘", "杜", "戴", "夏", "钟", "汪", "田", "任", "姜", "范", "方", "石", "姚", "谭", "廖",
            "邹", "熊", "金", "陆", "郝", "孔", "白", "崔", "康", "毛", "邱", "秦", "江", "史", "顾", "侯", "邵", "孟",
            "龙", "万", "段", "漕", "钱", "汤", "尹", "黎", "易", "常", "武", "乔", "贺", "赖", "龚", "文", "庞", "樊",
            "兰", "殷", "施", "陶", "洪", "翟", "安", "颜", "倪", "严", "牛", "温", "芦", "季", "俞", "章", "鲁", "葛",
            "伍", "韦", "申", "尤", "毕", "聂", "丛", "焦", "向", "柳", "邢", "路", "岳", "齐", "沿", "梅", "莫", "庄",
            "辛", "管", "祝", "左", "涂", "谷", "祁", "时", "舒", "耿", "牟", "卜", "路", "詹", "关", "苗", "凌", "费",
            "纪", "靳", "盛", "童", "欧", "甄", "项", "曲", "成", "游", "阳", "裴", "席", "卫", "查", "屈", "鲍", "位",
            "覃", "霍", "翁", "隋", "植", "甘", "景", "薄", "单", "包", "司", "柏", "宁", "柯", "阮", "桂", "闵", "解",
            "强", "柴", "华", "车", "冉", "房", "边", "辜", "吉", "饶", "刁", "瞿", "戚", "丘", "古", "米", "池", "滕",
            "晋", "苑", "邬", "臧", "畅", "宫", "来", "嵺", "苟", "全", "褚", "廉", "简", "娄", "盖", "符", "奚", "木",
            "穆", "党", "燕", "郎", "邸", "冀", "谈", "姬", "屠", "连", "郜", "晏", "栾", "郁", "商", "蒙", "计", "喻",
            "揭", "窦", "迟", "宇", "敖", "糜", "鄢", "冷", "卓", "花", "仇", "艾", "蓝", "都", "巩", "稽", "井", "练",
            "仲", "乐", "虞", "卞", "封", "竺", "冼", "原", "官", "衣", "楚", "佟", "栗", "匡", "宗", "应", "台", "巫",
            "鞠", "僧", "桑", "荆", "谌", "银", "扬", "明", "沙", "薄", "伏", "岑", "习", "胥", "保", "和", "蔺"};

    /**
     * 复姓
     */
    private static final String[] DOUBLE_SURNAME = {"欧阳", "上官", "皇甫", "令狐", "诸葛", "司徒", "司马", "申屠",
            "夏侯", "贺兰", "完颜", "慕容", "尉迟", "长孙"};

    /**
     * 名，单字
     */
    private static final String[] NAME = {"凯", "帅", "昆", "帆", "理", "嘉", "吉", "昊", "旭", "远", "昌", "萍",
            "叶", "成", "民", "向", "舒", "研", "昕", "刚", "君", "东", "娜", "思", "星", "娟", "怡", "鸣", "春", "琦",
            "倩", "航", "琪", "启", "爱", "琳", "阳", "常", "永", "丹", "琼", "格", "丽", "爽", "鸿", "义", "虎", "鹏",
            "乐", "恒", "晓", "硕", "瑛", "瑜", "瑞", "江", "健", "婧", "聪", "豪", "鑫", "扬", "平", "艳", "米", "晴",
            "瑶", "晶", "婷", "艺", "智", "登", "艾", "广", "梅", "庆", "璇", "璐", "云", "梓", "蒙", "亚", "炜", "红",
            "亦", "梦", "钧", "墨", "芬", "亮", "钰", "育", "芳", "康", "芷", "芸", "点", "雄", "雅", "勇", "盈", "磊",
            "立", "勋", "裕", "高", "蓝", "飞", "惠", "棠", "满", "波", "继", "滨", "雨", "烨", "雪", "铭", "雯", "峰",
            "英", "哲", "想", "维", "雷", "竹", "建", "峻", "曼", "泽", "国", "洁", "月", "洋", "霖", "弘", "猛", "洛",
            "甜", "茜", "霞", "贞", "伟", "生", "真", "欢", "欣", "贤", "大", "洪", "太", "紫", "焱", "露", "贵", "然",
            "伶", "男", "强", "元", "潇", "先", "光", "蕊", "煌", "华", "敏", "菲", "青", "靖", "南", "静", "博", "孜",
            "非", "赟", "楠", "正", "坤", "祥", "彦", "慧", "轩", "浩", "彪", "卫", "彬", "敬", "祯", "杰", "兰", "佳",
            "兴", "兵", "海", "祺", "好", "蕾", "松", "长", "超", "宇", "薇", "文", "辉", "安", "莉", "玉", "越", "斌",
            "美", "福", "宏", "林", "宗", "香", "龙", "军", "涛", "宜", "依", "翰", "玫", "冬", "微", "妮", "冰", "新",
            "玲", "莲", "涵", "家", "德", "莹", "达", "秀", "菁", "珂", "心", "毅", "淇", "俊", "珊", "凌", "富", "珍",
            "巍", "柏", "音", "金", "淑", "翔", "燕", "志", "诗", "萌", "凡", "臣", "凤", "希", "可", "堂", "明"};

    /**
     * 名，双字
     */
    private static final String[] DOUBLE_NAME = {"艺华", "小丽", "雅丽", "爱民", "秀秀", "燕子", "常青", "非翔", "亦舒",
            "一鸣", "洁洁", "菁菁", "佳敏", "依琳", "佳蕊", "雅婷", "美娜", "向阳", "梦琪", "欢欢", "波波", "珂珂", "小晴",
            "红红", "兴华", "海滨", "舒怡", "昊然", "正杰", "美丽", "洁楠", "洛阳", "艾青", "凤凤", "小芳", "继红", "雨婷",
            "晓梅", "昕怡", "彦华", "华丽", "金鑫", "月亮", "帅帅", "小雨", "超越", "心怡", "丽华", "春晓", "宇杰", "慧敏",
            "宇轩", "梦梦", "琦琦", "鸿伟", "雅雯", "先生", "思敏", "浩然", "小霞", "俊杰", "慧慧", "潇潇", "薇薇", "洪波",
            "立平", "育辉", "高峰", "盈盈", "烨烨", "芷蓝", "志明", "佳丽", "雅静", "小青", "倩倩", "安安", "莉莉", "惠娟",
            "怡然", "大海", "贤淑", "秀英", "贞芳", "满堂", "珊珊", "磊磊", "聪聪", "小玉", "志鹏", "嘉丽", "博超", "博文",
            "玉珍", "正豪", "洋洋", "玫玫", "静香", "德义", "宏伟", "小玲", "彬彬", "希希", "博涛", "斌斌", "扬扬", "萌萌",
            "瑞丽", "慧娟", "柏宇", "梦蕾", "维嘉", "理想", "萍萍", "紫薇", "泽霖", "铭哲", "旭东", "春华", "华华", "微微",
            "明明", "晓涵", "美美", "虎虎", "远东", "云鹏", "亚男", "祯祥", "元芳", "登峰", "志勇", "文婷", "敏儿", "远航",
            "诗雅", "嘉勋", "明阳", "洁雅", "建军", "可可", "凯凯", "美莲", "立华", "敏敏", "伟男", "雯雯", "伟光", "艺敏",
            "乐乐", "兰兰", "冰冰", "新新", "杰杰", "璐璐", "叶子", "子轩", "佳欣", "智慧", "志伟", "泽宇", "红梅", "翔宇",
            "曼妮", "瑞雪", "子龙", "海棠", "志强", "晓丹", "玲玲", "凌云", "雨琪", "露露", "美琪", "志华", "永辉", "霞姐",
            "明康", "思雨", "佳佳", "鸿飞", "琳琳", "芳芳", "艳艳", "阳阳", "吉祥", "大雄", "海洋", "德华", "青松", "太平",
            "伟巍", "晓艳", "英杰", "维维", "明雪", "婧怡", "静雅", "艳红", "飞翔", "云霞", "燕燕", "硕硕", "艳芳", "裕裕",
            "建平", "泽民", "文静", "瑞华", "晶晶", "瑶瑶", "长江", "伟刚", "东海", "昌坤", "洁莹", "瑜玲", "小洁", "南华",
            "爱国", "富贵", "英俊", "佳音", "婷婷", "嘉俊", "林林", "辉煌", "雷雷", "淑芬", "燕丽", "旭华", "伟鹏", "艳霞",
            "子健", "国庆", "慧玲", "明杰", "点点", "大卫", "蒙蒙", "静静", "宗翰", "香香", "小楠", "怡伶", "嘉琳", "炜健",
            "建国", "强强", "嘉琪", "正义", "燕婷", "米兰", "洁琼", "墨竹", "敏雅", "峻峰", "梅子", "艳莉", "军军", "涛涛",
            "瑛瑛", "小妮", "小冬", "竹子", "广成", "家豪", "智勇", "伟勇", "成龙", "宇航", "立峰", "娜娜", "曼曼", "小涵",
            "格格", "甜甜", "梓涵", "芳菲", "茜茜", "晓宇", "梓淇", "翔飞", "建华", "思琪", "弘毅", "晓龙", "好好", "雅凤",
            "博达", "爽爽", "蓝蓝", "海鹏", "启明", "思琳", "敬宜", "小凯", "蕾蕾", "启航", "敏华", "飞飞", "小明", "钰婷",
            "建林", "国强", "伟伟", "娟娟", "真真", "亦凡", "静怡"};

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

    /**
     * 禁止直接无参实例化
     */
    private RandomNameUtils() {
    }

    /**
     * 设置是否使用单字姓
     *
     * @param useSurname 是否使用单字姓
     * @return this
     */
    public RandomNameUtils setUseSurname(Boolean useSurname) {

        this.useSurname = useSurname;
        return this;
    }

    /**
     * 设置是否使用复姓
     *
     * @param useDoubleSurname 是否使用复姓
     * @return this
     */
    public RandomNameUtils setUseDoubleSurname(Boolean useDoubleSurname) {

        this.useDoubleSurname = useDoubleSurname;
        return this;
    }

    /**
     * 设置是否使用单字名
     *
     * @param useName 是否使用单字名
     * @return this
     */
    public RandomNameUtils setUseName(Boolean useName) {

        this.useName = useName;
        return this;
    }

    /**
     * 设置是否使用双字名
     *
     * @param useDoubleName 是否使用双字名
     * @return this
     */
    public RandomNameUtils setUseDoubleName(Boolean useDoubleName) {

        this.useDoubleName = useDoubleName;
        return this;
    }

    /**
     * 设置脱敏长度
     *
     * @param desensitizationLength 脱敏长度
     * @return this
     */
    public RandomNameUtils setDesensitizationLength(int desensitizationLength) {

        this.desensitizationLength = desensitizationLength;
        return this;
    }

    /**
     * 设置脱敏字
     *
     * @param desensitizationName 脱敏字
     * @return this
     */
    public RandomNameUtils setDesensitizationName(String desensitizationName) {

        this.desensitizationName = desensitizationName;
        return this;
    }

    /**
     * <p>初始化</p>
     * <p>useSurname：true</p>
     * <p>useDoubleSurname：true</p>
     * <p>useName：true</p>
     * <p>useDoubleName：true</p>
     * <p>desensitizationLength：0</p>
     * <p>desensitizationName：某</p>
     *
     * @return this
     */
    public static RandomNameUtils init() {
        return new RandomNameUtils()
                .setUseSurname(true)
                .setUseDoubleSurname(true)
                .setUseName(true)
                .setUseDoubleName(true)
                .setDesensitizationLength(0)
                .setDesensitizationName(DEFAULT_DESENSITIZATION_NAME);
    }

    /**
     * 获取一个随机人名
     *
     * @return 获取的人名
     */
    public String get() {

        return getBatch(1).get(0);
    }

    /**
     * 获取多个随机人名
     *
     * @param i 需获取的人名个数
     * @return 获取的人名集合
     */
    public List<String> getBatch(int i) {

        ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        List<String> surnameList = new ArrayList<>();
        if (useSurname) {
            surnameList.addAll(List.of(SURNAME));
        }
        if (useDoubleSurname) {
            surnameList.addAll(List.of(DOUBLE_SURNAME));
        }

        List<String> nameList = new ArrayList<>();
        if (useName) {
            nameList.addAll(List.of(NAME));
        }
        if (useDoubleName) {
            nameList.addAll(List.of(DOUBLE_NAME));
        }

        List<String> returnNameList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            String finalSurname = "";
            String finalName = "";

            if (useSurname || useDoubleSurname) {
                int surnameLength = localRandom.nextInt(surnameList.size());
                finalSurname = surnameList.get(surnameLength);
            }

            if (useName || useDoubleName) {
                int nameLength = localRandom.nextInt(nameList.size());
                finalName = nameList.get(nameLength);
            }

            if (desensitizationLength != 0) {
                int index = Math.min(desensitizationLength, finalName.length());
                String last = finalName.substring(index);
                String desensitizationRepeat = String.valueOf(desensitizationName).repeat(index);
                finalName = desensitizationRepeat + last;
            }
            returnNameList.add(finalSurname + finalName);
        }

        return returnNameList;
    }
}