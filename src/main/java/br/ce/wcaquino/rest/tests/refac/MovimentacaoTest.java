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
		
		System.out.println("\n--------------- Inserir movimenta��o com sucesso ---------------\n");
		
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
		
		System.out.println("\n--------------- Validar campos obrigat�rios ---------------\n");
		
		RestAssured.given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(400)
			.body("$", Matchers.hasSize(8))
			.body("msg", Matchers.hasItems(
					"Data da Movimenta��o � obrigat�rio",
					"Data do pagamento � obrigat�rio",
					"Descri��o � obrigat�rio",
					"Interessado � obrigat�rio",
					"Valor � obrigat�rio",
					"Valor deve ser um n�mero",
					"Conta � obrigat�rio",
					"Situa��o � obrigat�rio"))
		;
	}
	
	
	@Test
	public void naoInserirMovimentacaoComDataFutura() {
		
		System.out.println("\n--------------- N�o inserir movimenta��o com data futura ---------------\n");
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
			.body("msg", Matchers.hasItem("Data da Movimenta��o deve ser menor ou igual � data atual"))
		;
	}
	
	
	@Test
	public void naoRemoverContaComMovimentacao() {
		
		System.out.println("\n--------------- N�o remover conta com movimenta��o ---------------\n");
		
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
		
		System.out.println("\n--------------- Remover movimenta��o ---------------\n");
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