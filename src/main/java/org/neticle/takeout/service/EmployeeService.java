package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Employee;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface EmployeeService extends IService<Employee> {
    R<Employee> login(HttpServletRequest request, Employee employee);
    R<String> logout(HttpServletRequest request);
    R<String> saveEmp(HttpServletRequest request, Employee employee);
    R<Page<Employee>> getPage(int page, int pageSize, String name);
}
