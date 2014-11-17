package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrataInputTeclado implements Runnable{
	
	
	
	@Override
	public void run() {
		boolean continueRunning = true;
		BufferedReader recebido_browser = new BufferedReader(new InputStreamReader(System.in));
		
		// Essa thread eh responsavel por pausar a execucao
		while (continueRunning){
			System.out.println("Digite 'sair' para encerrar o proxy: ");
			String mensagem;
			try {
				mensagem = recebido_browser.readLine();
				if ("sair".equals(mensagem)){
					continueRunning = false;
					Main.endRun();
					mostrarEstatisticas();
				}
			} catch (IOException e) {
				System.out.println("Erro lendo input do teclado. Abortando execussão.");
				continueRunning = false;
				Main.endRun();
				mostrarEstatisticas();
			}
			
		}
        
        
		
	}
	
	// Ao final da execucao, busca as estatisticas pela DAO e mostra aqui.
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
			System.out.println("Problemas ao conectar com o BD");
		}
		
	}
	

}
