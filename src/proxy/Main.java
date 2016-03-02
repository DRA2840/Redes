package proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal do Proxy, eh responsavel por fazer o tratamento dos dados iniciais e iniciar o loop do proxy
 * O uso do proxy deve ter os seguintes parametros passados por linha de comando:
 * <ol>
 * <li><i>-b</i> ou <i>-w</i>: -b para rodar em modo blacklist e -w para rodar em modo whitelist</li>
 * <li><i>caminho (path)</i>: caminho para a localizacao do arquivo da lista</li>
 * <li><i>porta</i>: porta que o proxy vai escutar. Deve ser um numero, e nao ser uma porta reservada.</li>
 * <li><i>caminho (path)</i>: caminho para o diretorio para escrita das paginas bloqueadas. Nao deve terminar em '/'</li>
 * </ol>
 * 
 * @author <img src="https://avatars2.githubusercontent.com/u/3778188?v=2&s=30" width="30" height="30" /> <a href="https://github.com/DRA2840" target="_blank"> DRA2840 </a>
 *
 */
public class Main {	
	
	// Verifica a continuidade da execucao do proxy
	private static boolean continueRunning;
	
	/**
	 * Metodo Main, faz a verificacao dos argumentos e chama o metodo proxy, que realmente inicia o proxy.
	 * 
	 * @param args Argumentos passados por linha de comando da inicializacao da classe.<ol>
	 * <li><i>-b</i> ou <i>-w</i>: -b para rodar em modo blacklist e -w para rodar em modo whitelist</li>
	 * <li><i>caminho (path)</i>: caminho para a localizacao do arquivo da lista</li>
	 * <li><i>porta</i>: porta que o proxy vai escutar. Deve ser um numero, e nao ser uma porta reservada.</li>
	 * <li><i>caminho (path)</i>: caminho para o diretorio para escrita das paginas bloqueadas. Nao deve terminar em '/'</li>
	 * </ol>
	 */
	public static void main(String[] args) {
		
		ListType type = null;// Black or white list
		boolean erro = false;// no errors so far
		List<String> blackOrWhiteList = null; // no list of urls do far
		
		// inicia com 4 argumentos
		if(args.length < 4){
			erro = true;
		}else{
			// O primeiro deve ser -b ou -w, e o terceiro a porta
			if(!args[0].equals("-b") && !args[0].equals("-w") && !args[2].matches("[0-9]*")){
				erro = true;
				
			}else{
				
				// Define o tipo de lista
				if(args[0].equals("-b")){
					type = ListType.BLACK_LIST;
				}else{
					type = ListType.WHITE_LIST;
				}
				
				// Inicia a lista
				blackOrWhiteList = new ArrayList<String>();
				
				FileReader arquivo;
				BufferedReader leitor;
				
				try {
					
					// Le o arquivo e transforma em uma lista de strings
					arquivo = new FileReader(new File(args[1]));
					leitor = new BufferedReader(arquivo);
					String aux = null;
					while(leitor.ready()){
						
						// limpa possiveis espacos na url
						aux = leitor.readLine().trim();
						
						// Se nao for um endereco vazio
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
		
		// Explica como usar o programa de maneira correta
		if(erro){
			System.out.println("Uso indevido do programa! \n"
					+ "Deve ser usado como: java -jar proxy.jar <-b ou -r> <path para a black ou white list> <porta do proxy> <diretorio para escrita das paginas bloqueadas>");
		}
		
	}
	
	/**
	 * Proxy realmente dito. Inicia o {@link ServerSocket} que vai escutar as requisicoes, e inicia novas Threads de
	 * {@link TrataRequisicao} para cada {@link Socket}
	 * 
	 * @param blackOrWhiteList {@link List} de {@link String} com as URLs a serem permitidas/bloqueadas
	 * @param type {@link ListType} Tipo de Lista (Black or White list)
	 * @param porta Porta que o proxy vai excutar
	 * @param diretorioBloquedPages Path para o diretorio onde serao armazenadas as paginas bloqueadas.
	 */
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
		
		// Essa Thread eh responsavel por esperar o usuario encerrar o proxy
		// Ela chama o metodo estatico endRun() desta classe para faze-lo
		(new Thread(new TrataInputTeclado())).start();
		
		// Enquanto nao for encerrada, continua escutando
	    while(continueRunning){
	    	try {
	    		// Aceita nova requisicao
				Socket requisicao = proxy.accept();
				
				// Constroi uma classe responsavel pelo tratamento
				TrataRequisicao tratamento = new TrataRequisicao(requisicao, blackOrWhiteList, type, diretorioBloquedPages);

				// Faz o tratamento em uma nova Thread, permitindo que o proxy volte a aceitar conexoes.
				(new Thread(tratamento)).start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
	}
	
	/**
	 * Permite encerrar a execucao por outra thread
	 */
	public static void endRun(){
		continueRunning = false;
	}

}
