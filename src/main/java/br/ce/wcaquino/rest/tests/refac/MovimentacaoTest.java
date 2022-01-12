package br.ce.wcaquino.rest.tests.refac;
import org.hamcrest.Matchers;
import org.junit.Test;
import br.ce.wcaquino.rest.tests.Movimentacao;
import br.ce.wcaquino.rest.utils.DataUtils;
import br.ce.wcaquino.rest.utils.MetodosUtils;
import io.restassured.RestAssured;

public class MovimentacaoTest{
	
	@Test
	public void inserirMovimentacaoComSucesso() {
		
		System.out.println("\n--------------- Inserir movimentação com sucesso ---------------\n");
		
		Movimentacao mov = MetodosUtils.getMovimentacaoValida();
		
		RestAssured.given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(201)
		;
	}

	
	@Test
	public void validarCamposObrigatorios() {
		
		System.out.println("\n--------------- Validar campos obrigatórios ---------------\n");
		
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
	}
	
	
	@Test
	public void naoInserirMovimentacaoComDataFutura() {
		
		System.out.println("\n--------------- Não inserir movimentação com data futura ---------------\n");
		Movimentacao mov = MetodosUtils.getMovimentacaoValida();
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
	}
	
	
	@Test
	public void naoRemoverContaComMovimentacao() {
		
		System.out.println("\n--------------- Não remover conta com movimentação ---------------\n");
		
		Integer Conta_ID = MetodosUtils.getIdContaPeloNome("Conta com movimentacao");
		
		RestAssured.given()
			.pathParam("id", Conta_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.log().all()
			.statusCode(500)
			.body("constraint", Matchers.is("transacoes_conta_id_foreign"))
		;
	}
	
	
	@Test
	public void removerMovimentacao() {
		
		System.out.println("\n--------------- Remover movimentação ---------------\n");
		Integer Mov_ID = MetodosUtils.getIdMovPelaDescricao("Movimentacao para exclusao");
		
		RestAssured.given()
		.when()
			.delete("/transacoes/" + Mov_ID)
		.then()
			.log().all()
			.statusCode(204)
		;
	}
}