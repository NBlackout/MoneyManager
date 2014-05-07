package helpers.jsoup;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class JsoupConnection {

	public static Response execute(String url, Method method, String userAgent, Map<String, String> cookies, Map<String, String> data) {
		Response response = null;

		Connection connection = Jsoup.connect(url);
		connection.timeout(5000);
		connection.method(method);

		if (userAgent != null) {
			connection.userAgent(userAgent);
		}

		if (cookies != null) {
			connection.cookies(cookies);
		}

		if (data != null) {
			for (Entry<String, String> entry : data.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				connection.data(key, value);
			}
		}

		try {
			response = connection.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}
}
