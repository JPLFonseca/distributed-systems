package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class GestaoGerente {

	
	
	public static List<String> getDesignacoesClubes() {
		
		Manipula dados = new Manipula();
		
		List<String> designacoes = new ArrayList<>();
		
		try {
			String diretiva = "select designacao from Clube";
			ResultSet rs = dados.getResultado(diretiva);
			while (rs != null && rs.next()) {
				designacoes.add(rs.getString("designacao"));
			}
		} catch(SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return designacoes;
	}
	
	public static String[] getContactosClube(String designacao) {
		
		Manipula dados = new Manipula();
		
		String[] contactos = null;
		try {
			String diretiva = "select email, telemovel from Clube where designacao='" + designacao + "'";
			ResultSet rs = dados.getResultado(diretiva);
			if (rs != null && rs.next()) {
				contactos = new String[] {rs.getString("email"), rs.getString("telemovel")};
			}
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return contactos;
	}
	
	public static boolean updateContactosClube(String designacao, String[] contactos) {
		
		Manipula dados = new Manipula();
		String diretiva = "update Clube set email='" + contactos[0] + "', telemovel=" + contactos[1] +
						  " where designacao='" + designacao + "'";
		boolean sucesso = dados.xDirectiva(diretiva);
		dados.desligar();
		return sucesso;
	}
	
	public static String[][] getHorarioClube(String designacao) {
		
		Manipula dados = new Manipula();
		String[][] horario = new String[7][2];
		
		try {
			String diretiva = "select diaSemana, horaAbertura, horaFecho from Horario where NIF=(select NIF from Clube where designacao='"+designacao+"')";
		
			ResultSet rs = dados.getResultado(diretiva);
			int i = 0;
			while(rs != null && rs.next()) {
				String dia = rs.getString("diaSemana");
				String horaAbertura = rs.getString("horaAbertura");
				String horaFecho = rs.getString("horaFecho");
				
				if (dia.equals("Segunda")) i = 0;
				else if (dia.equals("Terca")) i = 1;
				else if (dia.equals("Quarta")) i = 2;
				else if (dia.equals("Quinta")) i = 3;
				else if (dia.equals("Sexta")) i = 4;
				else if (dia.equals("Sabado")) i = 5;
				else if (dia.equals("Domingo")) i = 6;
				
				horario[i][0] = horaAbertura;
				horario[i][1] = horaFecho;
			}
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return horario;
	}
	
	public static boolean updateHorarioClube(String designacao, String diaSemana, String horaAbertura, String horaFecho) {
		
		Manipula dados = new Manipula();
		boolean sucesso = false;
		
		try {

			// verificar se já existe
		
			boolean existe = false;
			String diretiva = "select diaSemana from Horario where NIF=(select NIF from Clube where designacao='" + designacao + "')";
			ResultSet rs = dados.getResultado(diretiva);
			while (rs != null && rs.next()) {
				String diaExistente = rs.getString("diaSemana");
				if (diaExistente.equals(diaSemana)) {
					existe = true;
					break;
				}
			}
			
			// alterar
			
			if (existe)
				diretiva = "update Horario set horaAbertura='"+horaAbertura+"', horaFecho='"+horaFecho+
							"' where diaSemana='"+diaSemana+"' and NIF=(select NIF from Clube where designacao='"+designacao+"')";
			else
				diretiva = "insert into Horario (NIF, diaSemana, horaAbertura, horaFecho) values ("+
							"(select NIF from Clube where designacao='"+designacao+"'), '"+diaSemana+"', '"+horaAbertura+"', '"+horaFecho+"')";
			
			sucesso = dados.xDirectiva(diretiva);
				
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		
		dados.desligar();
		return sucesso;
	}
	
	public static boolean removerHorarioClube(String designacao, String diaSemana) {
		
		Manipula dados = new Manipula();
		String diretiva = "delete from Horario where diaSemana='"+diaSemana+"' and "+
							"NIF=(select NIF from Clube where designacao='"+designacao+"')";
		boolean sucesso = dados.xDirectiva(diretiva);	
		dados.desligar();
		return sucesso;
	}
	
	public static String[][] getEquipamentosMenosUtilizados(String designacao) {
		
		Manipula dados = new Manipula();
		String[][] equipamentos = new String[5][4];
		
		try {
			
			String diretiva = "select numSala, numEquipamento, designacao from Equipamento "+
								"where (NIF, numSala, numEquipamento) not in "+
								"(select NIF, numSala, numEquipamento from Mancha_Equipamento) "+
								"and NIF=(select NIF from Clube where designacao='"+designacao+"') limit 5";
			
			ResultSet rs = dados.getResultado(diretiva);
			int i = 0;
			while(rs != null && rs.next()) {
				equipamentos[i][0] = rs.getString("numSala");
				equipamentos[i][1] = rs.getString("numEquipamento");
				equipamentos[i][2] = rs.getString("designacao");
				equipamentos[i][3] = "00:00";
				i++;
			}
			
			if (i < 4) {
				
				diretiva = "select e.numSala, e.numEquipamento, e.designacao, timediff(me.horaFim, me.horaInicio) as tempo "
						+ "from Equipamento e, Mancha_Equipamento me "
						+ "where e.NIF=(select NIF from Clube where designacao='"+designacao+"') and e.NIF=me.NIF and e.numSala=me.numSala and e.numEquipamento=me.numEquipamento "
						+ "order by tempo asc limit " + (5-i);
				
				rs = dados.getResultado(diretiva);
				while (rs != null && rs.next()) {
					equipamentos[i][0] = rs.getString("numSala");
					equipamentos[i][1] = rs.getString("numEquipamento");
					equipamentos[i][2] = rs.getString("designacao");
					equipamentos[i][3] = rs.getString("tempo");
					i++;
				}
			}
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return equipamentos;
	}
	
	public static InputStream getFotoSala(String designacao, String numSala) {
		
		Manipula dados = new Manipula();
		InputStream streamFoto = null;
		
		try {
			String diretiva = "select rv.recurso from RecursoVisual rv, Sala s "+
								"where s.codImagem=rv.codRecurso "+
								"and s.numSala="+numSala+" and s.NIF=(select NIF from Clube where designacao='"+designacao+"')";
			
			ResultSet rs = dados.getResultado(diretiva);
			if(rs != null && rs.next())
				streamFoto = rs.getBinaryStream("recurso");
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return streamFoto;
	}
	
	public static InputStream getVideoSala(String designacao, String numSala) {
		
		Manipula dados = new Manipula();
		InputStream streamVideo = null;
		
		try {
			String diretiva = "select rv.recurso from RecursoVisual rv, Sala s "+
								"where s.codVideo=rv.codRecurso "+
								"and s.numSala="+numSala+" and s.NIF=(select NIF from Clube where designacao='"+designacao+"')";
			
			ResultSet rs = dados.getResultado(diretiva);
			if(rs != null && rs.next())
				streamVideo = rs.getBinaryStream("recurso");
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return streamVideo;
	}
	
	public static List<String> getNumerosSala(String designacao) {
		
		Manipula dados = new Manipula();
		List<String> numsSala = new ArrayList<>();
		
		try {
			String diretiva = "select numSala from Sala where NIF=(select NIF from Clube where designacao='"+designacao+"')";
			
			ResultSet rs = dados.getResultado(diretiva);
			while (rs != null && rs.next())
				numsSala.add(rs.getString("numSala"));
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return numsSala;
	}
	
	public static boolean updateFotoSala(String designacao, String numSala, InputStream streamFoto, String fotoType) {
		
		Manipula dados = new Manipula();
		boolean sucesso = false;
		
		try {
			String diretiva = "update RecursoVisual set recurso=?, mimeType="+fotoType+" where codRecurso=(select codImagem from Sala where numSala="+
							numSala+" and NIF=(select NIF from Clube where designacao='"+designacao+"'))";
		
			Connection con = dados.getLigacao();
		
			PreparedStatement ps = con.prepareStatement(diretiva);
			ps.setBinaryStream(1, streamFoto);
			
			sucesso = ps.execute();
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return sucesso;
	}

	public static boolean updateVideoSala(String designacao, String numSala, InputStream streamVideo, String videoType) {
		
		Manipula dados = new Manipula();
		boolean sucesso = false;
		
		try {
			String diretiva = "update RecursoVisual set recurso=?, mimeType="+videoType+" where codRecurso=(select codVideo from Sala where numSala="+
							numSala+" and NIF=(select NIF from Clube where designacao='"+designacao+"'))";
		
			Connection con = dados.getLigacao();
		
			PreparedStatement ps = con.prepareStatement(diretiva);
			ps.setBinaryStream(1, streamVideo);
			
			sucesso = ps.execute();
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
		
		dados.desligar();
		return sucesso;
	}
}
