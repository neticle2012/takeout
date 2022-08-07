package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Category;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface CategoryService extends IService<Category> {
    R<String> saveCatory(@RequestBody Category category);
    R<Page<Category>> getPage(int page, int pageSize);
}
