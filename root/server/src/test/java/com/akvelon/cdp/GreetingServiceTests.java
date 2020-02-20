package com.akvelon.cdp;

import com.akvelon.cdp.endpoints.GreetingController;
import com.akvelon.cdp.services.greeting.GreetingService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.akvelon.cdp.TestData.EMPTY_STRING;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
class GreetingServiceTests {
	@MockBean
	private GreetingController greetingController;
	@MockBean
	private GreetingService greetingService;

	@Test
	public void testGreetingMethod() {
		val someUserName = "User";
		List.of(EMPTY_STRING, someUserName)
				.forEach(userName ->
					when(greetingController.greeting(userName)).thenReturn(EMPTY_STRING));
		verifyNoInteractions(greetingController);
	}

	@Test
	public void testGreetingService() {
		when(greetingService.sayHello(EMPTY_STRING)).thenReturn(EMPTY_STRING);
	}
}
