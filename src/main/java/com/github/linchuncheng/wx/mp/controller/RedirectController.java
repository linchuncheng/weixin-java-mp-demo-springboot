package com.github.linchuncheng.wx.mp.controller;

import com.github.linchuncheng.wx.mp.config.WxMpConfig;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Edward
 */
@Controller
@RequestMapping("/wx/redirect/{appId}")
public class RedirectController {
    @Autowired
    private WxMpConfig wxMpConfig;

    @RequestMapping("/greet")
    public String greetUser(@PathVariable String appId, @RequestParam String code, ModelMap map) {
        WxMpService mpService = wxMpConfig.getMpService(appId);
        try {
            WxMpOAuth2AccessToken accessToken = mpService.oauth2getAccessToken(code);
            WxMpUser user = mpService.oauth2getUserInfo(accessToken, null);
            map.put("user", user);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return "greet_user";
    }
}
