package com.gudt.imis.clubmanageis.service.impl;

import com.gudt.imis.clubmanageis.dao.ClubPayDao;
import com.gudt.imis.clubmanageis.dao.ClubRoleDao;
import com.gudt.imis.clubmanageis.dao.UserDao;
import com.gudt.imis.clubmanageis.model.entity.ClubPay;
import com.gudt.imis.clubmanageis.model.entity.ClubRole;
import com.gudt.imis.clubmanageis.model.result.Result;
import com.gudt.imis.clubmanageis.service.PayService;
import com.gudt.imis.clubmanageis.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: PayServiceImpl
 * @Author: ccjmlkj
 * @Description:
 * @create: 2020-12-19 00:43
 **/
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private ClubPayDao clubPayDao;
    @Autowired
    private ClubRoleDao clubRoleDao;
    @Autowired
    private UserDao userDao;
    @Override
    public Result<List<ClubPay>> getPayList(Integer clubId) {
        List<ClubPay> clubPayList = clubPayDao.getPayList(clubId);
        for (int i = 0;i < clubPayList.size();i ++){
            String userName = userDao.selectByPrimaryKey(clubPayList.get(i).getUserId()).getUserName();
            clubPayList.get(i).setUserName(userName);
        }
        return ResultUtil.success(clubPayList);
    }

    @Override
    public Result<BigDecimal> getTotalPay(Integer clubId) {
        List<ClubPay> clubPayList = clubPayDao.getPayList(clubId);
        BigDecimal totalPay = new BigDecimal(0);
        for (ClubPay clubPay:clubPayList){
            totalPay = totalPay.add(clubPay.getPayAmount());
        }
        return ResultUtil.success(totalPay);
    }

    @Override
    public Result<String> createPay(Integer clubId, Integer userId, String payTag, BigDecimal payAmount, String payReason) {
        ClubRole clubRole = clubRoleDao.selectByUserIdAndClubId(userId, clubId);
        if (clubRole != null) {
            Integer userRole = clubRole.getUserRole();
            if (userRole == 0 || userRole == 1) {
                ClubPay clubPay = new ClubPay();
                clubPay.setClubId(clubId);
                clubPay.setUserId(userId);
                clubPay.setPayTags(payTag);
                clubPay.setPayAmount(payAmount);
                clubPay.setPayReason(payReason);
                clubPay.setPayProof("默认没有");
                clubPay.setUpdateTime(new Date());
                clubPay.setCreateTime(new Date());
                clubPayDao.insertSelective(clubPay);
                return ResultUtil.success("success");
            } else {
                return ResultUtil.error(405, "error");
            }
        } else {
            return ResultUtil.error(1, "error");
        }
    }


}
