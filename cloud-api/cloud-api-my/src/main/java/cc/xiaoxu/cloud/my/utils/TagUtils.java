package cc.xiaoxu.cloud.my.utils;

import java.util.*;
import java.util.stream.Collectors;

public class TagUtils {

    private static final Map<String, String> map = new HashMap<>();

    static {
        map.put("胡辣汤", "豫菜");
        map.put("河南菜", "豫菜");
        map.put("小龙虾", "鄂菜");
        map.put("麻辣冷吃兔", "川菜");
        map.put("冒菜", "川菜");
        map.put("蹄花", "川菜");
        map.put("肥肠粉", "川菜");
        map.put("麻辣烫", "川菜");
        map.put("四川菜", "川菜");
        map.put("川味小吃", "川菜");
        map.put("重庆菜", "川菜");
        map.put("慕名而来的正宗川菜", "川菜");
        map.put("江湖菜宜宾菜的结合", "川菜");
        map.put("云南菜", "云贵菜");
        map.put("傣族菜", "云贵菜");
        map.put("过桥米线", "云贵菜");
        map.put("台湾特色菜", "台湾菜");
        map.put("炸酱面", "京菜");
        map.put("涮羊肉", "京菜");

        map.put("自助餐", "自助");

        map.put("牛肉火锅", "火锅");
        map.put("四川火锅", "火锅");
        map.put("重庆火锅", "火锅");
        map.put("麻辣火锅", "火锅");
        map.put("粥底火锅", "火锅");
        map.put("鱼火锅", "火锅");
        map.put("鲜鱼火锅", "火锅");
        map.put("蛙火锅", "火锅");
        map.put("海鲜火锅", "火锅");

        map.put("自助海鲜", "自助,海鲜");
        map.put("水产海鲜", "海鲜");

        map.put("韩国料理", "韩餐");
        map.put("拉面", "日料");
        map.put("日本料理", "日料");
        map.put("日式烧烤", "日料,烧烤");
        map.put("泰国菜", "泰餐");

        map.put("披萨", "美式快餐");
        map.put("米线", "中式快餐");
        map.put("猪脚饭", "中式快餐");
        map.put("小吃快餐", "中式快餐");
        map.put("抄手", "中式快餐");

        map.put("烧鹅", "粤菜");
        map.put("广府菜", "粤菜");
        map.put("潮州菜", "粤菜");
        map.put("砂锅粥", "粤菜");
        map.put("烧麦", "粤菜");
        map.put("茶座", "粤菜");
        map.put("大排档", "粤菜");
        map.put("茶餐厅", "粤菜");

        map.put("钵钵鸡", "地域特色,川菜");
        map.put("单县羊肉汤", "地域特色,鲁菜");
        map.put("羊肉汤", "鲁菜");
        map.put("锅盔", "地域特色,川菜");
        map.put("担担面", "地域特色,川菜");
        map.put("串串香", "地域特色,川菜");

        map.put("抓饭", "新疆菜");
        map.put("新疆烧烤", "新疆菜,烧烤");
        map.put("烤串", "烧烤");
        map.put("夜市烧烤", "烧烤");

        map.put("牛肉面", "粉面粥点");
        map.put("面馆", "粉面粥点");

        map.put("西式糕点", "面包糕点");
        map.put("宫廷糕点", "面包糕点");
        map.put("中式糕点", "面包糕点");
        map.put("蛋糕店", "面包糕点");
    }

    public static Set<String> getTagSet(String... tag) {

        return Arrays.stream(String.join(",", tag).split(",")).map(k -> {
                    if (!map.containsKey(k)) {
                        return Set.of(k);
                    }
                    return Arrays.stream(map.get(k).split(",")).collect(Collectors.toSet());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
