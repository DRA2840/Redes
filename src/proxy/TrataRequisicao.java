package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class TrataRequisicao implements Runnable{

	private static final String IGNORED_EXTENTIONS = "tif, tiff, gif, jpeg, jpg, jif, jfif, jp2, jpx, j2k, j2c, fpx, pcd, png, ico, css, js";
	
	private Socket requisicao;
	private ListType tipo;
	private List<String> blackOrWhiteList;
	
	public TrataRequisicao(Socket requisicao, List<String> blackOrWhiteList, ListType tipo){
		this.requisicao = requisicao;
		this.blackOrWhiteList = blackOrWhiteList;
		this.tipo = tipo;
	}

	@Override
	public void run() {

		System.out.println(requisicao.toString());
		try {
			OutputStream output  = requisicao.getOutputStream();
			
			String[] hostAndMethod = getUrlandMethodDestino(requisicao);
			
			URL serveradress = new URL(hostAndMethod[0]);
			HttpURLConnection connection= (HttpURLConnection)serveradress.openConnection();
			connection.setRequestMethod(hostAndMethod[1]);
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);
			connection.connect();
			
			output.write(IOUtils.toByteArray(connection.getInputStream()));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public String[] getUrlandMethodDestino(Socket requisicao) throws IOException{
		BufferedReader recebido_browser = new BufferedReader(new InputStreamReader(requisicao.getInputStream()));
        String mensagem = (String)recebido_browser.readLine();

        String[] splited = mensagem.split(" ");
        
        return new String[] {splited[1], splited[0]};
	}
	
	public boolean isValidUrl(String url){
		
		if(tipo.equals(ListType.BLACK_LIST)){
			
		}else{
			
		}
		
		return true;
	}
}
