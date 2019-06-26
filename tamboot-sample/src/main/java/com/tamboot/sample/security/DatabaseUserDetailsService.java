package com.tamboot.sample.security;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.sample.constants.UserStatus;
import com.tamboot.sample.mapper.SystemUserMapper;
import com.tamboot.sample.model.SystemUserModel;
import com.tamboot.security.core.TambootUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUserModel userModel = systemUserMapper.selectOneByUsername(username);
        if (userModel == null) {
            throw new UsernameNotFoundException("username not found");
        }

        return TambootUserDetails
                .init(userModel.getId(), userModel.getUsername(), userModel.getPassword())
                .disabled(userModel.getStatus() == null || userModel.getStatus().equals(UserStatus.DISABLED))
                .roles(TextUtil.splitByComma(userModel.getRoles()))
                .build();
    }
}
