package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import database.GestaoGerente;

/**
 * Servlet implementation class ServletGerente
 */
@WebServlet("/ServletGerente")
public class ServletGerente extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletGerente() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String comando = request.getParameter("comando");
		String designacao = request.getParameter("designacao");
		String email = request.getParameter("email");
		String telemovel = request.getParameter("telemovel");
		String diaSemana = request.getParameter("diaSemana");
		String horaAbertura = request.getParameter("horaAbertura");
		String horaFecho = request.getParameter("horaFecho");
		String opHorario = request.getParameter("opHorario");
		String numSala = request.getParameter("numSala");
		
		String info = "";
		
		if (comando.equals("W")) { // contactos do clube
			String[] contactos = GestaoGerente.getContactosClube(designacao);
			
			if (contactos != null) {
				info = contactos[0] + "|" + contactos[1];
				System.out.println("Contactos do clube " + designacao + ":");
				System.out.println(info);
			}
		}
		
		else if (comando.equals("X")) { // horário do clube
			
			String[][] horario = GestaoGerente.getHorarioClube(designacao);
			
			info = "<table><tr><th>Dia da semana</th><th>Hora de abertura</th><th>Hora de fecho</th></tr>";
			
			for (int i = 0; i < horario.length; i++) {
				
				if (horario[i][0] == null)
					continue;
				
				info += "<tr><td>";
				
				if (i == 0) info += "Segunda";
				if (i == 1) info += "Terca";
				if (i == 2) info += "Quarta";
				if (i == 3) info += "Quinta";
				if (i == 4) info += "Sexta";
				if (i == 5) info += "Sabado";
				if (i == 6) info += "Domingo";
				
				info += "</td><td>" + horario[i][0] + "</td><td>" + horario[i][1] + "</td></tr>";
			}
			
			info += "</table>";
			
			System.out.println("Horário do clube " + designacao + ":");
			System.out.println(info);
		}
		
		else if (comando.equals("Z")) { // foto da sala
			
			InputStream streamFoto = GestaoGerente.getFotoSala(designacao, numSala);

			if (streamFoto != null) {
				byte[] buffer = new byte[streamFoto.available()];
				streamFoto.read(buffer);
				streamFoto.close();
			
				OutputStream os = response.getOutputStream();
				os.write(buffer);
				os.close();
			}
		}
		
		else if (comando.equals("Y")) { // vídeo da sala
			
			InputStream streamVideo = GestaoGerente.getVideoSala(designacao, numSala);

			if (streamVideo != null) {
				byte[] buffer = new byte[streamVideo.available()];
				streamVideo.read(buffer);
				streamVideo.close();
			
				OutputStream os = response.getOutputStream();
				os.write(buffer);
				os.close();
			}
		}
		
		else if (comando.equals("T")) { // números de sala
			
			List<String> numerosSala = GestaoGerente.getNumerosSala(designacao);
			
			for(int i = 0; i < numerosSala.size(); i++) {
				info += numerosSala.get(i);
				
				if (i != numerosSala.size()-1) info += "|";
			}
			
			System.out.println("Números de sala do clube "+designacao+":");
			System.out.println(info);
		}
		
		else if (comando.equals("A")) { // atualizar contactos
			
			String[] contactos = new String[] {email, telemovel};
			
			if (GestaoGerente.updateContactosClube(designacao, contactos)) {
				System.out.println("Contactos do clube " + designacao + " atualizados:");
				System.out.println(email + "|" + telemovel);
				info = "<p>Contactos do clube atualizados com sucesso.</p>";
			}
			else {
				System.out.println("Erro ao atualizar contactos do clube "+designacao);
				info = "<p>Erro na atualização.</p>";
			}
		}
		
		else if (comando.equals("B")) { // atualizar horarios
			
			boolean sucesso = false;
			
			if (opHorario.equals("add"))
				sucesso = GestaoGerente.updateHorarioClube(designacao, diaSemana, horaAbertura, horaFecho);
			
			else if (opHorario.equals("rem"))
				sucesso = GestaoGerente.removerHorarioClube(designacao, diaSemana);
			
			if (sucesso) {
				System.out.println("Horário do clube "+designacao+" atualizado com sucesso");
				info = "<p>Horário do clube atualizado com sucesso.</p>";
			}
		}
		
		else if (comando.equals("C")) { // atualizar vídeo e imagem
			
			boolean sucesso = false;
			Part fotoPart = request.getPart("fotoSala");
			if (fotoPart != null && fotoPart.getSize() != 0) {
				String fotoType = fotoPart.getContentType();
				InputStream streamFoto = fotoPart.getInputStream();
				sucesso = GestaoGerente.updateFotoSala(designacao, numSala, streamFoto, fotoType);
				streamFoto.close();
			}
			
			if (sucesso) {
				System.out.println("Foto da sala "+numSala+" atualizada com sucesso");
				info += "<p>Foto atualizada com sucesso.</p>";
			}
			
			Part videoPart = request.getPart("videoSala");
			if (videoPart != null && videoPart.getSize() != 0) {
				String videoType = videoPart.getContentType();
				InputStream streamVideo = videoPart.getInputStream();
				sucesso = GestaoGerente.updateVideoSala(designacao, numSala, streamVideo, videoType);
				streamVideo.close();
			}

			if (sucesso) {
				System.out.println("Vídeo da sala "+numSala+" atualizado com sucesso");
				info += "<br><p>Vídeo atualizado com sucesso.</p>";
			}
		}
		
		else if (comando.equals("E")) { // equipamentos menos utilizados
			
			String[][] equipamentos = GestaoGerente.getEquipamentosMenosUtilizados(designacao);
			
			info = "<table><tr><th>Nº da sala</th><th>Nº do equipamento</th><th>Tipo de equipamento</th><th>Tempo de utilização</th></tr>";
			
			for (int i = 0; i < equipamentos.length; i++) {
				
				if (equipamentos[i][0] == null)
					continue;
				
				info += "<tr><td>"+equipamentos[i][0]+"</td><td>"+equipamentos[i][1]+
						"</td><td>"+equipamentos[i][2]+"</td><td>"+equipamentos[i][3]+"</td>";
			}
			
			System.out.println("Lista dos equipamentos do clube "+designacao+" com menos utilização:");
			System.out.println(info);
		}
		
		if (!comando.equals("Z") && !comando.equals("Y"))
			response.getWriter().write(info);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
