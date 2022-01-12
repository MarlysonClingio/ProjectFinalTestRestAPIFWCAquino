package br.ce.wcaquino.rest.tests.refac;
import org.junit.Test;
import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;

public class SaldoTest extends BaseTest{
	
	@Test
	public void mostrarContaComSaldo() {
		
		System.out.println("\n--------------- Mostrar contas com saldo ---------------\n");
		
		RestAssured.given()
		.when()
			.get("/saldo")
		.then()
			.log().all()
			.statusCode(200)
		;
	}
}