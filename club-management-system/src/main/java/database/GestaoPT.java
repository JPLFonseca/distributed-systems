package database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GestaoPT{
	
	
	//VER CLIENTES INSCRITOS
	public static String[][] consultarManchasEscalao(Manipula dados, String nomeEscalao) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT m.dia, m.horaInicio, m.horaFim, (SELECT nome FROM PT p, Utilizador u WHERE p.numPT=m.numPT AND u.userID=p.userID) AS PT, m.designacao, m.nomeEscalao, m.numInscricoes, m.confirmada FROM Mancha m WHERE m.nomeEscalao='"+nomeEscalao+"'");
		
		ArrayList<String> listaDias = new ArrayList<>();
		ArrayList<String> listaHorasInicio = new ArrayList<>();
		ArrayList<String> listaHorasFim = new ArrayList<>();
		ArrayList<String> listaPTs = new ArrayList<>();
		ArrayList<String> listaDesignacoes = new ArrayList<>();
		ArrayList<String> listaEscaloes = new ArrayList<>();
		ArrayList<String> listaNumInscricoes = new ArrayList<>();
		ArrayList<String> listConfirmacoes = new ArrayList<>();
		
		while (resultado.next()) {
			listaDias.add(resultado.getString("dia"));
			listaHorasInicio.add(resultado.getString("horaInicio"));
			listaHorasFim.add(resultado.getString("horaFim"));
			listaPTs.add(resultado.getString("PT"));
			listaDesignacoes.add(resultado.getString("designacao"));
			listaEscaloes.add(resultado.getString("nomeEscalao"));
			listaNumInscricoes.add(resultado.getString("numInscricoes"));
			listConfirmacoes.add(resultado.getString("confirmada"));
		}
		
		String[][] array = {listaDias.toArray(new String[0]), listaHorasInicio.toArray(new String[0]), listaHorasFim.toArray(new String[0]), listaPTs.toArray(new String[0]), listaDesignacoes.toArray(new String[0]), listaEscaloes.toArray(new String[0]), listaNumInscricoes.toArray(new String[0]), listConfirmacoes.toArray(new String[0])};
		
		return array;
	}
	
	public static String[][] consultarManchasNumPT(Manipula dados, String numPT) throws SQLException {
		
		ResultSet resultado = dados.getResultado("SELECT dia, horaInicio, horaFim, designacao, nomeEscalao, numInscricoes, confirmada FROM Mancha WHERE numPT="+numPT);
		
		ArrayList<String> listaDias = new ArrayList<>();
		ArrayList<String> listaHorasInicio = new ArrayList<>();
		ArrayList<String> listaHorasFim = new ArrayList<>();
		ArrayList<String> listaDesignacoes = new ArrayList<>();
		ArrayList<String> listaEscaloes = new ArrayList<>();
		ArrayList<String> listaNumInscricoes = new ArrayList<>();
		ArrayList<String> listConfirmacoes = new ArrayList<>();
		
		while (resultado.next()) {
			listaDias.add(resultado.getString("dia"));
			listaHorasInicio.add(resultado.getString("horaInicio"));
			listaHorasFim.add(resultado.getString("horaFim"));
			listaDesignacoes.add(resultado.getString("designacao"));
			listaEscaloes.add(resultado.getString("nomeEscalao"));
			listaNumInscricoes.add(resultado.getString("numInscricoes"));
			listConfirmacoes.add(resultado.getString("confirmada"));
		}
		
		String[][] array = {listaDias.toArray(new String[0]), listaHorasInicio.toArray(new String[0]), listaHorasFim.toArray(new String[0]), listaDesignacoes.toArray(new String[0]), listaEscaloes.toArray(new String[0]), listaNumInscricoes.toArray(new String[0]), listConfirmacoes.toArray(new String[0])};
		
		return array;
	}
	
	//USAR EQUIPAMENTO
	public static boolean publicarMancha (Manipula dados, String designacao, String nomeEscalao, String numPT, String dia, String horaInicio, String horaFim) {
	
		boolean publicado = dados.xDirectiva(
				"INSERT INTO Mancha (designacao, nomeEscalao, numPT, dia, horaInicio, horaFim, confirmada, numInscricoes) "
				+ "VALUES ('"+designacao+"', '"+nomeEscalao+"', '"+numPT+"', '"+dia+"', '"+horaInicio+"', '"+horaFim+"', 0, 0)");
		
		return publicado;
	}
	
	//CONFIRMACAO É 0 OU 1
	public static boolean confirmarMancha(Manipula dados, String numPT, String dia, String horaInicio, String horaFim, String confirmacao) {
		
		boolean publicado = dados.xDirectiva(
				"UPDATE Mancha SET confirmada="+confirmacao+
				" WHERE dia='"+dia+"' AND horaInicio='"+horaInicio+"' AND horaFim='"+horaFim+"'");
		
		return publicado;
	}
	
	public static String[][] consultarCliente(Manipula dados, String nome) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT u.nome, u.telemovel, u.email, funcaoIdade(c.dataNascimento) AS idade, o.descricao AS objetivo, p.descricao AS patologia, p.dataInicio AS inicioPatologia, p.dataFim AS fimPatologia, \r\n"
				+ "    r.tipoEquipamento AS equipamento, r.recomendado AS recomendacao, r.dataInicio AS inicioRecomendacao, \r\n"
				+ "    r.dataFim AS fimRecomendacao FROM Utilizador u, Cliente c LEFT JOIN Patologia p ON p.NIF=c.NIF  LEFT JOIN Objetivo o ON o.NIF = c.NIF LEFT JOIN Recomendacao r ON r.NIF=c.NIF WHERE \r\n"
				+ "    c.userID = u.userID AND u.nome='"+nome+"'");
		
		ArrayList<String> listaNome = new ArrayList<>();
		ArrayList<String> listaTelemovel = new ArrayList<>();
		ArrayList<String> listaEmail = new ArrayList<>();
		ArrayList<String> listaIdade = new ArrayList<>();
		ArrayList<String> listaObjetivo = new ArrayList<>();
		ArrayList<String> listaPatologia = new ArrayList<>();
		ArrayList<String> listaInicioPatologia = new ArrayList<>();
		ArrayList<String> listaFimPatologia = new ArrayList<>();
		ArrayList<String> listaEquipamento = new ArrayList<>();
		ArrayList<String> listaRecomendacao = new ArrayList<>();
		ArrayList<String> listaInicioRecomendacao = new ArrayList<>();
		ArrayList<String> listaFimRecomendacao = new ArrayList<>();
		
		while (resultado.next()) {
			listaNome.add(resultado.getString("nome"));
			listaTelemovel.add(resultado.getString("telemovel"));
			listaEmail.add(resultado.getString("email"));
			listaIdade.add(resultado.getString("idade"));
			listaObjetivo.add(resultado.getString("objetivo"));
			listaPatologia.add(resultado.getString("patologia"));
			listaInicioPatologia.add(resultado.getString("inicioPatologia"));
			listaFimPatologia.add(resultado.getString("fimPatologia"));
			listaEquipamento.add(resultado.getString("equipamento"));
			listaRecomendacao.add(resultado.getString("recomendacao"));
			listaInicioRecomendacao.add(resultado.getString("inicioRecomendacao"));
			listaFimRecomendacao.add(resultado.getString("fimRecomendacao"));
		}
		
		String[][] array = {listaNome.toArray(new String[0]), listaTelemovel.toArray(new String[0]), listaEmail.toArray(new String[0]), listaIdade.toArray(new String[0]), listaObjetivo.toArray(new String[0]), listaPatologia.toArray(new String[0]), listaInicioPatologia.toArray(new String[0]), listaFimPatologia.toArray(new String[0]), listaEquipamento.toArray(new String[0]), listaRecomendacao.toArray(new String[0]), listaInicioRecomendacao.toArray(new String[0]), listaFimRecomendacao.toArray(new String[0])};
		
		return array;
	}
	
	public static String[][] consultarPatObj(Manipula dados, String nome) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT o.descricao AS objetivo, p.descricao AS patologia, p.dataInicio, p.dataFim FROM Objetivo o, Patologia p WHERE o.NIF=(SELECT NIF FROM Cliente c, Utilizador u WHERE u.nome="+nome+" AND u.userID=c.userID) AND p.NIF=(SELECT NIF FROM Cliente c, Utilizador u WHERE u.nome="+nome+" AND u.userID=c.userID)");
		
		ArrayList<String> listaNome = new ArrayList<>();
		ArrayList<String> listaTelemovel = new ArrayList<>();
		ArrayList<String> listaEmail = new ArrayList<>();
		ArrayList<String> listaDataNascimento = new ArrayList<>();
		
		while (resultado.next()) {
			listaNome.add(resultado.getString("nome"));
			listaTelemovel.add(resultado.getString("telemovel"));
			listaEmail.add(resultado.getString("email"));
			listaDataNascimento.add(resultado.getString("dataNascimento"));
		}
		
		String[][] array = {listaNome.toArray(new String[0]), listaTelemovel.toArray(new String[0]), listaEmail.toArray(new String[0]), listaDataNascimento.toArray(new String[0])};
		
		return array;
	}
	
	
	
	public static String[] receberNomesClientes(Manipula dados, String numPT) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT nome FROM Cliente c, Utilizador u WHERE c.userID = u.userID");
		
		ArrayList<String> listaNome = new ArrayList<>();
		
		while (resultado.next()) {
			listaNome.add(resultado.getString("nome"));
		}
		
		return listaNome.toArray(new String[0]);
	}
	
	public static String[] receberNumPT(Manipula dados, String userID) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT numPT FROM PT WHERE userID='"+userID+"'");
		
		ArrayList<String> numPT = new ArrayList<>();
		
		while (resultado.next()) {
			numPT.add(resultado.getString("numPT"));
		}
		
		return numPT.toArray(new String[0]);
	}
	
	public static String[] receberNomesPropriosClientes(Manipula dados, String numPT) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT nome FROM PT_Cliente pc, Cliente c, Utilizador u WHERE pc.numPT ="+numPT+" AND pc.NIF = c.NIF AND c.userID = u.userID");
		
		ArrayList<String> listaNome = new ArrayList<>();
		
		while (resultado.next()) {
			listaNome.add(resultado.getString("nome"));
		}
		
		return listaNome.toArray(new String[0]);
	}
	
	public static boolean agendarCliente(Manipula dados, String nome, String numPT, String dia, String horaInicio, String horaFim) {
		
		boolean publicado = dados.xDirectiva(
				"INSERT INTO Cliente_Mancha(NIF, numPT, dia, horaInicio, horaFim) VALUES"
				+ "((SELECT NIF FROM Cliente c, Utilizador u WHERE u.nome='"+nome+"' AND u.userID=c.userID), "+numPT+", '"+dia+"', '"+horaInicio+"', '"+horaFim+"')");
		
		return publicado;
	}
	
	//VER EQUIPAMENTOS
	//CONSULTAR PATOLOGIAS
	public static boolean recomendarEquipamentos(Manipula dados, String nomeEquipamento, String nomeCliente, String numPT, String dataInicio, String dataFim, String recomendado) {
		
		boolean publicado = dados.xDirectiva(
				"INSERT INTO Recomendacao(tipoEquipamento, NIF, numPT, dataInicio, dataFim, recomendado) VALUES"
				+ "('"+nomeEquipamento+"', (SELECT NIF FROM Cliente c, Utilizador u WHERE u.nome='"+nomeCliente+"' AND u.userID=c.userID), "+numPT+", '"+dataInicio+"', '"+dataFim+"', "+recomendado+")");
		
		return publicado;
	}
	
	public static String[] receberEscaloes(Manipula dados) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT nomeEscalao FROM Escalao");
		
		ArrayList<String> listaEscaloes = new ArrayList<>();
		
		while (resultado.next()) {
			listaEscaloes.add(resultado.getString("nomeEscalao"));
		}
		
		return listaEscaloes.toArray(new String[0]);
	}
	
	public static String[] receberAtividades(Manipula dados) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT designacao FROM Atividade");
		
		ArrayList<String> listaEscaloes = new ArrayList<>();
		
		while (resultado.next()) {
			listaEscaloes.add(resultado.getString("designacao"));
		}
		
		return listaEscaloes.toArray(new String[0]);
	}
	
	public static String[] receberEquipamentos(Manipula dados) throws SQLException {
		
		ResultSet resultado = dados.getResultado(
				"SELECT designacao FROM Equipamento");
		
		ArrayList<String> listaEscaloes = new ArrayList<>();
		
		while (resultado.next()) {
			listaEscaloes.add(resultado.getString("designacao"));
		}
		
		return listaEscaloes.toArray(new String[0]);
	}

}