package helpers.jsoup.data;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;

import org.jsoup.Connection.Method;

import play.db.jpa.Model;

@MappedSuperclass
public class AccessData extends Model {

	public String url;
	
	public Method method;
	
	@ElementCollection
	public Map<String, String> staticData;
	
	protected AccessData(String url, Method method, Map<String, String> staticData) {
		this.url=url;
		this.method=method;
		this.staticData = staticData;
	}
}
