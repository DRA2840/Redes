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

public class TrataRequisicao implements Runnable{

	private static final String[] EXTENSOES_NAO_CONTAM = {".jpg", ".jpeg", ".gif", ".ico", ".js", ".css", ".woff"
		, ".png", ".svg", ".tiff", ".dib", ".bmp", ".avi", ".mp4", ".tif"};
	
	String diretorioBloquedPages;
	
	private static DaoRequisicao dao;
	
	private Socket requisicao;
	private ListType tipo;
	private List<String> blackOrWhiteList;
	
	public TrataRequisicao(Socket requisicao, List<String> blackOrWhiteList, ListType tipo, String diretorioBloquedPages) throws IOException{
		this.requisicao = requisicao;
		this.blackOrWhiteList = blackOrWhiteList;
		this.tipo = tipo;
		this.diretorioBloquedPages = diretorioBloquedPages;
	}

	@Override
	public void run() {
		
		Requisicao atual = new Requisicao();

		try {
			OutputStream req_server  = requisicao.getOutputStream();
			req_server.flush();
			String[] splitedHttpMethod = splitHttpMethod(requisicao);
			HttpURLConnection connection = getConnection(splitedHttpMethod);
			
			long before = System.currentTimeMillis();
			InputStream res_server = connection.getInputStream();
			long after = System.currentTimeMillis();
			
			// Monta os dados estatisticos da requisicao
			atual.setIp( requisicao.getInetAddress().toString() );
			atual.setDelay(after - before);
			atual.setUrl(splitedHttpMethod[1]);
			atual.setBlocked(! isValidUrl(atual.getUrl()));
			
			System.out.println("Requisicao: \n\tIP: " + atual.getIp() + "\n\tURL: " + atual.getUrl() + "\n\tTempo gasto: " + atual.getDelay() );
			
			if( ! atual.isBlocked() ){
				
				req_server.write(IOUtils.toByteArray(res_server));
				req_server.flush();
				requisicao.close();
			}else{
				req_server.write(IOUtils.toByteArray(new FileReader(new File("bloquedResponse.html") ) ));
				req_server.flush();
				requisicao.close();
				
				String file = splitedHttpMethod[1].replace("http://", "").replace("/", ".");
				if(file.endsWith(".")){
					file = file + "html";
				}
				
				FileOutputStream out = new FileOutputStream( diretorioBloquedPages + "/" + file);
				
				out.write(IOUtils.toByteArray(res_server));
				
				
				IOUtils.closeQuietly(out);
			}
			
			// Adiciona nas estatisticas
			addStatistcs(atual);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private String[] splitHttpMethod(Socket requisicao) throws IOException{
		BufferedReader recebido_browser = new BufferedReader(new InputStreamReader(requisicao.getInputStream()));
        String mensagem = recebido_browser.readLine();
        
        return mensagem.split(" ");
	}
	
	private boolean isValidUrl(String url){
		
		url = url.replace("http://", "");
		
		if(tipo.equals(ListType.BLACK_LIST)){
			
			for(String blockedItem : blackOrWhiteList){
				
				if(url.startsWith(blockedItem)){
					System.out.println("URL bloqueada: "+ url);
					return false;
				}
			}
			return true;
			
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
	
	private HttpURLConnection getConnection(String[] splitedHttpMethod) throws IOException{
		
		URL serveradress = new URL(splitedHttpMethod[1]);
		HttpURLConnection connection = (HttpURLConnection)serveradress.openConnection();
		connection.setRequestMethod(splitedHttpMethod[0]);
		
		connection.setDoOutput(true);
		connection.connect();
		
		return connection;
	}
	
	private static synchronized void addStatistcs( Requisicao r){
		
		boolean shouldCount = true;
		
		for(int i = 0; i < EXTENSOES_NAO_CONTAM.length; i++){
			if(r.getUrl().endsWith( EXTENSOES_NAO_CONTAM[i] )){
				shouldCount = false;
			}
		}
		
		if(shouldCount){
			try {
				if(dao == null){
					dao = new DaoRequisicao();
					dao.createTable();
				}
				
				dao.insertRequisicao(r);
				
			} catch (SQLException e) {
				System.out.println("Falha ao se comunicar com o Banco de Dados.");
				e.printStackTrace();
			}
		}
		
		
	}
}
