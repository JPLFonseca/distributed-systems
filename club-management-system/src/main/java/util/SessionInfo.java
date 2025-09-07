package util;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SessionInfo {

	public static char getTipoUtilizador(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		if (session != null) {
			
			String tipoUtilizador = (String) session.getAttribute("tipoUtilizador");
			
			if (tipoUtilizador != null)
				return tipoUtilizador.charAt(0);
			else
				session.invalidate();
		}
		request.getRequestDispatcher("/login.html").forward(request, response);
		return '0';
	}
	
}
