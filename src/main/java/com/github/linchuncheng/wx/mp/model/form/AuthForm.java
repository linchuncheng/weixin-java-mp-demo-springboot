package com.github.linchuncheng.wx.mp.model.form;

import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotBlank;

@Data
public class AuthForm {
    @NotBlank
    private String signature;
    @NotBlank
    private String timestamp;
    @NotBlank
    private String nonce;
    @NotBlank
    private String echostr;
}
