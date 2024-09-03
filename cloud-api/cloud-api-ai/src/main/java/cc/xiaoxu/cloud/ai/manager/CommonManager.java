package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.ai.dao.CommonMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class CommonManager {

    private final CommonMapper commonMapper;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<String> getList(String sql) {
        return commonMapper.sql(sql);
    }
}