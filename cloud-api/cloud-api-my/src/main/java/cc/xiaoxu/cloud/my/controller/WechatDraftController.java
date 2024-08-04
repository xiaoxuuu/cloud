package cc.xiaoxu.cloud.my.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpDraftService;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.draft.WxMpAddDraft;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftArticles;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wx/draft")
public class WechatDraftController {

    private final WxMpService wxService;

    @GetMapping("/add")
    public void add() throws WxErrorException, URISyntaxException {

        // 文件上传
        WxMpMaterialService materialService = wxService.getMaterialService();
        // 获取当前类的ClassLoader
        ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        // 使用ClassLoader获取图片的URL
        URL resourceUrl = classLoader.getResource("pic.png");

        // 将URL转换为File对象
        assert resourceUrl != null;
        File file = new File(resourceUrl.toURI());
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        WxMpMaterialUploadResult wxMpMaterialUploadResult = materialService.materialFileUpload(WxConsts.MediaFileType.IMAGE, wxMpMaterial);

        // 草稿
        WxMpDraftService draftService = wxService.getDraftService();
        WxMpDraftArticles wxMpDraftArticles = getWxMpDraftArticles(wxMpMaterialUploadResult.getMediaId());

        WxMpAddDraft wxMpAddDraft = new WxMpAddDraft();
        wxMpAddDraft.setArticles(List.of(wxMpDraftArticles));
        draftService.addDraft(wxMpAddDraft);
    }

    private static WxMpDraftArticles getWxMpDraftArticles(String mediaId) {
        WxMpDraftArticles wxMpDraftArticles = new WxMpDraftArticles();
        wxMpDraftArticles.setTitle("文章标题");
        wxMpDraftArticles.setAuthor("佚名");
        wxMpDraftArticles.setDigest("这是一条摘要");
        wxMpDraftArticles.setContent("## 测试内容");
        wxMpDraftArticles.setThumbMediaId(mediaId);
        wxMpDraftArticles.setNeedOpenComment(1);
        return wxMpDraftArticles;
    }
}
