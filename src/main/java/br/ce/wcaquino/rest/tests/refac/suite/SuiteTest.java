package br.ce.wcaquino.rest.tests.refac.suite;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.refac.AuthTest;
import br.ce.wcaquino.rest.tests.refac.ContasTest;
import br.ce.wcaquino.rest.tests.refac.MovimentacaoTest;
import br.ce.wcaquino.rest.tests.refac.SaldoTest;
import io.restassured.RestAssured;

@RunWith(Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AuthTest.class
	})
public class SuiteTest extends BaseTest{
	
	@BeforeClass
	public static void login() {
		
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "marlysonalmeida@gmail.com");
		login.put("senha", "Resident 5");
		
		String TOKEN = RestAssured.given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.body("id", Matchers.is(27185))
			.extract().path("token");
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		RestAssured.get("/reset").then().statusCode(200);
	}
}