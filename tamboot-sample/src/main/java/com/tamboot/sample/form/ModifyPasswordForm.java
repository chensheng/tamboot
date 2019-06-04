package com.tamboot.sample.form;

import javax.validation.constraints.NotEmpty;

public class ModifyPasswordForm {
    @NotEmpty(message = "原密码不能为空")
    private String oldPassword;

    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

    @NotEmpty(message = "确认密码不能为空")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
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
