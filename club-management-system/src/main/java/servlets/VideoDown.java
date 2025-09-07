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

@WebServlet("/VideoDown")
public class VideoDown extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public VideoDown() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		String designacao = request.getParameter("designacao");
		String numSala = request.getParameter("designacao");
		
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
