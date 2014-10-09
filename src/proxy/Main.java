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
	
	public static void main(String[] args) {
		
		ListType type = null;
		boolean erro = false;
		List<String> blackOrWhiteList = null;
		
		for(String item: args){
			System.out.println(item);
		}
		
		if(args.length < 3){
			erro = true;
		}else{
			if(!args[0].equals("-b") && !args[0].equals("-w") && !args[3].matches("[0-9]*")){
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
					
					arquivo = new FileReader(new File(args[1]));
					leitor = new BufferedReader(arquivo);
					String aux = null;
					while(leitor.ready()){
						aux = leitor.readLine().trim();
						if(!"".equals(aux)){
							blackOrWhiteList.add(aux);
						}
					}
					
					proxy(blackOrWhiteList, type, new Integer(args[2]));
				} catch ( IOException e) {
					System.out.println("O arquivo " + args[1] + " nao foi encontrado");
				}
				
			}
		}
		
		if(erro){
			System.out.println("Uso indevido do programa! \n"
					+ "Deve ser usado como: java -jar proxy.jar <-b ou -r> <path para a black ou white list> <porta do proxy>");
		}
		
	}
	
	public static void proxy(List<String> blackOrWhiteList, ListType type, int porta){
		
		ServerSocket proxy = null;
		
		boolean ok = true;
		
		try {
			proxy = new ServerSocket(porta);
		    System.out.println("Porta "+porta+" aberta!");
		} catch (IOException e) {
			ok = false;
		    System.out.println("Nao foi possivel abria a porta "+porta+"!");
		}
		
	    while(ok){
	    	try {
				Socket requisicao = proxy.accept();
				TrataRequisicao tratamento = new TrataRequisicao(requisicao, blackOrWhiteList, type);

				(new Thread(tratamento)).start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
	}

}
