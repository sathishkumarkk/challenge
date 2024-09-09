package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@Execution(ExecutionMode.SAME_THREAD)
class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  void createDuplicateAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  void createAccountNoAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  void getAccount() throws Exception {
    String uniqueAccountId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
      .andExpect(status().isOk())
      .andExpect(
        content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
  }

  @Test
  void fundTransferTest() throws Exception {
    String debitAccId = "Id-11";
    Account debitAccount = new Account(debitAccId, new BigDecimal("123.45"));
    this.accountsService.createAccount(debitAccount);
    String creditAccId = "Id-22";
    Account creditAccount = new Account(creditAccId, new BigDecimal("223.45"));
    this.accountsService.createAccount(creditAccount);
    this.mockMvc.perform(post("/v1/accounts/fund/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"debitAccountId\": \""+debitAccId+"\",\"creditAccountId\": \""+creditAccId+"\",\"fundToBeTransferred\": 10.01}"))
            .andExpect(status().isAccepted());
  }

  @Test
  void fundTransferTest_Debit_AccountNotFound() throws Exception {
    String creditAccId = "Id-" + System.currentTimeMillis();
    Account creditAccount = new Account(creditAccId, new BigDecimal("223.45"));
    this.accountsService.createAccount(creditAccount);
    this.mockMvc.perform(post("/v1/accounts/fund/transfer").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"debitAccountId\": \"Id-11\",\"creditAccountId\": \""+creditAccId+"\",\"fundToBeTransferred\": 10.01}"))
            .andExpect(status().isNotAcceptable()).andExpect(content().string("Debit Account Not Found"));
  }

  @Test
  void fundTransferTest_Credit_AccountNotFound() throws Exception {
    String debitAccId = "Id-" + System.currentTimeMillis();
    Account debitAccount = new Account(debitAccId, new BigDecimal("123.45"));
    this.accountsService.createAccount(debitAccount);
    this.mockMvc.perform(post("/v1/accounts/fund/transfer").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"debitAccountId\": \""+debitAccId+"\",\"creditAccountId\": \"Id-21\",\"fundToBeTransferred\": 10.01}"))
            .andExpect(status().isNotAcceptable()).andExpect(content().string("Credit Account Not Found"));
  }

  @Test
  void fundTransferTest_InSufficientFund() throws Exception {
    String debitAccId = "Id-777";// + System.currentTimeMillis();
    Account debitAccount = new Account(debitAccId, new BigDecimal("123.45"));
    this.accountsService.createAccount(debitAccount);
    String creditAccId = "Id-888";// + System.currentTimeMillis();
    Account creditAccount = new Account(creditAccId, new BigDecimal("223.45"));
    this.accountsService.createAccount(creditAccount);
    this.mockMvc.perform(post("/v1/accounts/fund/transfer").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"debitAccountId\": \""+debitAccId+"\",\"creditAccountId\": \""+creditAccId+"\",\"fundToBeTransferred\": 10000.01}"))
            .andExpect(status().isNotAcceptable())
            .andExpect(content().string("Maintain sufficient balance before Fund Transfer"));
  }

  @Test
  void fundTransferTest_No_Overdrafts() throws Exception {
    String debitAccId = "Id-111";
    Account debitAccount = new Account(debitAccId, new BigDecimal("123.45"));
    this.accountsService.createAccount(debitAccount);
    String creditAccId = "Id-222";
    Account creditAccount = new Account(creditAccId, new BigDecimal("223.45"));
    this.accountsService.createAccount(creditAccount);
    this.mockMvc.perform(post("/v1/accounts/fund/transfer").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"debitAccountId\": \""+debitAccId+"\",\"creditAccountId\": \""+creditAccId+"\",\"fundToBeTransferred\": -10000.01}"))
            .andExpect(status().isNotAcceptable())
            .andExpect(content().string("Overdrafts is not supported!"));
  }
}
