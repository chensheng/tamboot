package com.tamboot.mybatis.test.model;

import com.tamboot.mybatis.annotation.IgnoreInInsertUpdateSql;

import java.io.Serializable;
import java.util.Date;

public class BaseModel implements Serializable {
    private static final long serialVersionUID = -3118032057118133430L;

    @IgnoreInInsertUpdateSql
    protected Long id;

    @IgnoreInInsertUpdateSql
    protected Long version;

    @IgnoreInInsertUpdateSql
    protected Date createTime;

    @IgnoreInInsertUpdateSql
    protected Long creator;

    @IgnoreInInsertUpdateSql
    protected Date modifyTime;

    @IgnoreInInsertUpdateSql
    protected Long modifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }
}
