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
import java.time.LocalDate;
import java.io.IOException;
import database.*;

@WebServlet("/ServletCliente")
public class ServletCliente extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String comando = request.getParameter("comando");
        String uID = request.getParameter("uID");
        String atividade = request.getParameter("Atividade");
        String diaMancha = request.getParameter("diaAtividade");
        String horaInicio = request.getParameter("inicioAtividade");
        String horaFim = request.getParameter("fimAtividade");
        String email = request.getParameter("email");
        String telemovel = request.getParameter("telemovel");
        String descricao = request.getParameter("patologia");
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String descObjetivo = request.getParameter("objetivo");
        	
        String info = "";
        

        Manipula dados = new Manipula(new Configura());
        
        
        if(comando.equals("A")) { //Atualizar perfil
        	
        	String[][] new_data = {{email},{telemovel},{descricao,dataInicio,dataFim},{descObjetivo}};
        	
        	boolean inscreverCliente = GestaoCliente.atualizarPerfil(dados, uID,new_data);
        	
        	if(inscreverCliente == true) {
        		info+="<h1>Perfil atualizado com sucesso!</h1>";
        	}
        	else {
        		info+="<h1>Erro a atualizar o perfil!</h1>";
        	}
        }
        
        else if(comando.equals("G")) { // ver atividades de grupo
        	
        	String[][] manchasCliente = null;
			try {manchasCliente = GestaoCliente.mostrarAtividadesGrupo(dados);} catch (SQLException e) {e.printStackTrace();}
    		
			info="<h1>Minhas manchas</h1><br><table><tr><th>Dia</th><th>Hora de inicio</th><th>Hora de fim<th>Atividade</th><th>Escalao</th><th>Numero de inscricoes</th><th>Confirmacao</th></tr>";

    		for(int coluna=0; coluna<manchasCliente[0].length; coluna++){ 
    			info+="<tr>";
    			for(int linha=0; linha<manchasCliente.length; linha++){
    				System.out.println(manchasCliente[linha][coluna] + " P");
    				info+="<td>"+manchasCliente[linha][coluna]+"</td>";
    			}
    			info+="</tr>";
    		}
    		info+="</table>";
        	
        	
        }
        
        else if(comando.equals("F")) { // ver atividades individuais do seu PT
        	
        	String[][] manchasCliente = null;
			try {manchasCliente = GestaoCliente.mostraAtividadeIndividual(dados,uID);} catch (SQLException e) {e.printStackTrace();}
    		
			info="<h1>Minhas manchas</h1><br><table><tr><th>Dia</th><th>Hora de inicio</th><th>Hora de fim<th>Atividade</th><th>Escalao</th><th>Numero de inscricoes</th><th>Confirmado</th></tr>";

    		for(int coluna=0; coluna<manchasCliente[0].length; coluna++){ 
    			info+="<tr>";
    			for(int linha=0; linha<manchasCliente.length; linha++){
    				System.out.println(manchasCliente[linha][coluna] + " P");
    				info+="<td>"+manchasCliente[linha][coluna]+"</td>";
    			}
    			info+="</tr>";
    		}
    		info+="</table>";
        	
        	
        }
       

        else if(comando.equals("B")){ //Inscrever atividades individuais
        	
        	
        	boolean inscreverCliente = GestaoCliente.subAtividadeIndividual(dados, uID, atividade, diaMancha, horaInicio, horaFim);
        	
        	if(inscreverCliente == true) {
        		info+="<h1>Foi inscrito na mancha</h1>";
        	}
        	else {
        		info+="<h1>Erro na inscricao do na mancha</h1>";
        	}
        	

    	}
        
        else if(comando.equals("C")) {//Inscrever uma atividade grupo
        	
        	boolean inscreverCliente = GestaoCliente.subAtividadeGrupo(dados, uID, atividade, diaMancha, horaInicio, horaFim);
        	
        	if(inscreverCliente == true) {
        		info+="<h1>Foi inscrito na mancha</h1>";
        	}
        	else {
        		info+="<h1>Erro na inscricao do na mancha</h1>";
        	}
        	
        	
        	
        }
        
       else if(comando.equals("E")) {// Ve recomendacoe do seu PT em vigor
        	
        	
    	   	LocalDate localDate = LocalDate.now();
   			String data = localDate.toString(); // devolve a data do dia de hoje em String
        	
        	String[][] fichaCliente = null;
			try {fichaCliente = GestaoCliente.consultaRecomendacoes(dados, uID,data);} catch (SQLException e) {e.printStackTrace();}
    		
			info="<h1>Recomendacoes </h1><br><table><tr><th>Equipamento recomendado</th><th>Recomendacao</th><th>Inicio da recomendacao</th><th>Fim da recomendacao</th></tr>";

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
    		info+="</table>";}
        
        System.out.println(info);
        dados.desligar();

        response.getWriter().write(info);
    }
}
