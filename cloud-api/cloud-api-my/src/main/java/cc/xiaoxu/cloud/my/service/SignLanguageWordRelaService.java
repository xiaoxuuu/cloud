package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.my.dao.SignLanguageWordRelaMapper;
import cc.xiaoxu.cloud.my.entity.SignLanguageWordRela;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Getter
@Slf4j
@Service
public class SignLanguageWordRelaService extends ServiceImpl<SignLanguageWordRelaMapper, SignLanguageWordRela> {

    public Map<Integer, Set<Integer>> getWordRela(List<Integer> wordIds) {

        if (CollectionUtils.isEmpty(wordIds)) {
            return Collections.emptyMap();
        }

        List<SignLanguageWordRela> list = lambdaQuery()
                .in(SignLanguageWordRela::getWordIdLeft, wordIds)
                .or().in(SignLanguageWordRela::getWordIdRight, wordIds)
                .list();

        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (SignLanguageWordRela signLanguageWordRela : list) {
            Integer wordIdLeft = signLanguageWordRela.getWordIdLeft();
            Integer wordIdRight = signLanguageWordRela.getWordIdRight();
            buildMap(map, wordIdLeft, wordIdRight);
            buildMap(map, wordIdRight, wordIdLeft);
        }

        return map;
    }

    private static void buildMap(Map<Integer, Set<Integer>> map, Integer wordIdLeft, Integer wordIdRight) {
        Set<Integer> left = map.getOrDefault(wordIdLeft, new HashSet<>());
        left.add(wordIdRight);
        map.put(wordIdLeft, left);
    }
}