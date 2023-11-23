package com.gebeya.bankapi.Service;


import com.gebeya.bankapi.Model.DTO.MobileBankingUsersDTO;
import com.gebeya.bankapi.Model.DTO.ResponseModel;
import com.gebeya.bankapi.Model.Entities.MobileBankingUser;

public interface MobileBankingUserService {
    public ResponseModel activeMobileBanking(MobileBankingUsersDTO user);

    public Iterable<MobileBankingUser> getAllMobileBankingUsers();



//    public boolean login(MobileBankingUsersDTO user)
}
