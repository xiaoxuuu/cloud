package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.my.entity.User;
import cc.xiaoxu.cloud.my.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class UserManager {

    private final UserService userService;
    private final DataCacheManager dataCacheManager;

    public User add(String openId){

        try {
            if (dataCacheManager.getUserMap().containsKey(openId)) {
                return dataCacheManager.getUserMap().get(openId);
            }
            User entity = new User();
            entity.setOpenId(openId);
            entity.setState(StateEnum.ENABLE.getCode());
            entity.setCreateTime(new Date());
            dataCacheManager.getUserMap().put(openId, entity);
            dataCacheManager.getUserList().add(entity);
            userService.save(entity);
            return entity;
        } catch (Exception e) {
            log.error("添加用户失败，openId={}", openId, e);
            return null;
        }
    }
}