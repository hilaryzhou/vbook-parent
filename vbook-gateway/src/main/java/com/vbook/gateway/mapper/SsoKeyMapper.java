package com.vbook.gateway.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vbook.model.auth.SsoKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @auther: zhouhuan
 * @Date: 2022/8/24-16:06
 */
@Mapper
public interface SsoKeyMapper extends BaseMapper<SsoKey> {

}
