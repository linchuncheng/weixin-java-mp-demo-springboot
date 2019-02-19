package com.github.linchuncheng.wx.mp.config;

import com.github.linchuncheng.wx.mp.handler.*;
import com.github.linchuncheng.wx.mp.properties.WxMpProperties;
import com.google.common.collect.Maps;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.chanjar.weixin.common.api.WxConsts.*;

/**
 * wechat mp service
 *
 * @author Jensen Lam(https://github.com/linchuncheng)
 */
@Configuration
public class WxMpConfig implements InitializingBean {
    @Autowired
    private LogHandler logHandler;
    @Autowired
    private NullHandler nullHandler;
    @Autowired
    private KfSessionHandler kfSessionHandler;
    @Autowired
    private StoreCheckNotifyHandler storeCheckNotifyHandler;
    @Autowired
    private LocationHandler locationHandler;
    @Autowired
    private MenuHandler menuHandler;
    @Autowired
    private MsgHandler msgHandler;
    @Autowired
    private UnsubscribeHandler unsubscribeHandler;
    @Autowired
    private SubscribeHandler subscribeHandler;
    @Autowired
    private ScanHandler scanHandler;
    @Autowired
    private WxMpProperties wxMpProperties;
    private Map<String, WxMpMessageRouter> routers = Maps.newHashMap();
    private Map<String, WxMpService> mpServices = Maps.newHashMap();

    public WxMpMessageRouter getRouter(String appId) {
        return routers.get(appId);
    }

    public WxMpService getMpService(String appId) {
        WxMpService wxMpService = mpServices.get(appId);
        if (wxMpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }
        return wxMpService;
    }

    @Override
    public void afterPropertiesSet() {
        // 代码里 getConfigs()处报错的同学，请注意仔细阅读项目说明，你的IDE需要引入lombok插件！！！！
        List<WxMpProperties.MpConfig> mpConfigs = wxMpProperties.getConfigs();
        if (mpConfigs == null) {
            throw new RuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
        }
        mpServices = mpConfigs.stream().map(config -> {
            WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
            configStorage.setAppId(config.getAppId());
            configStorage.setSecret(config.getSecret());
            configStorage.setToken(config.getToken());
            configStorage.setAesKey(config.getAesKey());
            WxMpService service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(configStorage);
            routers.put(config.getAppId(), this.newRouter(service));
            return service;
        }).collect(Collectors.toMap(s -> s.getWxMpConfigStorage().getAppId(), a -> a, (o, n) -> o));
    }

    private WxMpMessageRouter newRouter(WxMpService wxMpService) {
        final WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        router.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
            .handler(this.kfSessionHandler).end();
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
            .handler(this.kfSessionHandler)
            .end();
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
            .handler(this.kfSessionHandler).end();

        // 门店审核事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.POI_CHECK_NOTIFY)
            .handler(this.storeCheckNotifyHandler).end();

        // 自定义菜单事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(MenuButtonType.CLICK).handler(this.menuHandler).end();

        // 点击菜单连接事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(MenuButtonType.VIEW).handler(this.nullHandler).end();

        // 关注事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.SUBSCRIBE).handler(this.subscribeHandler)
            .end();

        // 取消关注事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.UNSUBSCRIBE)
            .handler(this.unsubscribeHandler).end();

        // 上报地理位置事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.LOCATION).handler(this.locationHandler)
            .end();

        // 接收地理位置消息
        router.rule().async(false).msgType(XmlMsgType.LOCATION)
            .handler(this.locationHandler).end();

        // 扫码事件
        router.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.SCAN).handler(this.scanHandler).end();

        // 默认
        router.rule().async(false).handler(this.msgHandler).end();

        return router;
    }

}
