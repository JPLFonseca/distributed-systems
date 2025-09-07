package servlets;

import java.io.IOException;
import java.io.InputStream;

import database.GestaoGerente;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/FotoDown")
public class FotoDown extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public FotoDown() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		String designacao = request.getParameter("designacao");
		String numSala = request.getParameter("designacao");
		
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
