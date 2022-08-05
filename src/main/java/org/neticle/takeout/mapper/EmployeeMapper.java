package org.neticle.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.neticle.takeout.pojo.Employee;

/**
 * @author Faruku123
 * @version 1.0
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
