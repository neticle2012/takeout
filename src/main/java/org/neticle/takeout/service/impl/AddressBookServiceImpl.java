package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.BaseContext;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.AddressBookMapper;
import org.neticle.takeout.pojo.AddressBook;
import org.neticle.takeout.service.AddressBookService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
        implements AddressBookService {
    @Override
    public R<AddressBook> saveAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook: {}", addressBook);
        this.save(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R<List<AddressBook>> listAddress() {
        //SELECT * FROM address_book WHERE user_id = session.id ORDER BY update_time DESC
        LambdaQueryWrapper<AddressBook> lqwAddressBook = new LambdaQueryWrapper<>();
        lqwAddressBook.eq(AddressBook::getUserId, BaseContext.getCurrentId())
                      .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = this.list(lqwAddressBook);
        return R.success(addressBooks);
    }

    @Override
    public R<AddressBook> setDefaultAddress(AddressBook addressBook) {
        log.info("addressBook: {}", addressBook);
        //首先，将当前用户下的所有收货地址的is_default字段都设置为0
        //设置字段的时候应该用.set而不是传入pojo，因为这是后端偷偷设置的，不应该改变update_time
        //UPDATE address_book SET is_default = 0 WHERE user_id = session.id
        LambdaUpdateWrapper<AddressBook> luwAddressBook = new LambdaUpdateWrapper<>();
        luwAddressBook.eq(AddressBook::getUserId, BaseContext.getCurrentId())
                      .set(AddressBook::getIsDefault, 0);
        this.update(luwAddressBook);
        //然后，将指定的收货地址的is_default字段设置为1
        //UPDATE address_book SET is_default = 1 WHERE id = addressBook.id
        addressBook.setIsDefault(1);
        this.updateById(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R<AddressBook> getAddressById(Long id) {
        AddressBook addressBook = this.getById(id);
        return addressBook != null ? R.success(addressBook) : R.error("没有找到收货地址");
    }

    @Override
    public R<String> updateAddress(AddressBook addressBook) {
        this.updateById(addressBook);
        return R.success("修改收货地址成功");
    }

    @Override
    public R<String> deleteAddress(Long id) {
        this.removeById(id);
        return R.success("删除收货地址成功");
    }
}
