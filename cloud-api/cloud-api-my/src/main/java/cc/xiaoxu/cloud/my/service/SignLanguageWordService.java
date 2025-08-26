package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.IdsDTO;
import cc.xiaoxu.cloud.bean.dto.SignLanguageWordRelaAddDTO;
import cc.xiaoxu.cloud.bean.dto.SignLanguageWordSearchDTO;
import cc.xiaoxu.cloud.bean.vo.SignLanguageWordVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.SignLanguageWordMapper;
import cc.xiaoxu.cloud.my.entity.SignLanguageWord;
import cc.xiaoxu.cloud.my.entity.SignLanguageWordRela;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class SignLanguageWordService extends ServiceImpl<SignLanguageWordMapper, SignLanguageWord> {

    private final SignLanguageWordRelaService signLanguageWordRelaService;

    public List<SignLanguageWordVO> lists(SignLanguageWordSearchDTO dto) {

        List<SignLanguageWord> list = lambdaQuery()
                .like(SignLanguageWord::getWordName, dto.getKeywords())
                // 移除指定数据及其关联数据
                .ne(null != dto.getId(), SignLanguageWord::getId, dto.getId())
                .last(" LIMIT 20 ")
                .list();

        Map<Integer, Set<Integer>> relaMap = signLanguageWordRelaService.getWordRela(list.stream().map(SignLanguageWord::getId).toList());
        // 移除指定数据及其关联数据
        list = list.stream()
                .filter(k -> !relaMap.getOrDefault(dto.getId(), new HashSet<>()).contains(k.getId()))
                .toList();
        return tran(list, relaMap);
    }

    public List<SignLanguageWordVO> lists(IdsDTO dto) {

        List<SignLanguageWord> list = lambdaQuery()
                .in(SignLanguageWord::getId, dto.getIdList())
                .list();
        Map<Integer, Set<Integer>> relaMap = signLanguageWordRelaService.getWordRela(list.stream().map(SignLanguageWord::getId).toList());
        return tran(list, relaMap);
    }

    private static List<SignLanguageWordVO> tran(List<SignLanguageWord> list, Map<Integer, Set<Integer>> relaMap) {

        return list.stream().map(k -> {
            SignLanguageWordVO vo = new SignLanguageWordVO();
            BeanUtils.populate(k, vo);
            vo.setWordIdList(relaMap.get(k.getId()));
            return vo;
        }).toList();
    }

    public void add(SignLanguageWordRelaAddDTO dto) {

        if (dto.getWordIdLeft() > dto.getWordIdRight()) {
            int temp = dto.getWordIdLeft();
            dto.setWordIdLeft(dto.getWordIdRight());
            dto.setWordIdRight(temp);
        }
        signLanguageWordRelaService.save(new SignLanguageWordRela()
                .setWordIdLeft(dto.getWordIdLeft())
                .setWordIdRight(dto.getWordIdRight()));
    }
}