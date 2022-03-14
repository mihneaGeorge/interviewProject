package com.interviewproject.persist;

import com.interviewproject.PersistApplication;
import com.interviewproject.model.Transaction;
import com.interviewproject.util.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.integration.json.SimpleJsonSerializer.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = PersistApplication.class
)
@AutoConfigureMockMvc

class PersistApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void givenTransaction_whenSavingTransaction_thenStatus201() throws Exception {
		Transaction mockTransaction = generateMockTransaction();
		mvc.perform(post("/persist/saveTransaction")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(mockTransaction)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.cnp", is(mockTransaction.getCnp())))
				.andExpect(jsonPath("$.iban", is(mockTransaction.getIban())));
	}

	@Test
	public void givenCnp_whenGenerateReport_thenStatus200() throws Exception {
		String cnp = "1111";
		mvc.perform(get("/report/" + cnp )
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content()
				.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.cnp", is(cnp)));
	}

	public Transaction generateMockTransaction() {
		Transaction mockTransaction = new Transaction();
		mockTransaction.setTransactionType(TransactionType.IBAN_TO_IBAN);
		mockTransaction.setIban("1234567");
		mockTransaction.setName("ionut");
		mockTransaction.setCnp("12345");
		mockTransaction.setDescription("description");
		mockTransaction.setSum(10.2);

		return mockTransaction;
	}
}
