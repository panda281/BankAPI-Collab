package com.gebeya.bankapi.Controller;

import com.gebeya.bankAPI.Model.DTO.*;
import com.gebeya.bankAPI.Model.Entities.Account;
import com.gebeya.bankAPI.Repository.HistoryRepository;
import com.gebeya.bankAPI.Service.AccountService;
import com.gebeya.bankAPI.Service.MobileBankingUserService;
import com.gebeya.bankAPI.Service.Profile;
import com.gebeya.bankAPI.Service.TransactionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v2")
public class AccountController {


    @Autowired
    HistoryRepository historyRepository;

    AccountService accountService;
    TransactionService transactionService;
    MobileBankingUserService mobileBankingUserService;

    Profile profile;

    @Autowired
    public AccountController(AccountService accountService, TransactionService transactionService, MobileBankingUserService mobileBankingUserService, Profile profile)
    {
        this.accountService=accountService;
        this.transactionService=transactionService;
        this.mobileBankingUserService = mobileBankingUserService;
        this.profile=profile;
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "When an Account created successfully"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),

    })

    //it works
    @PostMapping("/account")


    public ResponseEntity<?> postAccount (@RequestBody Account account){
//        log.info("Account content: {}", account);
//        Account createdAccount = ;
        return ResponseEntity.ok(accountService.addAccount(account));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "When an Account and associated customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "when an account can not be found"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),

    })

    @PutMapping("/account/{AccountNo}")
    public ResponseEntity<?>updateAccount(@PathVariable("AccountNo") long accountNo ,@RequestBody Account account){
        return ResponseEntity.ok(accountService.updateAccountCustomer(accountNo,account));
    }

//        @PostMapping("/account")
//    public ResponseEntity<Account> postAccount (){
//            Account senderAccount = accountRepository.findById(10000004L).get();
//                senderAccount.setBalance(50);
//
//        return ResponseEntity.ok(accountRepository.save(senderAccount));
//    }

