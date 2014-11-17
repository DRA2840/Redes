package proxy;

/**
 * POJO que representa uma requisicao.
 * @author diego
 *
 */
public class Requisicao {
	
	private String ip;
	private long delay;
	private String url;
	private Boolean blocked;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public long getDelay() {
		return delay;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	
	
	
}
