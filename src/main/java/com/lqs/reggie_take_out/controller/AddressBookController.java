package com.lqs.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lqs.reggie_take_out.common.BaseContext;
import com.lqs.reggie_take_out.common.R;
import com.lqs.reggie_take_out.entity.AddressBook;
import com.lqs.reggie_take_out.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;


    /**
     * 显示用户的地址簿
     *
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId, addressBook.getUserId());
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(lambdaQueryWrapper));
    }

    /**
     * 保存用户信息
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        // 为地址设置所属用户的id
        addressBook.setUserId(BaseContext.getCurrentId());
        // 保存id
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置地址为默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("AddressBook:{}", addressBook);

        // 设置登录用户该地址的默认状态为0
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        lambdaUpdateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(lambdaUpdateWrapper);

        // 设置该地址的默认状态为1
        addressBook.setIsDefault(1);

        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }


    /**
     * 修改用户信息界面 用户信息回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getId, id);

        AddressBook addressBook = addressBookService.getOne(lambdaUpdateWrapper);
        if (addressBook != null) {
            return R.success(addressBook);
        }
        return R.error("查找用户失败");
    }

    /**
     * 修改用户信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook){
        log.info("地址簿信息：{}",addressBook);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }


    /**
     * 删除用户
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }
}
