package proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DaoRequisicao {
	
	// Cria conexao
	public Connection getConnection(){
		Connection c = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(true);
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    return c;
	}
	
	// Cria uma nova tabela cada ve que o proxy inicia
	public void createTable() throws SQLException{
		Connection c = getConnection();
		
		Statement stmt = c.createStatement();
	      String sql = "DROP TABLE IF EXISTS Requisicoes; " +
	    		  	   "CREATE TABLE Requisicoes   " +
	                   "(IP             TEXT     , " +
	                   " URL            TEXT     , " + 
	                   " DELAY          LONG     , " + 
	                   " BLOCKED        BOOLEAN  ) "; 
	      stmt.executeUpdate(sql);
	      
	      c.close();
	}
	
	//Insere uma nova requisicao
	public void insertRequisicao(Requisicao req) throws SQLException{
		Connection conn = getConnection();
		
		String sql = "INSERT into Requisicoes (IP, URL, DELAY, BLOCKED) values (?,?,?,?)";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, req.getIp());
		stmt.setString(2, req.getUrl());
		stmt.setLong(3, req.getDelay());
		stmt.setBoolean(4, req.isBlocked());
		
		stmt.execute();
		stmt.close();
		
		conn.close();
	}
	
	//Busca URLs por ordem de acesso
	public ResultSet urlsPorOrdemDeAcesso() {
		Connection conn = getConnection();
		
		String sql = "SELECT URL, count(URL) , avg(delay) from Requisicoes GROUP BY URL ORDER BY count(URL) desc";
		
		ResultSet rs = null;
		try {
			Statement stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			System.out.println("Erro: urlsPorOrdemDeAcesso");
		}
		
		return rs;
	}
	
	//Busca IPs por ordem de acesso
	public ResultSet ipsPorOrdemDeAcesso() {
		Connection conn = getConnection();
		
		String sql = "SELECT IP, count(IP) from Requisicoes GROUP BY IP ORDER BY count(IP) desc";
		
		ResultSet rs = null;
		try {
			Statement stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			System.out.println("Erro: ipsPorOrdemDeAcesso");
		}
		
		return rs;
	}
	
	// Busca as URLs bloqueadas
	public ResultSet urlsBloqueadas() {
		Connection conn = getConnection();
		
		String sql = "SELECT URL, IP, count(IP) from Requisicoes WHERE BLOCKED = '1' GROUP BY URL, IP order by URL";
		
		ResultSet rs = null;
		try {
			Statement stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			System.out.println("Erro: urlsBloqueadas");
		}
		
		return rs;
	}
}
