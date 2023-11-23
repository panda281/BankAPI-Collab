package com.gebeya.bankapi.Service;
import com.gebeya.bankAPI.Model.DTO.MobileBankingUsersDTO;
import com.gebeya.bankAPI.Model.DTO.ResponseModel;
import com.gebeya.bankAPI.Model.Entities.MobileBankingUser;

public interface MobileBankingUserService {
    public ResponseModel activeMobileBanking(MobileBankingUsersDTO user);

    public Iterable<MobileBankingUser> getAllMobileBankingUsers();



//    public boolean login(MobileBankingUsersDTO user)
}
