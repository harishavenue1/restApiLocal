
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson; 
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject; 

public class RestApiMaxTransfer {
	
	static String endPoint = "https://jsonmock.hackerrank.com/api/transactions";
	
	public static void main(String[] args) throws IOException {
//		Scanner sc = new Scanner(System.in);
//		String title = sc.next();
//		sc.close();

		List<String> titles = getPatientDetails("");
		titles.forEach(System.out::println);
	}
	
	private static List<String> getPatientDetails(String subtitle) throws IOException {
		List<String> titles = new ArrayList<>();
		int page = 1;
		int totalPage = 1;
		String response;

		while (page <= totalPage) {
			URL obj = new URL(endPoint + "?page=" + page);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			while ((response = in.readLine()) != null) {
				System.out.println(response);

				JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
				totalPage = jsonResponse.get("total_pages").getAsInt();
				JsonArray data = jsonResponse.getAsJsonArray("data");

				for (JsonElement e : data) {
					String title = e.getAsJsonObject().get("amount").getAsString();
					titles.add(title);
				}
				break;
			}
			
			page++;
			break;
		}
		return titles;
	}
}

