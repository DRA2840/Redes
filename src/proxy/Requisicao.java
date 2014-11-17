package proxy;

/**
 * Objeto que representa uma requisicao. Encapsula diferentes elementos que serao gravados no BD.
 * 
 * @author <img src="https://avatars2.githubusercontent.com/u/3778188?v=2&s=30" width="30" height="30" /> <a href="https://github.com/DRA2840" target="_blank"> DRA2840 </a>
 *
 */
public class Requisicao {
	
	private String ip;
	private long delay;
	private String url;
	private Boolean blocked;
	
	/**
	 * Getter de IP
	 * 
	 * @return IP que fez a requisicao
	 */
	public String getIp() {
		return ip;
	}
	
	/**
	 * Setter de IP
	 * 
	 * @param ip IP que fez a requisicao
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	/**
	 * Getter de Delay
	 * 
	 * @return Delay (tempo gasto para receber a requisicao)
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Setter de Delay
	 * 
	 * @param delay Delay (tempo gasto para receber a requisicao)
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	/**
	 * Getter de URL
	 * 
	 * @return URL que foi requisitada
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Getter de URL
	 * 
	 * @param url URL que foi requisitada
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Getter de blocked
	 * 
	 * @return true se a URL foi bloqueada, false caso contrario
	 */
	public Boolean isBlocked() {
		return blocked;
	}
	
	/**
	 * Setter de blocked
	 * 
	 * @param blocked true se a URL foi bloqueada, false caso contrario
	 */
	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	
	
	
}
