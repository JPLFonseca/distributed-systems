package servlets;

import database.Manipula;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Manipula dbAccess = new Manipula();
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		try {
			String diretiva = "select userPassword, tipo from Utilizador where email='" + email + "'";
			ResultSet rs = dbAccess.getResultado(diretiva);
			
			if (rs != null && rs.next()) {
				if (password.compareTo(rs.getString("userPassword")) == 0) {
					switch(rs.getString("tipo").charAt(0)) {
					case 'C':
						ResultSet rsCliente = dbAccess.getResultado("select userID from Utilizador where email='" + email + "'");
						rsCliente.next();
						String uID = rsCliente.getString("userID");
						System.out.println(uID + " s");
						request.setAttribute("uID", uID);
						request.getRequestDispatcher("/Cliente.jsp").forward(request, response);
						return;
					case 'P':
						ResultSet rsPT = dbAccess.getResultado("select numPT FROM Utilizador u, PT p where u.email='" + email + "' AND p.userID=u.userID");
						rsPT.next();
						String numPT = rsPT.getString("numPT");
						System.out.println(numPT + " s");
						request.setAttribute("numPT", numPT);
						request.getRequestDispatcher("/PT.jsp").forward(request, response);
						return;
					case 'G':
						request.getRequestDispatcher("/Gerente.jsp").forward(request, response);
						return;
					default:
						request.getRequestDispatcher("/").forward(request, response);
						return;
					}
				}
			}
			
			request.getRequestDispatcher("/").forward(request, response);
			
		} catch (SQLException e) {
			System.err.println("-----SQLException-----");
			System.err.println("SQLState:  " + e.getSQLState());
			System.err.println("Message:  " + e.getMessage());
			System.err.println("Vendor:  " + e.getErrorCode());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
