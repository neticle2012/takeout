package org.neticle.takeout.controller;

import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.AddressBook;
import org.neticle.takeout.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增收货地址
     */
    @PostMapping
    public R<AddressBook> saveAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.saveAddress(addressBook);
    }

    /**
     * 查询当前登录用户的全部收货地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> listAddress() {
        return addressBookService.listAddress();
    }

    /**
     * 设置默认收货地址
     */
    @PutMapping("/default")
    public R<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.setDefaultAddress(addressBook);
    }

    /**
     * 根据id查询收货地址
     */
    @GetMapping("/{id}")
    public R<AddressBook> getAddressById(@PathVariable("id") Long id) {
        return addressBookService.getAddressById(id);
    }

    /**
     * 修改收货地址
     */
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.updateAddress(addressBook);
    }

    /**
     * 根据id删除收货地址
     */
    @DeleteMapping
    public R<String> deleteAddress(@RequestParam("ids") Long id) {
        return addressBookService.deleteAddress(id);
    }

    /**
     * 查询默认收货地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress() {
        return addressBookService.getDefaultAddress();
    }
}
