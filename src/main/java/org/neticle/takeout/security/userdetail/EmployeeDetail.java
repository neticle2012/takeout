package org.neticle.takeout.security.userdetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neticle.takeout.pojo.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Faruku123
 * @version 1.0
 * UserDetails对于员工的实现子类，封装了从数据库中查询到的员工的各种信息
 */
@Data
@AllArgsConstructor
public class EmployeeDetail implements UserDetails {
    private Employee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getUsername();
    }

    @Override //账号是否过期
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //账号是否被锁定
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //密码是否过期
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //账号是否被禁用
    public boolean isEnabled() {
        return employee.getStatus() != 0;
    }
}
