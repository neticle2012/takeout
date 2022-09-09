package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.EmployeeMapper;
import org.neticle.takeout.pojo.Employee;
import org.neticle.takeout.service.EmployeeService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {
    @Override
    @Deprecated
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        //1、将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名来查数据库，如果没有查询到则返回失败结果
        // SELECT * FROM employee WHERE username = Employee.username
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = this.getOne(lqw);
        if (emp == null) {
            return R.error("登录失败");
        }
        //3、比对密码，如果不一致则返回失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //4、查看员工状态，如果已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //5、登录成功，将用户id存入Session并返回成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @Override
    @Deprecated
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @Override
    public R<String> saveEmp(HttpServletRequest request, Employee employee) {
        log.info("新增员工，员工信息: {}", employee.toString());
        //设置初始密码123456，需要进行加盐加密处理
        employee.setPassword(new BCryptPasswordEncoder().encode("123456"));
        this.save(employee);
        return R.success("新增员工成功");
    }

    @Override
    public R<Page<Employee>> getPage(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        //SELECT * FROM employee WHERE name LIKE %name% ORDER BY update_time DESC
        //LIMIT (page - 1)*pageSize ,pageSize
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);
        this.page(pageInfo, lqw);//执行查询
        return R.success(pageInfo);
    }

    @Override
    public R<String> updateEmp(HttpServletRequest request, Employee employee) {
        log.info(employee.toString());
        log.info("线程id = {}", Thread.currentThread().getId());
        this.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @Override
    public R<Employee> getByEmpId(Long id) {
        log.info("根据id查询员工信息...");
        boolean flag = SecurityContextHolder.getContext().getAuthentication() != null;
        log.info("测试authentication是否存在于SecurityContextHolder中[{}]", flag);
        Employee employee = this.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
}
