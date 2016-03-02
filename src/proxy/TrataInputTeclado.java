package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe responsavel por esperar sinal de encerramento por parte do usuario, avisar esse encerramento para a main
 * e mostrar as estatisticas de uso do proxy.
 * 
 * @author <img src="https://avatars2.githubusercontent.com/u/3778188?v=2&s=30" width="30" height="30" /> <a href="https://github.com/DRA2840" target="_blank"> DRA2840 </a>
 *
 */
public class TrataInputTeclado implements Runnable{
	
	
	
	/**
	 * Metodo principal da thread. Fica constantemente lendo um input do teclado, e encerra o proxy quando
	 * for digitado 'sair'. Caso tenha alguma IOException, também encerra o proxy.<br />
	 * Independente da forma que encerrar o proxy, o programa mostra as estatísticas.
	 * 
	 */
	@Override
	public void run() {
		boolean continueRunning = true;
		BufferedReader recebido_browser = new BufferedReader(new InputStreamReader(System.in));
		
		while (continueRunning){
			System.out.println("Digite 'sair' para encerrar o proxy: ");
			String mensagem;
			try {
				
				// Se o usuario mandar sair, encerra a Main e mostra as estatisticas.
				mensagem = recebido_browser.readLine();
				if ("sair".equals(mensagem)){
					continueRunning = false;
					Main.endRun();
					mostrarEstatisticas();
				}
			} catch (IOException e) {
				
				// Se houver algum problema lendo o input do usuario, aborta o proxy e mostra as estatisticas.
				System.out.println("Erro lendo input do teclado. Abortando.");
				continueRunning = false;
				Main.endRun();
				mostrarEstatisticas();
			}
			
		}
        
        
		
	}
	
	/**
	 *  Ao final da execucao, busca as estatisticas pela DAO e mostra para o usuario.
	 *  
	 */
	private void mostrarEstatisticas() {
		
		DaoRequisicao dao = new DaoRequisicao();
		
		try {
			ResultSet rs = dao.urlsPorOrdemDeAcesso();
			
			System.out.println("\n\nLista de URLs por ordem de acesso: ");
			while(rs.next()){
				System.out.println("URL: " + rs.getString(1) + " acessos: " + rs.getInt(2));
			}
			
			rs = dao.ipsPorOrdemDeAcesso();
			
			System.out.println("\n\nLista de IPs por ordem de acesso: ");
			while(rs.next()){
				System.out.println("IP: " + rs.getString(1) + " acessos: " + rs.getInt(2));
			}
			
			rs = dao.urlsBloqueadas();
			
			System.out.println("\n\nLista de URLs bloqueadas e IPs que tentaram acesso: ");
			while(rs.next()){
				System.out.println("URL bloqueada: " + rs.getString(1) + " IP: " + rs.getString(2) + " tentativas: " + rs.getInt(3));
			}
			
		} catch (SQLException e) {
			
			// Se nao puder se conectar com o banco, avisa o usuario
			System.out.println("Problemas ao conectar com o BD");
		}
		
	}
	

}
