package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.FundTransfer;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.InSufficientFundException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  @Getter
  private final EmailNotificationService emailNotificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, EmailNotificationService emailNotificationService) {
    this.accountsRepository = accountsRepository;
    this.emailNotificationService = emailNotificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  /**
   * @param fundTransfer
   * This will transfer fund to the credit Account provided
   */
  public void transferFund(FundTransfer fundTransfer) throws AccountNotFoundException, InSufficientFundException{
    Account debitAccount = getAccount(fundTransfer.getDebitAccountId());
    Account creditAccount = getAccount(fundTransfer.getCreditAccountId());
    if(Optional.ofNullable(debitAccount).isEmpty()){
      throw new AccountNotFoundException("Debit Account Not Found");
    }
    if(Optional.ofNullable(creditAccount).isEmpty()){
      throw new AccountNotFoundException("Credit Account Not Found");
    }
    BigDecimal amount = fundTransfer.getFundToBeTransferred();
    if(amount.compareTo(BigDecimal.ZERO) <= 0 ){
      throw new InSufficientFundException("Overdrafts is not supported! ");
    }
    if (amount.compareTo(debitAccount.getBalance()) > 0) {
      throw new InSufficientFundException("Maintain sufficient balance before Fund Transfer.");
    }
    debitAccount.setBalance(debitAccount.getBalance().subtract(amount));
    emailNotificationService.notifyAboutTransfer(debitAccount,
            "Fund Transfer Success and Debited with amount "
                    + fundTransfer.getFundToBeTransferred().toString());
    creditAccount.setBalance(creditAccount.getBalance().add(amount));
    accountsRepository.updateAccount(debitAccount);
    emailNotificationService.notifyAboutTransfer(creditAccount,
            "Fund Transfer Success and Credited with amount "
                    + fundTransfer.getFundToBeTransferred().toString());
    accountsRepository.updateAccount(creditAccount );
  }

}
