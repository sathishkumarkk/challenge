package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.FundTransfer;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InSufficientFundException;
import com.dws.challenge.service.AccountsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }

    @PostMapping("/fund/transfer")
    public ResponseEntity<?> transferFund(@RequestBody FundTransfer fundTransfer) {
        log.info("Fund Transfer Initiated from Account id {} to Account id {}",
                fundTransfer.getDebitAccountId(),
                fundTransfer.getCreditAccountId()
        );
        try {
            this.accountsService.transferFund(fundTransfer);
            log.info("Fund Transfer Success for Amount : {}", fundTransfer.getFundToBeTransferred());
        } catch (AccountNotFoundException | InSufficientFundException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>("Fund Transfer Success", HttpStatus.ACCEPTED);
    }
}
