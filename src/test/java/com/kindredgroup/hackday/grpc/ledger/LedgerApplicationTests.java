package com.kindredgroup.hackday.grpc.ledger;

import com.kindredgroup.hackday.grpc.ledger.bank.model.AccountRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest
public class LedgerApplicationTests {

	@Autowired
	protected AccountRepository accountRepository;

	@After
	public void cleanUp() {
		accountRepository.deleteAll();
	}

	@Test
	public void contextLoads() {
	}

}
