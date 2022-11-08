package com.lqs.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lqs.reggie_take_out.common.R;
import com.lqs.reggie_take_out.entity.Employee;
import com.lqs.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,  @RequestBody Employee employee) {

        // 1、将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3、如果查询不到返回登录失败
        if(emp ==null){
            return R.error("登录失败!");
        }

        // 4、密码比对，不一致则返回登陆失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败!");
        }

        // 5、状态比对，如果已经禁用返回失败结果
        if(emp.getStatus()==(0)){
            return R.error("账号已禁用!");
        }

        // 6、登陆成功,将员工id存入session，并返回登录成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    /**
     * 员工退出
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功!");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息:{}",employee.toString());

        // 设置初始密码，需要进行加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 获得创建人的信息
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新建员工成功!");
    }
}
