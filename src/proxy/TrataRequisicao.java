package proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * Classe primordial para o funcionamento do proxy, eh responsavel por receber um socket e outras informacoes,
 * Funcionamento:
 * <ol>
 * <li>Recebe um {@link Socket} de uma requisicao.</li>
 * <li>Faz a requisicao na rede.</li>
 * <li>Verifica a validade da URL. 
 * 		<ul>
 * 		<li> Se estiver bloqueada, retorna uma pagina de erro e grava o resultado da requisicao em disco para avaliacao</li>
 * 		<li> Se estiver liberada, envia o resultado da requisicao direto para o usuario</li></ul></li> 
 * <li>Adiciona essa requisicao nas estatisticas</li>
 * </ol>
 * 
 * @author <img src="https://avatars2.githubusercontent.com/u/3778188?v=2&s=30" width="30" height="30" /> <a href="https://github.com/DRA2840" target="_blank"> DRA2840 </a>
 *
 */
public class TrataRequisicao implements Runnable{

	// Ignoro essas extensoes na hora de gravar no banco, pois sao sub-requisicoes de uma pagina.
	private static final String[] EXTENSOES_NAO_CONTAM = {".jpg", ".jpeg", ".gif", ".ico", ".js", ".css", ".woff"
		, ".png", ".svg", ".tiff", ".dib", ".bmp", ".avi", ".mp4", ".tif"};
	
	
	private String diretorioBloquedPages;  // Diretorio de paginas bloqueadas
	private static DaoRequisicao dao;      // Dao que grava as requisicoes
	private Socket requisicao;             // Socket entre o proxy e o cliente
	private ListType tipo;                 // Tipo de lista (Black or White list)
	private List<String> blackOrWhiteList; // Lista de URLs bloqueadas/liberadas
	
	/**
	 * Construtor.
	 * 
	 * @param requisicao            {@link Socket} com a requisicao feita pelo cliente
	 * @param blackOrWhiteList      {@link List} de {@link String} com as URLs a serem permitidas/bloqueadas
	 * @param tipo                  {@link ListType} Tipo de Lista (Black or White list)
	 * @param diretorioBloquedPages Path para o diretorio onde serao armazenadas as paginas bloqueadas.
	 */
	public TrataRequisicao(Socket requisicao, List<String> blackOrWhiteList, ListType tipo, String diretorioBloquedPages){
		this.requisicao = requisicao;
		this.blackOrWhiteList = blackOrWhiteList;
		this.tipo = tipo;
		this.diretorioBloquedPages = diretorioBloquedPages;
	}

