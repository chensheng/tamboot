package com.tamboot.mybatis.core;

import com.tamboot.mybatis.annotation.IgnoreInInsertUpdateSql;

import java.io.Serializable;
import java.util.Date;

public class BaseModel implements Serializable {
    private static final long serialVersionUID = -4438154607826453448L;

    @IgnoreInInsertUpdateSql
    protected Long id;

    @IgnoreInInsertUpdateSql
    protected Long creator;

    @IgnoreInInsertUpdateSql
    protected Date createTime;

    @IgnoreInInsertUpdateSql
    protected Long modifier;

    @IgnoreInInsertUpdateSql
    protected Date modifyTime;

    @IgnoreInInsertUpdateSql
    protected Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
