package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.neticle.takeout.dto.EmployeeDto;
import org.neticle.takeout.pojo.Employee;
import org.neticle.takeout.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Faruku123
 * @version 1.0
 */
@Service
public class EmployeeDetailServiceImpl implements UserDetailsService {
    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1. 根据员工名获取数据库中的员工
        // SELECT * FROM employee WHERE username = Employee.username
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, username);
        Employee emp = employeeService.getOne(lqw);
        if (Objects.isNull(emp)) {
            throw new RuntimeException("员工不存在");
        }
        //TODO 2.查询权限信息
        //3. 返回UserDetails的实现子类
        EmployeeDto empDto = new EmployeeDto();
        empDto.setEmployee(emp);
        return empDto;
    }
}
