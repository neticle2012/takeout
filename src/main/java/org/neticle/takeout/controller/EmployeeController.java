package org.neticle.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "员工相关接口")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 新增员工
     */
    @ApiOperation(value = "新增员工接口")
    @ApiImplicitParam(name = "employee", value = "员工", required = true)
    @PostMapping
    public R<String> saveEmp(HttpServletRequest request, @RequestBody Employee employee){
        return employeeService.saveEmp(request, employee);
    }

    /**
     * 员工信息分页查询
     */
    @ApiOperation(value = "员工分页查询接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", required = true),
                        @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
                        @ApiImplicitParam(name = "name", value = "员工名称", required = false)})
    @GetMapping("/page")
    public R<Page<Employee>> getPage(int page, int pageSize, String name){
        return employeeService.getPage(page, pageSize, name);
    }

    /**
     * 根据id修改员工信息
     */
    @ApiOperation(value = "根据id修改员工接口")
    @ApiImplicitParam(name = "employee", value = "员工", required = true)
    @PutMapping
    public R<String> updateEmp(HttpServletRequest request, @RequestBody Employee employee){
        return employeeService.updateEmp(request, employee);
    }

    /**
     * 根据id查询员工信息
     */
    @ApiOperation(value = "根据id查询员工接口")
    @ApiImplicitParam(name = "id", value = "员工主键id", required = true)
    @GetMapping("/{id}")
    public R<Employee> getByEmpId(@PathVariable("id") Long id){
        return employeeService.getByEmpId(id);
    }
}
