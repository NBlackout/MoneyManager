package helpers.jsoup;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class JsoupConnection {

	public static Response get(String url, Map<String, String> cookies) {
		return execute(url, Method.GET, cookies, null);
	}

	public static Response post(String url, Map<String, String> cookies, Map<String, String> data) {
		return execute(url, Method.POST, cookies, data);
	}

	private static Response execute(String url, Method method, Map<String, String> cookies, Map<String, String> data) {
		Response response = null;

		Connection connection = Jsoup.connect(url);
		connection.method(method);

		connection.timeout(10000);
		connection.ignoreContentType(true);
		connection.maxBodySize(0);

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
