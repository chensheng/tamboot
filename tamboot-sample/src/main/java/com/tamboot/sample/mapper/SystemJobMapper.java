package com.tamboot.sample.mapper;

import com.tamboot.sample.model.SystemJobModel;

import java.util.List;

public interface SystemJobMapper {
    List<SystemJobModel> selectAll();
}
