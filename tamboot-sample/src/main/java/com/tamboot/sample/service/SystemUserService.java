package com.tamboot.sample.service;

import com.tamboot.security.core.TambootUserDetails;
import com.tamboot.sample.form.ModifyPasswordForm;
import com.tamboot.sample.form.ResetPasswordForm;

public interface SystemUserService {
    TambootUserDetails findProfile();

    int modifyPassword(ModifyPasswordForm form);

    int resetPassword(ResetPasswordForm form);
}
