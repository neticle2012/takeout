package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.Category;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface CategoryService extends IService<Category> {
    R<String> saveCatory(Category category);
    R<Page<Category>> getPage(int page, int pageSize);
    R<String> deleteCategory(Long id);
    R<String> updateCategory(Category category);
    R<List<Category>> listCategory(Category category);
}
