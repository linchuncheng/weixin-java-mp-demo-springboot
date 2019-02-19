package com.github.linchuncheng.wx.mp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * 健康检查控制器
 * Created by Jensen Lam on 2018/8/25.
 * </pre>
 *
 * @author <a href="https://github.com/linchuncheng">Jensen Lam</a>
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String healthCheck() {
        return "OK";
    }

}
