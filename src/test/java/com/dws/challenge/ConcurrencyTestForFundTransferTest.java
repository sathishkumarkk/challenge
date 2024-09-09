package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.FundTransfer;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConcurrencyTestForFundTransferTest {
    @Autowired
    private AccountsService accountsService;

    @BeforeAll
    public void init() {
        IntStream.rangeClosed(1, 100).forEachOrdered(id -> {
            Account account = new Account("Id-" + id);
            account.setBalance(new BigDecimal(1000));
            this.accountsService.createAccount(account);
        });
    }

    private void transferFund(String randomDebitAccId, String randomCreditAccId, Long expectedDebitAccBal, Long expectedCreditAccBal) {
        FundTransfer fundTransfer = new FundTransfer();
        fundTransfer.setFundToBeTransferred(BigDecimal.valueOf(10));
        fundTransfer.setDebitAccountId(randomDebitAccId);
        fundTransfer.setCreditAccountId(randomCreditAccId);
        accountsService.transferFund(fundTransfer);
//        assertThat(this.accountsService.getAccount(randomDebitAccId).getBalance()).isEqualTo(BigDecimal.valueOf(expectedDebitAccBal));
//        assertThat(this.accountsService.getAccount(randomCreditAccId).getBalance()).isEqualTo(BigDecimal.valueOf(expectedCreditAccBal));
    }

    @ParameterizedTest
    @CsvFileSource(
            files = "src/test/resources/csv-file-source.csv",
            numLinesToSkip = 1)
    public void testMethodTransferFund1(int debitAcc, int creditAcc) throws Exception {
        String debitAccId = "Id-"+debitAcc;
        String creditAccId = "Id-"+creditAcc;
        transferFund(debitAccId, creditAccId,1l,1l);
    }

    @Test
    public void testMethodTransferFundToId100() throws Exception {
        String debitAccId = "Id-1"; //+ RandomGenerator.getDefault().nextInt(1, 50);
        String creditAccId = "Id-100";// + RandomGenerator.getDefault().nextInt(51, 100);
        transferFund(debitAccId, creditAccId, 990L, 1010L);
    }

    @Test
    public void testMethodTransferFundToId99() throws Exception {
        String debitAccId = "Id-1";
        String creditAccId = "Id-99";
        transferFund(debitAccId, creditAccId, 980L, 1010L);
    }

}
