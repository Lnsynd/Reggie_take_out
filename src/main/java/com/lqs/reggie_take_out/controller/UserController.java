package com.lqs.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lqs.reggie_take_out.common.R;
import com.lqs.reggie_take_out.entity.User;
import com.lqs.reggie_take_out.service.UserService;
import com.lqs.reggie_take_out.utils.SMSUtils;
import com.lqs.reggie_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送手机验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotBlank(phone)) {
            // 生成随机的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("生成的验证码：{}", code);
            // 发送短信
//            SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", "18435375345", code);

            // 将生成的验证码存到session
//            session.setAttribute(phone, code);

            // 将生成的验证码存到redis
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 用户登录
     *
     * @param userMap 存放 手机号 验证码
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map userMap, HttpSession session) {
        log.info("userMap:{}", userMap);
        // 获取手机号
        String phone = userMap.get("phone").toString();

        // 获取验证码
        String code = userMap.get("code").toString();

        // 从Session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        // 从redis获取保存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        // 进行验证码的比对
        if (codeInSession != null && code.equals(codeInSession)) {

            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);

            // 用户未注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());


            // 如果登录成功，删除redis中存的验证码信息
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }


    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session){
        log.info("session:{}",session);
        session.removeAttribute("user");
        return R.success("退出成功!");
    }
}
