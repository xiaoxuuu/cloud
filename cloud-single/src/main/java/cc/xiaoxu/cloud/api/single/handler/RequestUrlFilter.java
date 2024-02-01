package cc.xiaoxu.cloud.api.single.handler;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class RequestUrlFilter implements Filter {

    private static final Set<String> REPLACE_SET = new HashSet<>();

    static {
        REPLACE_SET.add("/authorize");
        REPLACE_SET.add("/service_portrait");
        REPLACE_SET.add("/service_system");
        REPLACE_SET.add("/service_file");
        REPLACE_SET.add("/service_ideology");
    }

    private String handle(String uri) {

        for (String s : REPLACE_SET) {
            if (uri.startsWith(s)) {
                return uri.replace(s, "");
            }
        }
        return uri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String requestUri = req.getRequestURI();
        StringBuffer requestUrl = req.getRequestURL();
        // 将请求 URI 修改为要修改的 URI
        String modifiedUri = handle(requestUri);
        String modifiedUrl = requestUrl.toString().replace(requestUri, modifiedUri);

        // 创建一个包含修改后 URI 的请求对象
        ModifiedUriHttpServletRequestWrapper wrapper = new ModifiedUriHttpServletRequestWrapper(req, modifiedUri, modifiedUrl);

        // 将修改后的请求对象传递给下一个过滤器
        chain.doFilter(wrapper, response);
    }
}

class ModifiedUriHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final String modifiedUri;
    private final String modifiedUrl;

    public ModifiedUriHttpServletRequestWrapper(HttpServletRequest request, String modifiedUri, String modifiedUrl) {
        super(request);
        this.modifiedUri = modifiedUri;
        this.modifiedUrl = modifiedUrl;
    }

    @Override
    public String getRequestURI() {
        return modifiedUri;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(modifiedUrl);
    }
}