	/**
	 * Metodo chamado pela {@link Thread} criada com esse {@link Runnable}.
	 * Eh responsavel por executar a logica da classe.
	 */
	@Override
	public void run() {
		
		// Cria uma Requisicao para armazenar os dados dessa pesquisa no proxy
		Requisicao atual = new Requisicao();

		try {
			
			OutputStream resposta_user  = requisicao.getOutputStream();
			resposta_user.flush();
			
			// Pega os tokens da requisicao e cria uma nova conexao
			String[] splitedHttpMethod = splitHttpMethod(requisicao);
			HttpURLConnection connection = getConnection(splitedHttpMethod);
			
			// Pega a resposta do servidor e verifica o tempo gasto
			long before = System.currentTimeMillis();
			InputStream resposta_servidor = connection.getInputStream();
			long after = System.currentTimeMillis();
			
			// Monta os dados estatisticos da requisicao
			atual.setIp( requisicao.getInetAddress().toString() );
			atual.setDelay(after - before);
			atual.setUrl(splitedHttpMethod[1]);
			atual.setBlocked(! isValidUrl(atual.getUrl()));
			
			// Acompanhamento em tempo real =P
			System.out.println("Requisicao: \n\tIP: " + atual.getIp() + "\n\tURL: " + atual.getUrl() + "\n\tTempo gasto: " + atual.getDelay() );
			
			if( ! atual.isBlocked() ){
				
				// Se nao esta bloqueada, so manda pro cliente
				resposta_user.write(IOUtils.toByteArray(resposta_servidor));
				resposta_user.flush();
				requisicao.close();
			}else{
				
				// Se estiver bloqueada, retorna pagina de erro
				resposta_user.write(IOUtils.toByteArray(new FileReader(new File("bloquedResponse.html") ) ));
				resposta_user.flush();
				requisicao.close();
				
				// Monta o nome do arquivo
				String file = splitedHttpMethod[1].replace("http://", "").replace("/", ".");
				if(file.endsWith(".")){
					file = file + "html";
				}
				
				// Grava o arquivo no diretorio especifico
				FileOutputStream out = new FileOutputStream( diretorioBloquedPages + "/" + file);
				
				out.write(IOUtils.toByteArray(resposta_servidor));
				
				IOUtils.closeQuietly(out);
			}
			
			// Adiciona nas estatisticas
			addStatistcs(atual);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Divide os tokens da requisicao (GET, URL, VERSION)
	 * 
	 * @param requisicao {@link Socket} entre o cliente e o proxy
	 * @return Array de String com cada um dos tokens
	 * @throws IOException Se a requisicao estiver vazia
	 */
	private String[] splitHttpMethod(Socket requisicao) throws IOException{
		BufferedReader recebido_browser = new BufferedReader(new InputStreamReader(requisicao.getInputStream()));
        String mensagem = recebido_browser.readLine();
        
        return mensagem.split(" ");
	}
	
	/**
	 * Verifica se a URL eh valida
	 * 
	 * @param url URL a ser verificada
	 * @return true se for valida, false caso contrario
	 */
	private boolean isValidUrl(String url){
		
		url = url.replace("http://", "");
		
		// Se for BlackList, vai estar bloqueada se estiver na lista.
		if(tipo.equals(ListType.BLACK_LIST)){
			
			for(String blockedItem : blackOrWhiteList){
				
				if(url.startsWith(blockedItem)){
					System.out.println("URL bloqueada: "+ url);
					return false;
				}
			}
			return true;
			
		// Se for Whitelist, vai estar bloqueada se nao estiver na lista.
		}else{
			
			for(String permitedItem : blackOrWhiteList){
				
				if(url.startsWith(permitedItem)){
					return true;
				}
			}
			System.out.println("URL bloqueada: "+ url);
			return false;
			
		}
		
	}
	
	/**
	 * Cria a conexao HTTP com o servidor
	 * 
	 * @param splitedHttpMethod Tokens da requisicao HTTP (GET, URL, VERSION)
	 * @return {@link HttpURLConnection} conexao aberta com o servidor.
	 * @throws IOException Se houver algum problema com a requisicao.
	 */
	private HttpURLConnection getConnection(String[] splitedHttpMethod) throws IOException{
		
		URL serveradress = new URL(splitedHttpMethod[1]);
		HttpURLConnection connection = (HttpURLConnection)serveradress.openConnection();
		connection.setRequestMethod(splitedHttpMethod[0]);
		
		connection.setDoOutput(true);
		connection.connect();
		
		return connection;
	}
	
	/**
	 * Adiciona as estatisticas de uma requisicao completa. 
	 * 
	 * @param req {@link Requisicao} que deve ser gravada.
	 */
	private static synchronized void addStatistcs( Requisicao req){
		
		
		// Verifica se realmente deve ser colocado no banco
		for(int i = 0; i < EXTENSOES_NAO_CONTAM.length; i++){
			if(req.getUrl().endsWith( EXTENSOES_NAO_CONTAM[i] )){
				// Se deve ser ignorado, ja sai do metodo
				return;
			}
		}
		
		try {
			// Se a variavel estatica dao for nula, inicia ela.
			if(dao == null){
				dao = new DaoRequisicao();
				dao.createTable();
			}
			
			// Insere a requisicao
			dao.insertRequisicao(req);
			
		} catch (SQLException e) {
			System.out.println("Falha ao se comunicar com o Banco de Dados.");
			e.printStackTrace();
		}
	}
}
