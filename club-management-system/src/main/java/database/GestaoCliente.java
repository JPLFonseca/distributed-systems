package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GestaoCliente {
	
	
	/**
	 * 
	 * int com o NIF ,receber um array que tem uma ordem [[email, telemovel],[descricao,dataInicio,dataFim],Objetivo[descricao]]
	 * 
	 * tenho de colocar as verificacoes dos dados no JSP
	 * Talvez seja melhor fazer a verificacao aqui
	 */
	public static boolean atualizarPerfil(Manipula dados,String userID,String[][] new_data) {
		
		boolean a = false;
		boolean b = false;
		boolean c = false;
		boolean d = false;
		
		// atualiza o email
		if(new_data[0][0] != null) {
			String comando = "update Utilizador set email='" + new_data[0][0] + "' where userID=UUID_TO_BIN(" + userID + ");";
			a = dados.xDirectiva(comando);
		} else {
			a = true;
		}
		
		// atualiza o numero de telemovel
		if(new_data[0][1] != null) {
			String comando = "update Utilizador set telemovel=" + new_data[0][1] + " where userID=UUID_TO_BIN(" + userID + ");";
			b = dados.xDirectiva(comando);
		} else {
			b = true;
		}
		
		// atualiza a patologia
		if(new_data[0][2] != null) {
			String comando = "update Patologia set descricao='" + new_data[1][0] +"', dataInicio="+new_data[1][1]+",dataFim="
					+ new_data[1][2] + " where NIF=(SELECT NIF from Cliente where userID=UUID_TO_BIN(" + userID + ");";
			c = dados.xDirectiva(comando);
		} else {
			c = true;
		}
		
		// atualiza o objetivo
		if(new_data[0][3] != null) {
			String comando = "update Objetivo set descricao='" + new_data[1][0] +" where NIF=UUID_TO_BIN(" + userID + ");";
			d = dados.xDirectiva(comando);
		} else {
			d = true;
		}
		
		
		
		return (a && b && c && d); // verifica se foi feito
	}
	
	/**
	 * 
	 * 
	 * Tem de ser atividades do seu PT
	 * 
	 * da tabela PT_Cliente, ir ver qual é o PT do cliente e colocalo na tabela mancha_cliente
	 */
	public static String[][] mostraAtividadeIndividual(Manipula dados,String userID) throws SQLException {
		
		
		
		// mostrar atividades do PT que tem um máximo de 1 participante e não têm ninguém inscrito
		ResultSet atividades_PT = dados.getResultado(
				"SELECT dia, horaInicio, horaFim, designacao, nomeEscalao, numInscricoes, confirmada FROM Mancha m WHERE m.numPT =(SELECT numPT from PT_Cliente where NIF="
				+ "(SELECT NIF from Cliente where userID=UUID_TO_BIN('"+userID+"'))) AND m.numInscricoes=0 AND designacao.maxParticipantes=1");
		
		ArrayList<String> atiDesignacao = new ArrayList<>();
		ArrayList<String> atiDia = new ArrayList<>();
		ArrayList<String> atiHoraInicio = new ArrayList<>();
		ArrayList<String> atiHoraFim = new ArrayList<>();
		ArrayList<String> atiNomeEscalao = new ArrayList<>();
		ArrayList<String> atiNumInsc = new ArrayList<>();
		ArrayList<String> atiConf = new ArrayList<>();
		
		while (atividades_PT.next()) {
			atiDesignacao.add(atividades_PT.getString("designacao"));
			atiDia.add(atividades_PT.getString("dia"));
			atiHoraInicio.add(atividades_PT.getString("horaInicio"));
			atiHoraFim.add(atividades_PT.getString("horaFim"));
			atiNomeEscalao.add(atividades_PT.getString("nomeEscalao"));
			atiNumInsc.add(atividades_PT.getString("numInscricoes"));
			atiConf.add(atividades_PT.getString("confirmada"));
}
		
		
		String[][] array = {atiDia.toArray(new String[0]), atiHoraInicio.toArray(new String[0]), atiHoraFim.toArray(new String[0]),atiDesignacao.toArray(new String[0]),atiNomeEscalao.toArray(new String[0]),atiNumInsc.toArray(new String[0]),atiConf.toArray(new String[0])};
		
		// retorna um array com atividades individuais, do PT da pessoa, contendo o nome, dia, hora de inicio e fim
		return array;
	}
	
	public static boolean subAtividadeIndividual(Manipula dados, String userID, String atividade,String dia, String horaInicio, String horaFim) {
		
		boolean inscrito = dados.xDirectiva("UPDATE Cliente_Mancha m set NIF=(SELECT NIF from Cliente where userID=UUID_TO_BIN('"+userID+"')) WHERE m.numPT=(SELECT numPT from PT_Cliente where NIF=(SELECT NIF from Cliente where userID=UUID_TO_BIN('"+userID+"')))"
				+ "AND dia='"+ dia+"' AND Atividade='"+ atividade +"' AND horaInicio='"+horaInicio+"' and horaFim='"+horaFim+"'"); 
		
		return inscrito;
	}
	
public static boolean subAtividadeGrupo(Manipula dados, String userID, String atividade,String dia, String horaInicio, String horaFim) {
		
		boolean inscrito = dados.xDirectiva("UPDATE Cliente_Mancha set NIF=(SELECT NIF from Cliente where userID=UUID_TO_BIN('"+userID+"')) WHERE dia='"+ dia+"' AND Atividade='"+ atividade +"' AND horaInicio='"+horaInicio+"' and horaFim='"+horaFim+"'"); 
		
		return inscrito;
	}
	
	
	
	/**
	 * 
	 * 
	 * Adere a atividades de grupo de qualquer PT
	 * 
	 * 
	 */
	public static String[][] mostrarAtividadesGrupo(Manipula dados) throws SQLException{
		
		
		
		// mostrar atividades do PT que tem um máximo de 1 participante e não têm ninguém inscrito
		ResultSet atividades_PT = dados.getResultado(
				"SELECT dia, horaInicio, horaFim, designacao as d, nomeEscalao, numInscricoes, confirmada FROM Mancha m WHERE m.numInscricoes< d.maxParticipantes AND d.maxParticipantes>1") ;
		
		ArrayList<String> atiDesignacao = new ArrayList<>();
		ArrayList<String> atiDia = new ArrayList<>();
		ArrayList<String> atiHoraInicio = new ArrayList<>();
		ArrayList<String> atiHoraFim = new ArrayList<>();
		ArrayList<String> atiNomeEscalao = new ArrayList<>();
		ArrayList<String> atiNumInsc = new ArrayList<>();
		ArrayList<String> atiConf = new ArrayList<>();
		
		while (atividades_PT.next()) {
			atiDesignacao.add(atividades_PT.getString("designacao"));
			atiDia.add(atividades_PT.getString("dia"));
			atiHoraInicio.add(atividades_PT.getString("horaInicio"));
			atiHoraFim.add(atividades_PT.getString("horaFim"));
			atiNomeEscalao.add(atividades_PT.getString("nomeEscalao"));
			atiNumInsc.add(atividades_PT.getString("numInscricoes"));
			atiConf.add(atividades_PT.getString("confirmada"));}
		
		
		String[][] array = {atiDia.toArray(new String[0]), atiHoraInicio.toArray(new String[0]), atiHoraFim.toArray(new String[0]),atiDesignacao.toArray(new String[0]),atiNomeEscalao.toArray(new String[0]),atiNumInsc.toArray(new String[0]),atiConf.toArray(new String[0])};
		
		
		// retorna um array com atividades contendo o nome, dia, hora de inicio e fim
		return array;
	}
	
	
	
	/**
	 * 
	 * 
	 * Obtém calendário semanal das atividades
	 * 
	 * Alterar tipo de dados retornado
	 */
	public static String[][] getAtividadesSemanal(Manipula dados, String dia)throws SQLException {
		
	    String mostrar_atividades = "SELECT designacao,dia,horaInicio,horaFim FROM Mancha m WHERE m.dia BETWEEN '" + dia + "' AND DATE_ADD('" + dia + "', INTERVAL 6 DAY)";
	    ResultSet mAtividades = dados.getResultado(mostrar_atividades);
	    
	    
	    ArrayList<String> atividades = new ArrayList<>();
	    ArrayList<String> dias = new ArrayList<>();
	    ArrayList<String> horaInicio = new ArrayList<>();
	    ArrayList<String> horaFim = new ArrayList<>();
	    
	    while (mAtividades.next()) {
	    	atividades.add(mAtividades.getString("designacao"));
	    	dias.add(mAtividades.getString("dia"));
	    	horaInicio.add(mAtividades.getString("horaInicio"));
	    	horaFim.add(mAtividades.getString("horaFim"));
	    }
	    
	    String[][] array = {atividades.toArray(new String[0]),dias.toArray(new String[0]),horaInicio.toArray(new String[0]),horaFim.toArray(new String[0])};
	    
	    return array;
	}

	
	/**
	 * 
	 * 
	 * Consulta recomendacoes do PT
	 * 
	 * Alterar tipo de dados retornado
	 */
	public static String[][] consultaRecomendacoes(Manipula dados,String userID,String data) throws SQLException{
		
		
		
		// vai buscar as recomendacoes em vigor para aquele cliente
		String recom = "SELECT tipoEquipamento FROM Recomedacao r WHERE dataInicio<='"+ data +"' AND dataFim>='"+data
				+ "' AND r.NIF=(SELECT NIF from Cliente where userID=UUID_TO_BIN('" + userID + "')) AND r.numPT=(SELECT PT from PT_Cliente where "
						+ "NIF=(SELECT NIF from Cliente where userID=UUID_TO_BIN('" + userID +"')));";
		
		ResultSet recomendacoes = dados.getResultado(recom);
		
		ArrayList<String> recomend = new ArrayList<>();
		
		while(recomendacoes.next()) {
			recomend.add(recomendacoes.getString("tipoEquipamento"));
		}
		
		String[][] array = {recomend.toArray(new String[0])};
		
		
		return array;
	}
	
	
	
	public static void main(String[] args) throws SQLException {
			
			/**
			Configura cfg = new Configura();
			Manipula dados = new Manipula(cfg);
			
			dados.xDirectiva("INSERT INTO Escalao (nomeEscalao, idadeMin, idadeMax) VALUES ('Crianca', 2, 10)");
			ResultSet tabelaPTs = dados.getResultado("SELECT * FROM PT");
			String data="";
			try {
				while (tabelaPTs!=null && tabelaPTs.next()) 
					data+=tabelaPTs.getString("numPT").replaceAll("'","&#39;");
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println(e.toString());
			} 
			System.out.println(data);
			dados.getTabelas("");
			**/
			
			Manipula dados = new Manipula(new Configura());
			
			/*
			String[][] resultado = consultarManchasEscalao(dados, "Jovem");
			System.out.println(resultado[3][0]);*/ 
			
			dados.desligar();
		}
}
