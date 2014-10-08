package proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	private static final String IGNORED_EXTENTIONS = "tif, tiff, gif, jpeg, jpg, jif, jfif, jp2, jpx, j2k, j2c, fpx, pcd, png, ico, css, js";
	
	private enum ListType{
		BLACK_LIST,
		WHITE_LIST;
	}
	
	
	
	public static void main(String[] args) {
		
		ListType type = null;
		boolean erro = false;
		List<String> blackOrWhiteList = null;
		
		for(String item: args){
			System.out.println(item);
		}
		
		if(args.length < 2){
			erro = true;
		}else{
			if(!args[0].equals("-b") && !args[0].equals("-w")){
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
					
					proxy(blackOrWhiteList, type);
				} catch ( IOException e) {
					System.out.println("O arquivo " + args[1] + " nao foi encontrado");
				}
				
			}
		}
		
		if(erro){
			System.out.println("Uso indevido do programa! \n"
					+ "Deve ser usado como: java -jar proxy.jar <-b ou -r> <path para a black ou white list> ");
		}
		
	}
	
	public static void proxy(List<String> blackOrWhiteList, ListType type){
		
		if(type.equals(ListType.BLACK_LIST)){
			System.out.println("This is my black list: ");
		}else{
			System.out.println("This is my white list: ");
		}
		
		for(String item: blackOrWhiteList){
			System.out.println(item);
		}
		
		
	}

}
