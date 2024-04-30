package cc.xiaoxu.cloud.api.demo;

import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class BeanDemo {


    public static <T, R> T tran(R r, Class<T> tClass) {

        T t;
        try {
            t = tClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BeanUtils.populate(r, t);
        return t;
    }

    public static void main(String[] args) {

        Goods goods = new Goods();
        goods.amount = 1;
        GoodsVO tran = tran(goods, GoodsVO.class);
        System.out.println(12);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoodsVO {

        private Integer amount;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Goods {
        private Integer amount;
    }
}
