package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Employee;
import org.neticle.takeout.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Faruku123
 * @version 1.0
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        return employeeService.login(request, employee);
    }

    /**
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        return employeeService.logout(request);
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> saveEmp(HttpServletRequest request, @RequestBody Employee employee){
        return employeeService.saveEmp(request, employee);
    }

    /**
     * 员工信息分页查询
     */
    @GetMapping("/page")
    public R<Page<Employee>> getPage(int page, int pageSize, String name){
        return employeeService.getPage(page, pageSize, name);
    }
}
