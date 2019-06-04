package com.tamboot.sample.service.impl;

import com.tamboot.sample.mapper.SystemUserMapper;
import com.tamboot.sample.model.SystemUserModel;
import com.tamboot.sample.service.SystemUserService;
import com.tamboot.security.core.PasswordEncoderFactory;
import com.tamboot.security.core.TambootUserDetails;
import com.tamboot.security.util.SafeSecurityContextHolder;
import com.tamboot.sample.form.ModifyPasswordForm;
import com.tamboot.sample.form.ResetPasswordForm;
import com.tamboot.web.config.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SystemUserServiceImpl implements SystemUserService {
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private PasswordEncoderFactory passwordEncoderFactory;

    @Override
    public TambootUserDetails findProfile() {
        return SafeSecurityContextHolder.getUserDetails();
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public int modifyPassword(ModifyPasswordForm form) {
        Long userId = SafeSecurityContextHolder.getUserDetails().getUserId();
        SystemUserModel userModel = systemUserMapper.selectOneById(userId);

        if (!form.getConfirmPassword().equals(form.getNewPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }
        if (!passwordEncoderFactory.get().matches(form.getOldPassword(), userModel.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        userModel.setPassword(passwordEncoderFactory.get().encode(form.getNewPassword()));
        return systemUserMapper.updateById(userModel);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public int resetPassword(ResetPasswordForm form) {
        SystemUserModel userModel = systemUserMapper.selectOneById(form.getUserId());
        if (userModel == null) {
            throw new BusinessException("该用户不存在");
        }

        if (!form.getConfirmPassword().equals(form.getNewPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        userModel.setPassword(passwordEncoderFactory.get().encode(form.getNewPassword()));
        return systemUserMapper.updateById(userModel);
    }
}
