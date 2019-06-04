package com.tamboot.sample.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ResetPasswordForm {
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

    @NotEmpty(message = "确认密码不能为空")
    private String confirmPassword;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
