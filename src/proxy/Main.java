package proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {	
	
	// Verifica a continuidade da execucao do proxy (para de receber novas requisicoes)
	private static boolean continueRunning;
	
	public static void main(String[] args) {
		
		ListType type = null;
		boolean erro = false;
		List<String> blackOrWhiteList = null;
		
		// inicia com no mínimo 4 argumentos
		if(args.length < 4){
			erro = true;
		}else{
			// O primeiro deve ser -b ou -w, e o terceiro a porta
			if(!args[0].equals("-b") && !args[0].equals("-w") && !args[2].matches("[0-9]*")){
				erro = true;
			}else{
				if(args[0].equals("-b")){
					type = ListType.BLACK_LIST;
				}else{
					type = ListType.WHITE_LIST;
				}
				
				blackOrWhiteList = new ArrayList<String>();
				
				FileReader arquivo;
				BufferedReader leitor;
				
				try {
					
					// Le o arquivo e transforma em uma lista de strings
					arquivo = new FileReader(new File(args[1]));
					leitor = new BufferedReader(arquivo);
					String aux = null;
					while(leitor.ready()){
						aux = leitor.readLine().trim();
						if(!"".equals(aux)){
							blackOrWhiteList.add(aux);
						}
					}
					
					// Inicia o proxy com a lista, tipo de lista, porta e diretorio de arquivos bloqueados
					proxy(blackOrWhiteList, type, new Integer(args[2]), args[3]);
				} catch ( IOException e) {
					System.out.println("O arquivo " + args[1] + " nao foi encontrado");
				}
				
			}
		}
		
		if(erro){
			System.out.println("Uso indevido do programa! \n"
					+ "Deve ser usado como: java -jar proxy.jar <-b ou -r> <path para a black ou white list> <porta do proxy> <diretorio para escrita das paginas bloqueadas>");
		}
		
	}
	
	public static void proxy(List<String> blackOrWhiteList, ListType type, int porta, String diretorioBloquedPages){
		
		/* Funcionamento:
		 * 1) Escuta na porta definida
		 * 2) inicia uma thread que vai efetivamente tratar as requisicoes
		 * 3) volta a escutar na porta definida
		 */
		
		ServerSocket proxy = null;
		
		continueRunning = true;
		
		try {
			proxy = new ServerSocket(porta);
		    System.out.println("Porta "+porta+" aberta!");
		} catch (IOException e) {
			continueRunning = false;
		    System.out.println("Nao foi possivel abria a porta "+porta+"!");
		}
		
		(new Thread(new TrataInputTeclado())).start();
		
	    while(continueRunning){
	    	try {
				Socket requisicao = proxy.accept();
				TrataRequisicao tratamento = new TrataRequisicao(requisicao, blackOrWhiteList, type, diretorioBloquedPages);

				(new Thread(tratamento)).start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
	}
	
	// Permite encerrar a execucao por outra thread
	public static void endRun(){
		continueRunning = false;
	}

}
