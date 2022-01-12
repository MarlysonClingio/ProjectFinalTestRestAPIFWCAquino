package br.ce.wcaquino.rest.tests.refac;
import org.hamcrest.Matchers;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest{
	
	@Test
	public void naoAcessarContaSemToken() {
		
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		System.out.println("\n--------------- Não acessar conta sem token ---------------\n");
		
		RestAssured.given()
		.when()
			.get("/contas")
		.then()
			.log().all()
			.statusCode(401)
			.body(Matchers.is("Unauthorized"))
		;
	}
}