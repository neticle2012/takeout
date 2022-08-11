package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.AddressBook;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface AddressBookService extends IService<AddressBook> {
    R<AddressBook> saveAddress(AddressBook addressBook);
    R<List<AddressBook>> listAddress();
    R<AddressBook> setDefaultAddress(AddressBook addressBook);
    R<AddressBook> getAddressById(Long id);
    R<String> updateAddress(AddressBook addressBook);
    R<String> deleteAddress(Long id);
}
