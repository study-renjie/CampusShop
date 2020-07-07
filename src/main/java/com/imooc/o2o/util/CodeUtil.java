package com.imooc.o2o.util;

import com.google.code.kaptcha.Constants;

import javax.servlet.http.HttpServletRequest;

public class CodeUtil
{
    /**
     * 检查验证码是否正确
     * @param request
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request)
    {
        //实际验证码
        String verifyCodeExpected = (String)request.getSession().
                getAttribute(Constants.KAPTCHA_SESSION_KEY);

        //输入的验证码
        String verifyCodeActual = HttpServletRequestUtil.getString(request, "verifyCodeActual");

        if(verifyCodeActual == null || !verifyCodeActual.equals(verifyCodeExpected))
            return false;
        return true;
    }
}
