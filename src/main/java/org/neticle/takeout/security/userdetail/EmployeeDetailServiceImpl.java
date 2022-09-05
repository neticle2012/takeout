package org.neticle.takeout.security.userdetail;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
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
 * UserDetailService对于员工的实现子类，用于从数据库中查询员工信息
 */
@Service
@Slf4j
public class EmployeeDetailServiceImpl implements UserDetailsService {
    @Autowired
    private EmployeeService employeeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1. 根据员工名获取数据库中的员工
        // SELECT * FROM employee WHERE username = Employee.username
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, username);
        Employee emp = employeeService.getOne(lqw);
        if (Objects.isNull(emp)) {
            log.warn("员工 {} 不存在", username);
            throw new UsernameNotFoundException("员工不存在");
        }
        //TODO 2.查询权限信息
        //3. 返回UserDetails的实现子类
        return new EmployeeDetail(emp);
    }
}
