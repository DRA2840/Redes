package proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe que fornece uma interface simples com o banco de dados.
 * 
 * @author <img src="https://avatars2.githubusercontent.com/u/3778188?v=2&s=30" width="30" height="30" /> <a href="https://github.com/DRA2840" target="_blank"> DRA2840 </a>
 *
 */
public class DaoRequisicao {
	
	/**
	 * Cria uma nova conexao com o Banco de Dados
	 * @return {@link Connection} 
	 */
	private Connection getConnection(){
		Connection conn = null;
	    try {
	    	// Procura o Driver do sqlite (obtido via maven, nao eh necessario adicionar ao classpath manualmente)
	    	Class.forName("org.sqlite.JDBC");
	    	conn = DriverManager.getConnection("jdbc:sqlite:test.db");
	    	conn.setAutoCommit(true);
	    } catch ( SQLException | ClassNotFoundException e ) {
	    	e.printStackTrace();
	    }
	    return conn;
	}
	
	/** 
	 * Cria uma nova tabela. É usado cada vez que o proxy recebe a primeira {@link Requisicao}
	 * 
	 * @throws SQLException Caso a tabela não seja criada.
	 */
	public void createTable() throws SQLException{
		Connection c = getConnection();
		
		// Cria a tabela Requisicoes
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
	
	/** 
	 * Insere uma nova {@link Requisicao} no Banco de Dados
	 * 
	 * @param req {@link Requisicao} a ser gravada
	 * @throws SQLException Caso nao seja possivel gravar a {@link Requisicao}
	 */
	public void insertRequisicao(Requisicao req) throws SQLException{
		Connection conn = getConnection();
		
		String sql = "INSERT into Requisicoes (IP, URL, DELAY, BLOCKED) values (?,?,?,?)";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		// Bind de variaveis, prevenindo SQL injection
		// Motivacao: http://xkcd.com/327/
		stmt.setString(1, req.getIp());
		stmt.setString(2, req.getUrl());
		stmt.setLong(3, req.getDelay());
		stmt.setBoolean(4, req.isBlocked());
		
		stmt.execute();
		stmt.close();
		
		conn.close();
	}
	
	/** 
	 * Busca URLs mais requisitadas, em ordem decrescente. Usado como estatistica basica, possui os campos:
	 * <ul>
	 * <li> URL: url que foi requisitada.</li>
	 * <li> acessos: Numero de vezes que a URL foi requisitada</li>
	 * <li> tempo_Medio: Tempo medio, em milisegundos, gasto para receber o conteudo da pagina</li>
	 * </ul>
	 * 
	 * @return {@link ResultSet} com os campos acima.
	 */
	public ResultSet urlsPorOrdemDeAcesso() {
		Connection conn = getConnection();
		
		String sql = "SELECT URL, count(URL) as acessos , avg(delay) as tempo_Medio from Requisicoes GROUP BY URL ORDER BY count(URL) desc";
		
		ResultSet rs = null;
		try {
			// Seria mais elegante usar PreparedStatement, mas como nao ha possibilidade de
			// SQL injection, optei pela alternativa mais simples.
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/**
	 * Busca IPs que mais fizeram requisicoes, em ordem descescente. Usado como estatistica basica, possui os campos:
	 * <ul>
	 * <li> IP: ip da maquina que gerou as requisicoes</li>
	 * <li> acessos: Numero de requisicoes feitas</li>
	 * </ul>
	 *  
	 * @return {@link ResultSet} com os campos acima.
	 */
	public ResultSet ipsPorOrdemDeAcesso() {
		Connection conn = getConnection();
		
		String sql = "SELECT IP, count(IP) as acessos from Requisicoes GROUP BY IP ORDER BY count(IP) desc";
		
		ResultSet rs = null;
		try {
			// Seria mais elegante usar PreparedStatement, mas como nao ha possibilidade de
			// SQL injection, optei pela alternativa mais simples.
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/**
	 * Busca as URLs bloqueadas, em ordem decrescente de requisicoes. Usado como estatistica basica, possui os campos:
	 * <ul>
	 * <li> URL: url que foi requisitada.</li>
	 * <li> IP: ip que requisitou a url bloqueada</li>
	 * <li> tentativas: Numero de tentativas de acesso que aquele IP fez para aquela URL</li>
	 * </ul>
	 * 
	 * @return {@link ResultSet} com os campos acima.
	 */
	public ResultSet urlsBloqueadas() {
		Connection conn = getConnection();
		
		String sql = "SELECT URL, IP, count(IP) as tentativas from Requisicoes WHERE BLOCKED = '1' GROUP BY URL, IP order by URL";
		
		ResultSet rs = null;
		try {
			// Seria mais elegante usar PreparedStatement, mas como nao ha possibilidade de
			// SQL injection, optei pela alternativa mais simples.
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
}
