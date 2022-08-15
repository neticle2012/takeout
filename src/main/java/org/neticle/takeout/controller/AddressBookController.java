package org.neticle.takeout.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "地址相关接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增收货地址
     */
    @ApiOperation(value = "新增收货地址接口")
    @ApiImplicitParam(name = "addressBook", value = "地址", required = true)
    @PostMapping
    public R<AddressBook> saveAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.saveAddress(addressBook);
    }

    /**
     * 查询当前登录用户的全部收货地址
     */
    @ApiOperation(value = "查询用户所有收货地址接口")
    @GetMapping("/list")
    public R<List<AddressBook>> listAddress() {
        return addressBookService.listAddress();
    }

    /**
     * 设置默认收货地址
     */
    @ApiOperation(value = "设置默认收货地址接口")
    @ApiImplicitParam(name = "addressBook", value = "地址", required = true)
    @PutMapping("/default")
    public R<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.setDefaultAddress(addressBook);
    }

    /**
     * 根据id查询收货地址
     */
    @ApiOperation(value = "根据id查询收货地址接口")
    @ApiImplicitParam(name = "id", value = "地址主键id", required = true)
    @GetMapping("/{id}")
    public R<AddressBook> getAddressById(@PathVariable("id") Long id) {
        return addressBookService.getAddressById(id);
    }

    /**
     * 修改收货地址
     */
    @ApiOperation(value = "修改收货地址接口")
    @ApiImplicitParam(name = "addressBook", value = "地址", required = true)
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBook addressBook) {
        return addressBookService.updateAddress(addressBook);
    }

    /**
     * 根据id删除收货地址
     */
    @ApiOperation(value = "根据id删除收货地址接口")
    @ApiImplicitParam(name = "ids", value = "地址主键id", required = true)
    @DeleteMapping
    public R<String> deleteAddress(@RequestParam("ids") Long id) {
        return addressBookService.deleteAddress(id);
    }

    /**
     * 查询默认收货地址
     */
    @ApiOperation(value = "查询默认收货地址接口")
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress() {
        return addressBookService.getDefaultAddress();
    }
}
