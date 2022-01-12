package br.ce.wcaquino.rest.tests.refac;
import org.hamcrest.Matchers;
import org.junit.Test;
import br.ce.wcaquino.rest.utils.MetodosUtils;
import io.restassured.RestAssured;

public class ContasTest{
	
	@Test
	public void incluirContaComSucesso() {
		
		System.out.println("\n--------------- Incluir conta com sucesso ---------------\n");
		
		RestAssured.given()
			.body("{ \"nome\": \"Conta inserida\" }")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(201)
			.body("usuario_id", Matchers.is(27185))
		;
	}
	
	
	@Test
	public void alterarContaComSucesso() {
		
		System.out.println("\n--------------- Alterar conta com sucesso ---------------\n");
		
		Integer Conta_ID = MetodosUtils.getIdContaPeloNome("Conta para alterar");
		
		RestAssured.given()
			.body("{ \"nome\": \"Conta alterada\" }")
			.pathParam("id", Conta_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.body("usuario_id", Matchers.is(27185))
			.body("nome", Matchers.is("Conta alterada"))
		;
	}

	
	@Test
	public void naoInserirContaComMesmoNome() {
		
		System.out.println("\n--------------- Não inserir conta com mesmo nome ---------------\n");
		
		RestAssured.given()
			.body("{ \"nome\": \"Conta mesmo nome\" }")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"))
		;
	}
}