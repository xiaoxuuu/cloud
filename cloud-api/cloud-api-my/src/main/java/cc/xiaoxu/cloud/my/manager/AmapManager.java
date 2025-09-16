package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.amap.*;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import cc.xiaoxu.cloud.core.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    private static final String ID_SEARCH_URL = "https://restapi.amap.com/v5/place/detail";

    /**
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/inputtips">输入提示</a>
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
            log.debug("高德地图输入提示API响应: {}", response);

            return JsonUtils.parse(response, AmapInputTipsResponseDTO.class);
        } catch (Exception e) {
            log.error("调用高德地图输入提示API失败", e);
            throw new RuntimeException("调用高德地图输入提示API失败: " + e.getMessage());
        }
    }

    /**
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/newpoisearch">搜索POI</a>
     *
     * @param requestDTO 请求参数
     * @return POI搜索响应结果
     */
    public String searchPoiString(AmapPoiSearchRequestDTO requestDTO) {
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
            log.debug("高德地图搜索POI API响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("调用高德地图搜索POI API失败", e);
            throw new RuntimeException("调用高德地图搜索 POI API 异常");
        }
    }

    /**
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/newpoisearch">搜索POI</a>
     *
     * @param requestDTO 请求参数
     * @return POI搜索响应结果
     */
    public AmapPoiSearchResponseDTO searchPoi(AmapPoiSearchRequestDTO requestDTO) {

        return JsonUtils.parse(searchPoiString(requestDTO), AmapPoiSearchResponseDTO.class);
    }

    /**
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/newpoisearch#t6">ID 搜索</a>
     *
     * @param requestDTO 请求参数
     * @return ID 搜索响应结果
     */
    public String idSearchString(AmapIdSearchRequestDTO requestDTO) {
        try {
            HttpUtils httpUtils = HttpUtils.builder()
                    .url(ID_SEARCH_URL)
                    .param("key", amapApiKey)
                    .param("id", String.join("|", requestDTO.getIdList()))
                    .param("output", "json");

            // 添加可选参数
            if (CollectionUtils.isNotEmpty(requestDTO.getShowFieldList())) {
                httpUtils.param("show_fields", String.join(",", requestDTO.getShowFieldList()));
            } else {
                // 默认全部获取
                httpUtils.param("show_fields", "children,business,indoor,navi,photos");
            }

            String response = httpUtils.get();
            log.info("高德地图搜索POI API响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("调用高德地图搜索POI API失败", e);
            throw new RuntimeException("调用高德地图搜索 POI API 异常");
        }
    }

    public AmapPoiSearchResponseDTO idSearch(AmapIdSearchRequestDTO requestDTO) {

        return JsonUtils.parse(idSearchString(requestDTO), AmapPoiSearchResponseDTO.class);
    }
}