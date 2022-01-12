package br.ce.wcaquino.rest.utils;

import br.ce.wcaquino.rest.tests.Movimentacao;
import io.restassured.RestAssured;

public class MetodosUtils {

	public static Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}

	public static Integer getIdMovPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
	}
	
	public static Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setTipo("REC");
		mov.setData_transacao(DataUtils.getDataComAcrescimoDeDias(-1));
		mov.setData_pagamento(DataUtils.getDataComAcrescimoDeDias(30));
		mov.setDescricao("Movimentação Normal");
		mov.setEnvolvido("Marlyson Clingio Almeida Coutinho");
		mov.setValor(1000f);
		mov.setConta_id(MetodosUtils.getIdContaPeloNome("Conta para movimentacoes"));
		mov.setStatus(true);
		return mov;
	}
}