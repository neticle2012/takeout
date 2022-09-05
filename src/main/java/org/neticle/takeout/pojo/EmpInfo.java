package org.neticle.takeout.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Faruku123
 * @version 1.0
 * 给前端返回的员工信息对象
 */
@AllArgsConstructor
@Data
public class EmpInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String empUsername;
    private String empName;
}
