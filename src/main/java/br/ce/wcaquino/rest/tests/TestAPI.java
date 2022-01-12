package br.ce.wcaquino.rest.tests;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class TestAPI extends BaseTest{
	
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
	}
	
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
	
	
	@Test
	public void validarProcedimentosDaConta() {
		
		System.out.println("\n--------------- Incluir conta com sucesso ---------------\n");
				
		Integer ID = RestAssured.given()
			.body("{ \"nome\": \"MARLYSON CLINGIO ALMEIDA COUTINHO\" }")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(201)
			.body("usuario_id", Matchers.is(27185))
			.extract().path("id")
		;
		
		System.out.println("\n--------------- Alterar conta com sucesso ---------------\n");
		
		RestAssured.given()
			.body("{ \"nome\": \"MARLYSON CLINGIO ALMEIDA COUTINHO " + ID + "\" }")
		.when()
			.put("/contas/" + ID)
		.then()
			.log().all()
			.statusCode(200)
			.body("usuario_id", Matchers.is(27185))
		;
		
		System.out.println("\n--------------- Não inserir conta com mesmo nome ---------------\n");
		
		RestAssured.given()
			.body("{ \"nome\": \"MARLYSON CLINGIO ALMEIDA COUTINHO " + ID + "\" }")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"))
		;
		
		System.out.println("\n--------------- Inserir movimentação com sucesso ---------------\n");
		
		Movimentacao mov = new Movimentacao();
		mov.setTipo("REC");
		mov.setData_transacao(DataUtils.getDataComAcrescimoDeDias(-1));
		mov.setData_pagamento(DataUtils.getDataComAcrescimoDeDias(30));
		mov.setDescricao("Movimentação Normal");
		mov.setEnvolvido("MARLYSON CLINGIO ALMEIDA COUTINHO");
		mov.setValor(10000f);
		mov.setConta_id(ID);
		mov.setStatus(true);
		
		Integer IdTransacao = RestAssured.given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(201)
			.extract().path("id")
		;
		
		
		System.out.println("\n--------------- Validar campos obrigatórios da movimentação ---------------\n");
		
		RestAssured.given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(400)
			.body("$", Matchers.hasSize(8))
			.body("msg", Matchers.hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"))
		;
		
		
		System.out.println("\n--------------- Não inserir movimentação com data futura ---------------\n");
		
		mov.setData_transacao(DataUtils.getDataComAcrescimoDeDias(30));
		
		RestAssured.given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(400)
			.body("$", Matchers.hasSize(1))
			.body("msg", Matchers.hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
		
		
		System.out.println("\n--------------- Não remover conta com movimentação ---------------\n");
		
		RestAssured.given()
		.when()
			.delete("/contas/" + ID)
		.then()
			.log().all()
			.statusCode(500)
			.body("constraint", Matchers.is("transacoes_conta_id_foreign"))
		;
		
		
		System.out.println("\n--------------- Mostrar contas com saldo ---------------\n");
		
		RestAssured.given()
		.when()
			.get("/saldo")
		.then()
			.log().all()
			.statusCode(200)
		;
		
		
		System.out.println("\n--------------- Remover movimentação ---------------\n");
		
		RestAssured.given()
		.when()
			.delete("/transacoes/" + IdTransacao)
		.then()
			.log().all()
			.statusCode(204)
		;
	}
}