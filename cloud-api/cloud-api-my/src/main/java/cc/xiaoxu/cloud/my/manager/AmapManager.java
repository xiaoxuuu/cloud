package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsResponseDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchResponseDTO;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import cc.xiaoxu.cloud.core.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class AmapManager {

    @Value("${app.config.amap}")
    private String amapApiKey;

    private static final String INPUT_TIPS_URL = "https://restapi.amap.com/v3/assistant/inputtips";
    private static final String POI_SEARCH_URL = "https://restapi.amap.com/v5/place/text";

    /**
     * 高德地图输入提示API
     * 文档：<a href="https://lbs.amap.com/api/webservice/guide/api-advanced/inputtips">...</a>
     *
     * @param requestDTO 请求参数
     * @return 输入提示响应结果
     */
    public AmapInputTipsResponseDTO inputTips(AmapInputTipsRequestDTO requestDTO) {
        try {
            HttpUtils httpUtils = HttpUtils.builder()
                    .url(INPUT_TIPS_URL)
                    .param("key", amapApiKey)
                    .param("keywords", requestDTO.getKeywords())
                    .param("output", "json")
                    .param("datatype", "poi");

            // 添加可选参数
            if (StringUtils.hasText(requestDTO.getRegion())) {
                httpUtils.param("city", requestDTO.getRegion());
            }
            if (StringUtils.hasText(requestDTO.getLocation())) {
                httpUtils.param("location", requestDTO.getLocation());
            }
            if (requestDTO.getCityLimit() != null) {
                httpUtils.param("citylimit", requestDTO.getCityLimit().toString());
            }

            String response = httpUtils.get();
            log.info("高德地图输入提示API响应: {}", response);

            return JsonUtils.parse(response, AmapInputTipsResponseDTO.class);
        } catch (Exception e) {
            log.error("调用高德地图输入提示API失败", e);
            throw new RuntimeException("调用高德地图输入提示API失败: " + e.getMessage());
        }
    }

    /**
     * 高德地图搜索POI 2.0 API
     * 文档：<a href="https://lbs.amap.com/api/webservice/guide/api-advanced/newpoisearch">...</a>
     *
     * @param requestDTO 请求参数
     * @return POI搜索响应结果
     */
    public AmapPoiSearchResponseDTO searchPoi(AmapPoiSearchRequestDTO requestDTO) {
        try {
            HttpUtils httpUtils = HttpUtils.builder()
                    .url(POI_SEARCH_URL)
                    .param("key", amapApiKey)
                    .param("keywords", URLEncoder.encode(requestDTO.getKeywords(), StandardCharsets.UTF_8))
                    .param("output", "json")
                    .param("page_size", requestDTO.getPageSize().toString())
                    .param("page_num", requestDTO.getPageNum().toString())
                    .param("extensions", requestDTO.getExtensions())
                    .param("type", requestDTO.getType());

            // 添加可选参数
            if (StringUtils.hasText(requestDTO.getRegion())) {
                httpUtils.param("region", requestDTO.getRegion());
            }
            if (requestDTO.getCityLimit() != null) {
                httpUtils.param("city_limit", requestDTO.getCityLimit().toString());
            }
            if (StringUtils.hasText(requestDTO.getShowFields())) {
                httpUtils.param("show_fields", requestDTO.getShowFields());
            }

            String response = httpUtils.get();
            log.info("高德地图搜索POI API响应: {}", response);

            return JsonUtils.parse(response, AmapPoiSearchResponseDTO.class);
        } catch (Exception e) {
            log.error("调用高德地图搜索POI API失败", e);
            throw new RuntimeException("调用高德地图搜索 POI API 异常");
        }
    }
}