//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "when operation completed successfully"),
//            @ApiResponse(responseCode = "400", description = "when OTP expire"),
//            @ApiResponse(responseCode = "400", description = "when bad request happens"),
//            @ApiResponse(responseCode = "400", description = "when the balance is insufficient"),
//            @ApiResponse(responseCode = "404", description = "when a customer account can not be found"),
//            @ApiResponse(responseCode = "404", description = "when a customer mobile could not be found"),
//            @ApiResponse(responseCode = "404", description = "when invalid otp or mobileNo inserted"),
//            @ApiResponse(responseCode = "404", description = "when invalid otp or accountNo inserted"),
//            @ApiResponse(responseCode = "500", description = "when internal error occurred"),
//
//    })
//    @PostMapping("/deposit")
//    public ResponseEntity<?> deposit(@RequestBody TransactionRequestDTOtemp t){
//        log.info("Account content: {}", t);
//        return ResponseEntity.ok(accountService.deposit(t));
//    }
//
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "when an operation complete successfully"),
//            @ApiResponse(responseCode = "400", description = "when OTP expire"),
//            @ApiResponse(responseCode = "400", description = "when bad request happens"),
//            @ApiResponse(responseCode = "400", description = "when unknown error occurred"),
//            @ApiResponse(responseCode = "400", description = "when the balance is insufficient"),
//            @ApiResponse(responseCode = "404", description = "when a customer account can not be found"),
//            @ApiResponse(responseCode = "404", description = "when a customer mobile could not be found"),
//            @ApiResponse(responseCode = "404", description = "when invalid otp or mobileNo inserted"),
//            @ApiResponse(responseCode = "404", description = "when invalid otp or accountNo inserted"),
//            @ApiResponse(responseCode = "500", description = "when internal error occurred"),
//
//    })
//    @PostMapping("/withdrawal")
//    public ResponseEntity<?> withdrawal(@RequestBody TransactionRequestDTOtemp transaction){
//        log.info("Account content: {}", transaction);
//        return ResponseEntity.ok(accountService.withdrawal(transaction));
//    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "when transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "when an account is blocked"),
            @ApiResponse(responseCode = "400", description = "when the balance is insufficient"),
            @ApiResponse(responseCode = "400", description = "when an amount is less than or equal to 0"),
            @ApiResponse(responseCode = "404", description = "when an account could not be found"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),

    })

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferDTO transferDTO){
        ResponseModel responseModel = accountService.transfer(transferDTO);
        return ResponseEntity.ok(responseModel);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "return body"),
            @ApiResponse(responseCode = "404", description = "when transaction is empty"),
            @ApiResponse(responseCode = "404", description = "when an account could not be found"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),

    })

    @GetMapping("/ShortStatement/{AccountNo}")
    public ResponseEntity<?> statement(@PathVariable("AccountNo") long id)
    {
        List<ShortStatementDTO> shortStatementDTO = transactionService.shortStatement(id);
        return ResponseEntity.ok(shortStatementDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "return amount"),
            @ApiResponse(responseCode = "404", description = "when an account could not be found"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),

    })

    @GetMapping("/CheckBalance/{AccountNo}")
    public ResponseEntity<?> checkBalance(@PathVariable("AccountNo") long accountNo){
        return ResponseEntity.ok(accountService.checkBalance(accountNo));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "when an account saved successfully"),
            @ApiResponse(responseCode = "400", description = "invalid request"),
            @ApiResponse(responseCode = "404", description = "when an account could not be found"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),

    })

    @PostMapping("/activeMobileBanking")
    public ResponseEntity<?> activateMobileBanking(@RequestBody MobileBankingUsersDTO dto)
    {
        return ResponseEntity.ok(mobileBankingUserService.activeMobileBanking(dto));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "when language changed successfully"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),

    })

    @GetMapping("/changelanguage")
    public ResponseEntity<?> changelanguage(@RequestHeader("Accept-Language") String locale) {
        return ResponseEntity.ok(profile.language(locale));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "when operation completed successfully"),
            @ApiResponse(responseCode = "400", description = "when bad request occurred"),
            @ApiResponse(responseCode = "400", description = "when the balance is insufficient"),
            @ApiResponse(responseCode = "404", description = "when an account could not be found"),
            @ApiResponse(responseCode = "500", description = "when internal error occurred"),
            @ApiResponse(responseCode = "500", description = "when unknown server error occurred"),
    })

    @PostMapping("/topup")
    public ResponseEntity<?> airtime(@RequestBody topUpRequestDTO top){
        TopUpResponseDTO topup = accountService.topUp(top);
        return ResponseEntity.ok(topup);
    }

    @GetMapping("/mobileBankingUsers")
    public ResponseEntity<?> MobileBankingUser()
    {
        return ResponseEntity.ok(mobileBankingUserService.getAllMobileBankingUsers());
    }


//    @DeleteMapping("/account/{id}")
//    public ResponseEntity<?> DeleteAccount(@PathVariable("id") long accountNo)
//    {
//        return ResponseEntity.ok(accountService.deleteAccountCustomer(accountNo));
//    }

    @GetMapping("/transaction")
    public ResponseEntity<?> listTransactions()
    {
        return ResponseEntity.ok(transactionService.listAllTransactions());
    }


    @PostMapping("/withdrawalForMerchant")
    public ResponseEntity<?> WithdrawalForMerchant(MerchantCustomerDTO request)
    {
        return ResponseEntity.ok(accountService.withdrawalForMerchantCustomer(request));
    }

    @PostMapping("/withdrawalForDefault")
    public ResponseEntity<?> withdrawalForDefault(DefaultCustomerDTO request)
    {
        return ResponseEntity.ok(accountService.withdrawalForDefaultCustomer(request));
    }

    @PostMapping("/depositForMerchant")
    public ResponseEntity<?> DepositForMerchant (MerchantCustomerDTO request)
    {
        return ResponseEntity.ok(accountService.depositForMerchantCustomer(request));
    }

    @PostMapping("/depositForDefault")
    public ResponseEntity<?> DepositForDefault (DefaultCustomerDTO request)
    {
        return ResponseEntity.ok(accountService.depositForDefaultCustomer(request));
    }


}