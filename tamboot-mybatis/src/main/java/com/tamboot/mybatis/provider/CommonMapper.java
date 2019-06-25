package com.tamboot.mybatis.provider;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CommonMapper<MODEL, ID> {
    @InsertProvider(type = CommonSqlProvider.class, method = "insert")
    int insert(MODEL model);

    @UpdateProvider(type = CommonSqlProvider.class, method = "updateById")
    int updateById(MODEL model);

    @UpdateProvider(type = CommonSqlProvider.class, method = "updateNotNullById")
    int updateNotNullById(MODEL model);

    @DeleteProvider(type = CommonSqlProvider.class, method = "deleteById")
    int deleteById(ID id);

    @SelectProvider(type = CommonSqlProvider.class, method = "selectOneById")
    MODEL selectOneById(ID id);

    @SelectProvider(type = CommonSqlProvider.class, method = "selectAllByExample")
    List<MODEL> selectAllByExample(@Param("example") MODEL example, @Param("orderBys") String[] orderBys);

    @SelectProvider(type = CommonSqlProvider.class, method = "pageByExample")
    Page<MODEL> pageByExample(@Param("example") MODEL example, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("orderBys") String[] orderBys);

    @SelectProvider(type = CommonSqlProvider.class, method = "countByExample")
    long countByExample(MODEL example);
}
