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
import java.util.List;

import org.apache.commons.io.IOUtils;

public class TrataRequisicao implements Runnable{

	private static final String IGNORED_EXTENTIONS = "tif, tiff, gif, jpeg, jpg, jif, jfif, jp2, jpx, j2k, j2c, fpx, pcd, png, ico, css, js";
	
	private Socket requisicao;
	private ListType tipo;
	private List<String> blackOrWhiteList;
	
	public TrataRequisicao(Socket requisicao, List<String> blackOrWhiteList, ListType tipo) throws IOException{
		this.requisicao = requisicao;
		this.blackOrWhiteList = blackOrWhiteList;
		this.tipo = tipo;
	}

	@Override
	public void run() {

		System.out.println(requisicao.toString());
		try {
			OutputStream req_server  = requisicao.getOutputStream();
			req_server.flush();
			String[] hostAndMethod = getUrlandMethodDestino(requisicao);
			
			System.out.println(hostAndMethod[0]);
			
			URL serveradress = new URL(hostAndMethod[0]);
			HttpURLConnection connection= (HttpURLConnection)serveradress.openConnection();
			connection.setRequestMethod(hostAndMethod[1]);
			connection.setDoOutput(true);
			connection.connect();
			
			InputStream res_server = connection.getInputStream();
			
			if(isValidUrl(hostAndMethod[0])){
				
				req_server.write(IOUtils.toByteArray(res_server));
				req_server.flush();
				requisicao.close();
			}else{
				req_server.flush();
				requisicao.close();
				
				String file = hostAndMethod[0].replace("http://", "").replace("/", ".");
				if(file.endsWith(".")){
					file = file + "html";
				}
				
				FileOutputStream out = new FileOutputStream("blockedPages/" + file);
				
				out.write(IOUtils.toByteArray(res_server));
				
				
				IOUtils.closeQuietly(out);
			}
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private synchronized String[] getUrlandMethodDestino(Socket requisicao) throws IOException{
		BufferedReader recebido_browser = new BufferedReader(new InputStreamReader(requisicao.getInputStream()));
        String mensagem  = recebido_browser.readLine();

        String[] splited = mensagem.split(" ");
        
        return new String[] {splited[1], splited[0]};
	}
	
	private boolean isValidUrl(String url){
		
		if(tipo.equals(ListType.BLACK_LIST)){
			
		}else{
			
		}
		
		if(url.startsWith("http://www.unb.br")){
			System.out.println("URL bloqueada: "+ url);
			return false;
		}
		
		return true;
	}
}
