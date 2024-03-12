package cc.xiaoxu.cloud.api.demo.valueError;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SdkUtil {

    private static final Logger log = LoggerFactory.getLogger(SdkUtil.class);

    public static String workdir;

    public SdkUtil() {
    }

    @PostConstruct
    public void init() {
        log.error("init 工作目录：【{}】", workdir);
    }

    @Value("${safety.workdir:}")
    public void setWorkdir(String workdir) {
        log.error("setWorkdir 工作目录：【{}】", workdir);
        SdkUtil.workdir = workdir;
    }

    public static String getDir() {

        return workdir;
    }
}