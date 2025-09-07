package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import database.*;

@WebServlet("/ServletPT")
public class ServletPT extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String comando = request.getParameter("comando");
        String escalao = request.getParameter("escalao");
        String numPT = request.getParameter("numPT");
        String nomeCliente = request.getParameter("nomeCliente");
        String atividade = request.getParameter("atividade");
        String diaMancha = request.getParameter("diaMancha");
        String horaInicio = request.getParameter("horaInicio");
        String horaFim = request.getParameter("horaFim");
        String confirmacao = request.getParameter("confirmacao");
        String inicioRecomendacao = request.getParameter("inicioRecomendacao");
        String fimRecomendacao = request.getParameter("fimRecomendacao");
        String equipamento = request.getParameter("equipamento");
        String recomendacao = request.getParameter("recomendacao");
        	
        String info = "";
        

        Manipula dados = new Manipula(new Configura());
        
        if(comando.equals("Z")) { //Nomes dos clientes para autocomplete
        	
        	String[] nomesClientes = null;
        	try {nomesClientes = GestaoPT.receberNomesPropriosClientes(dados, numPT);} catch (SQLException e) {e.printStackTrace();}
        	
        	for(int idx=0; idx<nomesClientes.length; idx++) {
        		info+=nomesClientes[idx];
        		if(idx!=nomesClientes.length-1) {
        			info+="|";
        		}
        	}
        }
        
        else if(comando.equals("W")) { //Nomes dos equipamentos
        	
        	String[] nomesEquipamentos = null;
        	try {nomesEquipamentos = GestaoPT.receberEquipamentos(dados);} catch (SQLException e) {e.printStackTrace();}
        	
        	for(int idx=0; idx<nomesEquipamentos.length; idx++) {
        		info+=nomesEquipamentos[idx];
        		if(idx!=nomesEquipamentos.length-1) {
        			info+="|";
        		}
        	}
        }
        
        else if(comando.equals("E")) {//Ficha do cliente
        	
        	String[][] fichaCliente = null;
			try {fichaCliente = GestaoPT.consultarCliente(dados, nomeCliente);} catch (SQLException e) {e.printStackTrace();}
    		
			info="<h1>Ficha do cliente escolhido</h1><br><table><tr><th>Nome</th><th>Telemóvel</th><th>Email</th><th>Idade</th><th>Objetivo</th><th>Patologia</th><th>Início da patologia</th><th>Fim da patologia</th><th>Equipamento recomendado</th><th>Recomendação</th><th>Início da recomendação</th><th>Fim da recomendação</th></tr>";

    		for(int coluna=0; coluna<fichaCliente[0].length; coluna++){ 
    			info+="<tr>";
    			for(int linha=0; linha<fichaCliente.length; linha++){
    				String dado = fichaCliente[linha][coluna];
    				System.out.println(dado);
    				if(dado == null) {
    					info+="<td>-</td>";
    				}
    				else if (dado.equals("")) {
    					info+="<td>-</td>";
    				}
    				else if (dado.equals("0")) {
    					info+="<td>Desaconselhado</td>";
    				}
    				else if (dado.equals("1")) {
    					info+="<td>Aconselhado</td>";
    				}
    				else {
    					info+="<td>"+fichaCliente[linha][coluna]+"</td>";
    				}
    			}
    			info+="</tr>";
    		}
    		info+="</table>";
        }
        
        else if(comando.equals("D")) { //Confirmar manchas
        	
        	boolean confirmarMancha = GestaoPT.confirmarMancha(dados, numPT, diaMancha, horaInicio, horaFim, confirmacao);
        	
        	if(confirmarMancha == true) {
        		info+="<h1>Mancha atualizada</h1>";
        	}
        	else {
        		info+="<h1>Erro na atualização da mancha</h1>";
        	}
        	
        }
        
        else if(comando.equals("F")) { //Inscrever cliente na mancha
        	
        	boolean inscreverCliente = GestaoPT.agendarCliente(dados, nomeCliente, numPT, diaMancha, horaInicio, horaFim);
        	
        	if(inscreverCliente == true) {
        		info+="<h1>Cliente inscrito na mancha</h1>";
        	}
        	else {
        		info+="<h1>Erro na inscrição do cliente na mancha</h1>";
        	}
        }
        
        else if(comando.equals("G")) { //Recomendar equipamento
        	
        	boolean recomendacaoCliente = GestaoPT.recomendarEquipamentos(dados, equipamento, nomeCliente, numPT, inicioRecomendacao, fimRecomendacao, recomendacao);
        	
        	if(recomendacaoCliente == true) {
        		info+="<h1>Recomendação guardada</h1>";
        	}
        	else {
        		info+="<h1>Erro a guardar recomendação</h1>";
        	}
        }
       

        else if(comando.equals("B")){ //Mancha por escalao
        	
    		String[][] manchasEscalao = null;
			try {manchasEscalao = GestaoPT.consultarManchasEscalao(dados, escalao);} catch (SQLException e) {e.printStackTrace();}
    		
			info="<h1>Manchas filtradas pelo escalão</h1><br><table><tr><th>Dia</th><th>Hora de início</th><th>Hora de fim</th><th>PT</th><th>Atividade</th><th>Escalão</th><th>Número de inscrições</th><th>Confirmação</th></tr>";

    		for(int coluna=0; coluna<manchasEscalao[0].length; coluna++){ 
    			info+="<tr>";
    			for(int linha=0; linha<manchasEscalao.length; linha++){
    				info+="<td>"+manchasEscalao[linha][coluna]+"</td>";
    			}
    			info+="</tr>";
    		}
    		info+="</table>";
    	}
        
        else if(comando.equals("Y")){ //Receber o nome dos escaloes
        	
        	String[] nomesEscaloes = null;
        	try {nomesEscaloes = GestaoPT.receberEscaloes(dados);} catch (SQLException e) {e.printStackTrace();}
        	
        	for(int idx=0; idx<nomesEscaloes.length; idx++) {
        		info+=nomesEscaloes[idx];
        		if(idx!=nomesEscaloes.length-1) {
        			info+="|";
        		}
        	}
        }
        
        else if(comando.equals("C")) {//Publicar uma mancha
        	
        	boolean publicarMancha = GestaoPT.publicarMancha(dados, atividade, escalao, numPT, diaMancha, horaInicio, horaFim);
        	
        	if(publicarMancha == true) {
        		info+="<h1>Mancha publicada</h1>";
        	}
        	else {
        		info+="<h1>Erro na publicação da mancha</h1>";
        	}
        }
        
        else if(comando.equals("X")){ //Receber o nome das atividades
        	
        	String[] nomesAtividades = null;
        	try {nomesAtividades = GestaoPT.receberAtividades(dados);} catch (SQLException e) {e.printStackTrace();}
        	
        	for(int idx=0; idx<nomesAtividades.length; idx++) {
        		info+=nomesAtividades[idx];
        		if(idx!=nomesAtividades.length-1) {
        			info+="|";
        		}
        	}
        }
        
        else if(comando.equals("A")){ //Ver proprias manchas
        	
    		String[][] manchasPT = null;
			try {manchasPT = GestaoPT.consultarManchasNumPT(dados, numPT);} catch (SQLException e) {e.printStackTrace();}
    		
			info="<h1>Minhas manchas</h1><br><table><tr><th>Dia</th><th>Hora de início</th><th>Hora de fim<th>Atividade</th><th>Escalão</th><th>Número de inscrições</th><th>Confirmação</th></tr>";

    		for(int coluna=0; coluna<manchasPT[0].length; coluna++){ 
    			info+="<tr>";
    			for(int linha=0; linha<manchasPT.length; linha++){
    				String dado = manchasPT[linha][coluna];
    				if(linha == manchasPT.length - 1) {
    					if (dado.equals("0")) {
        					info+="<td>Não confirmada</td>";
        				}
        				else if (dado.equals("1")) {
        					info+="<td>Confirmada</td>";
        				}
    				}
    				else {
        				info+="<td>"+dado+"</td>";
    				}
    			}
    			info+="</tr>";
    		}
    		info+="</table>";
    	}
        
        System.out.println(info);
        dados.desligar();

        response.getWriter().write(info);
    }
}