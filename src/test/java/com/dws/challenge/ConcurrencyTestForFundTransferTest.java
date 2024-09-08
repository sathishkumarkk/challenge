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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

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

    private void transferFund(String randomDebitAccId, String randomCreditAccId) {
        FundTransfer fundTransfer = new FundTransfer();
        fundTransfer.setFundToBeTransferred(BigDecimal.valueOf(10));
        fundTransfer.setDebitAccountId(randomDebitAccId);
        fundTransfer.setCreditAccountId(randomCreditAccId);
        accountsService.transferFund(fundTransfer);
        assertThat(this.accountsService.getAccount(randomDebitAccId).getBalance()).isEqualTo(BigDecimal.valueOf(990));
        assertThat(this.accountsService.getAccount(randomCreditAccId).getBalance()).isEqualTo(BigDecimal.valueOf(1010));
    }

//    @ParameterizedTest
//    @CsvFileSource(
//            files = "src/test/resources/csv-file-source.csv",
//            numLinesToSkip = 1)
//    public void testMethodTransferFund1(int debitAcc, int creditAcc) throws Exception {
//        String debitAccId = "Id-"+debitAcc; //+ RandomGenerator.getDefault().nextInt(1, 50);
//        String creditAccId = "Id-"+creditAcc;// + RandomGenerator.getDefault().nextInt(51, 100);
//        transferFund(debitAccId, creditAccId);
//    }
@Test
public void testMethodTransferFund1() throws Exception {
        String debitAccId = "Id-1"; //+ RandomGenerator.getDefault().nextInt(1, 50);
        String creditAccId = "Id-100";// + RandomGenerator.getDefault().nextInt(51, 100);
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund2() throws Exception {
        String debitAccId = "Id-2";
        String creditAccId = "Id-99";
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund3() throws Exception {
        String debitAccId = "Id-3";
        String creditAccId = "Id-98";
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund4() throws Exception {
        String debitAccId = "Id-4";
        String creditAccId = "Id-97";
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund5() throws Exception {
        String debitAccId = "Id-5";
        String creditAccId = "Id-96";
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund6() throws Exception {
        String debitAccId = "Id-6";
        String creditAccId = "Id-95";
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund7() throws Exception {
        String debitAccId = "Id-7";
        String creditAccId = "Id-94";
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund8() throws Exception {
        String debitAccId = "Id-8";
        String creditAccId = "Id-93";
        transferFund(debitAccId, creditAccId);
    }

@Test
    public void testMethodTransferFund9() throws Exception {
        String debitAccId = "Id-9";
        String creditAccId = "Id-92";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund10() throws Exception {
        String debitAccId = "Id-10";
        String creditAccId = "Id-91";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund11() throws Exception {
        String debitAccId = "Id-11";
        String creditAccId = "Id-90";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund12() throws Exception {
        String debitAccId = "Id-12";
        String creditAccId = "Id-89";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund13() throws Exception {
        String debitAccId = "Id-13";
        String creditAccId = "Id-88";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund14() throws Exception {
        String debitAccId = "Id-14";
        String creditAccId = "Id-87";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund15() throws Exception {
        String debitAccId = "Id-15";
        String creditAccId = "Id-86";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund16() throws Exception {
        String debitAccId = "Id-16";
        String creditAccId = "Id-85";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund17() throws Exception {
        String debitAccId = "Id-17";
        String creditAccId = "Id-84";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund18() throws Exception {
        String debitAccId = "Id-18";
        String creditAccId = "Id-83";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund19() throws Exception {
        String debitAccId = "Id-19";
        String creditAccId = "Id-82";
        transferFund(debitAccId, creditAccId);
    }

    @Test
    public void testMethodTransferFund20() throws Exception {
        String debitAccId = "Id-20";
        String creditAccId = "Id-81";
        transferFund(debitAccId, creditAccId);
    }


}
