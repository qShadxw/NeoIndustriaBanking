package uk.co.tmdavies.nibanking.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.entity.player.Player;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.utils.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NeoNetworkIRS {

    private static final String endPoint = "https://irs.neonetwork.xyz/api/";
    private static final String LINE_END = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "*****"; // Change this string to a unique boundary
    private final String apiKey;

    public NeoNetworkIRS(String apiKey) {
        this.apiKey = apiKey;
    }

    public void constructRequest(HttpURLConnection connection, HashMap<String, String> data) throws IOException {
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(twoHyphens + boundary + LINE_END);
            wr.writeBytes("Content-Disposition: form-data; name=\"apikey\"" + LINE_END);
            wr.writeBytes(LINE_END);
            wr.writeBytes(this.apiKey + LINE_END);

            for (Map.Entry<String, String> entry : data.entrySet()) {
                wr.writeBytes(twoHyphens + boundary + LINE_END);
                wr.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                wr.writeBytes(LINE_END);
                wr.writeBytes(entry.getValue() + LINE_END);
            }
        }
    }

    public JsonObject doRequest(String endPointSpec, HashMap<String, String> data) {
        try {
            URL obj = URI.create(endPoint + endPointSpec).toURL();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setDoOutput(true);

            constructRequest(con, data);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            NIBanking.LOGGER.info(response.toString());

            return JsonParser.parseString(response.toString()).getAsJsonObject();
        } catch (IOException exception) {
            NIBanking.LOGGER.error("Unable to complete request [{}]", data.toString());
            NIBanking.LOGGER.error("Exception: {}", exception.toString());

            return null;
        }
    }

    public void sendMoney(Player target, int amount, String ref) {
        HashMap<String, String> data = new HashMap<>() {{
            put("to", target.getName().toString());
            put("amount", String.valueOf(amount));
            put("reference", ref);
        }};
        JsonObject response = doRequest("send", data);

        target.sendSystemMessage(Utils.Chat("&a+£%s", amount));
        target.sendSystemMessage(Utils.Chat("&aRef: %s", ref));
    }

    public void requestMoney(String shopKeeper, Player customer, int amount, String ref) {
        HashMap<String, String> data = new HashMap<>() {{
            put("to", shopKeeper);
            put("from", customer.getName().toString());
            put("amount", String.valueOf(amount));
            put("reference", ref);
        }};
        JsonObject response = doRequest("request", data);

        if (response.get("success").getAsBoolean()) {
//            shopKeeper.sendSystemMessage(Utils.Chat("Requested money from %s. Amount: %d. Transaction ID: %s",
//                    customer.getName().toString(), amount, response.get("data").getAsJsonObject().get("txID").getAsString()));
            customer.sendSystemMessage(Utils.Chat("Sending money to %s. Amount: %d. Transaction ID: %s",
                    shopKeeper, amount, response.get("data").getAsJsonObject().get("txID").getAsString()));
        }
    }

    public boolean hasCompleted(String transactionId) {
        // Check if transaction is complete
        return false;
    }
}
