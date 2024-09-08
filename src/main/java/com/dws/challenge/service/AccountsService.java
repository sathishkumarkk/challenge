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

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
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
    BigDecimal amount = fundTransfer.getFundToBeTransferred();
    if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(debitAccount.getBalance()) > 0) {
      throw new InSufficientFundException("Maintain sufficient balance before Fund Transfer.");
    }
    debitAccount.setBalance(debitAccount.getBalance().subtract(amount));
    creditAccount.setBalance(creditAccount.getBalance().add(amount));
    accountsRepository.updateAccount(debitAccount);
    accountsRepository.updateAccount(creditAccount );
  }

}
