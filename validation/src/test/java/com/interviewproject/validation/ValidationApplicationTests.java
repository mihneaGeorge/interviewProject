package com.interviewproject.validation;

import com.interviewproject.model.Transaction;
import com.interviewproject.service.ValidationService;
import com.interviewproject.util.TransactionType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import javax.validation.ValidationException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ValidationApplicationTests {

	@Autowired
	private ValidationService validationService;

	@Test
	public void testMissingIban() {
		Transaction mockTransaction = new Transaction();
		mockTransaction.setTransactionType(TransactionType.IBAN_TO_IBAN);
		mockTransaction.setName("ionut");
		mockTransaction.setCnp("12345");
		mockTransaction.setDescription("description");
		mockTransaction.setSum(10.2);

		ValidationException validationException = assertThrows(
				ValidationException.class,
				() -> validationService.validateTransaction(mockTransaction)
		);

		String expectedMessage = "IBAN cannot be null!";
		String actualMessage = validationException.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

}
