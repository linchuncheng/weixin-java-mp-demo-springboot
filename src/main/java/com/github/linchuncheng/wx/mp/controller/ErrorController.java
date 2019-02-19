package com.github.linchuncheng.wx.mp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * 出错页面控制器
 * Created by Jensen Lam on 2018/8/25.
 * </pre>
 *
 * @author <a href="https://github.com/linchuncheng">Jensen Lam</a>
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

  @GetMapping(value = "/404")
  public String error404() {
    return "error";
  }

  @GetMapping(value = "/500")
  public String error500() {
    return "error";
  }

}
