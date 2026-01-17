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
    private final CacheManager cacheManager;

    public User add(String openId){

        try {
            if (cacheManager.getUserMap().containsKey(openId)) {
                return cacheManager.getUserMap().get(openId);
            }
            User entity = new User();
            entity.setOpenId(openId);
            entity.setState(StateEnum.ENABLE.getCode());
            entity.setCreateTime(new Date());
            cacheManager.getUserMap().put(openId, entity);
            cacheManager.getUserList().add(entity);
            userService.save(entity);
            return entity;
        } catch (Exception e) {
            log.error("添加用户失败，openId={}", openId, e);
            return null;
        }
    }
}