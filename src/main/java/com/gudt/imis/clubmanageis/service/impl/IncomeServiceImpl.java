package com.gudt.imis.clubmanageis.service.impl;

import com.gudt.imis.clubmanageis.dao.ClubIncomeDao;
import com.gudt.imis.clubmanageis.dao.ClubRoleDao;
import com.gudt.imis.clubmanageis.dao.UserDao;
import com.gudt.imis.clubmanageis.model.entity.Club;
import com.gudt.imis.clubmanageis.model.entity.ClubIncome;
import com.gudt.imis.clubmanageis.model.entity.ClubRole;
import com.gudt.imis.clubmanageis.model.result.Result;
import com.gudt.imis.clubmanageis.service.IncomeService;
import com.gudt.imis.clubmanageis.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: IncomeServiceImpl
 * @Author: ccjmlkj
 * @Description:
 * @create: 2020-12-18 22:25
 **/
@Service
public class IncomeServiceImpl implements IncomeService {
    @Autowired
    private ClubIncomeDao clubIncomeDao;
    @Autowired
    private ClubRoleDao clubRoleDao;
    @Autowired
    private UserDao userDao;
    @Override
    public Result<List<ClubIncome>> getIncomeList(Integer clubId) {
        List<ClubIncome> clubIncomeList = clubIncomeDao.getIncomeList(clubId);
        for (int i = 0; i < clubIncomeList.size(); i++){
            String userName = userDao.selectByPrimaryKey(clubIncomeList.get(i).getUserId()).getUserName();
            clubIncomeList.get(i).setUserName(userName);
        }
        return ResultUtil.success(clubIncomeList);
    }

    @Override
    public Result<BigDecimal> getTotalIncome(Integer clubId) {
        BigDecimal totalIncome = new BigDecimal(0);
        List<ClubIncome> clubIncomeList = clubIncomeDao.getIncomeList(clubId);
        for (ClubIncome clubIncome:clubIncomeList){
            totalIncome = totalIncome.add(clubIncome.getIncomeAmount());
        }
        return ResultUtil.success(totalIncome);
    }

    @Override
    public Result<String> createIncome(String incomeTag, Integer clubId, Integer userId, BigDecimal incomeAmount, String incomeReason) {
        ClubRole clubRole = clubRoleDao.selectByUserIdAndClubId(userId,clubId);
        if (clubRole != null){
            Integer userRole = clubRole.getUserRole();
            if (userRole == 0 || userRole == 1){
                ClubIncome clubIncome = new ClubIncome();
                clubIncome.setClubId(clubId);
                clubIncome.setUserId(userId);
                clubIncome.setIncomeTags(incomeTag);
                clubIncome.setIncomeAmount(incomeAmount);
                clubIncome.setIncomeReason(incomeReason);
                clubIncome.setIncomeProof("默认没有");
                clubIncome.setCreateTime(new Date());
                clubIncome.setUpdateTime(new Date());
                clubIncomeDao.insertSelective(clubIncome);
                return ResultUtil.success("success");
            }else {
                return ResultUtil.error(405,"error");
            }
        }else{
            return ResultUtil.error(1,"error");
        }
    }


}
