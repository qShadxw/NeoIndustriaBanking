package uk.co.tmdavies.nibanking.files;

import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import uk.co.tmdavies.nibanking.NIBanking;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ConfigFile {

    private final String path;
    private final String fileName;
    private File file;

    private JsonObject jsonObj;

    public ConfigFile(String name) {
        if (!name.endsWith(".json")) {
            name = name + ".json";
        }

        this.path = "./config/nibanking";
        this.fileName = name;
        this.file = new File(this.path + "/" + this.fileName);

        checkDir();
        checkFile();
    }

    public void checkDir() {
        File dir = new File(this.path);

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void checkFile() {
        if (file.exists()) {
            return;
        }

        try {
            file.createNewFile();
        } catch (IOException exception) {
            NIBanking.LOGGER.error("Error creating config file.");
            exception.printStackTrace();
        }
    }

    public void loadConfig() {
        this.file = new File(this.path + "/" + this.fileName);

        if (file.length() == 0) {
            switch (this.fileName) {
                case "config.json" -> setDefaultsForConfig();
                case "connections.json" -> setDefaultsForConnections();
            }
        }

        try (FileInputStream inputStream = new FileInputStream(this.path + "/" + this.fileName)) {
            this.jsonObj = JsonParser.parseString(IOUtils.toString(inputStream, Charset.defaultCharset())).getAsJsonObject();
        } catch (IOException e) {
            NIBanking.LOGGER.error("Error loading config file. Continuing to create new...");
        }
    }

    public void setDefaultsForConnections() {
        JsonObject connectionObject = new JsonObject();

        JsonObject neoNetworkIRSObj = new JsonObject();
        neoNetworkIRSObj.addProperty("apikey", "01189998819991197253");

        JsonObject webSocketObj = new JsonObject();
        webSocketObj.addProperty("url", "ws://ws.example.com:443");

        connectionObject.add("NeoNetworkIRS", neoNetworkIRSObj);
        connectionObject.add("WebSocket", webSocketObj);

        pushToFile(connectionObject);
    }

    public void setDefaultsForConfig() {
        // Main Object
        JsonObject configObject = new JsonObject();

        pushToFile(configObject);
    }

    public void pushToFile(JsonObject objectToPush) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(this.path + "/" + this.fileName)) {
            gson.toJson(objectToPush, writer);
        } catch (IOException exception) {
            NIBanking.LOGGER.error("Failed to write {} file defaults. \n{}", this.fileName, exception);
        }
    }

    public JsonObject getConfig() {
        if (jsonObj == null) {
            NIBanking.LOGGER.error("Config was not loaded before getting.");

            return null;
        }

        return jsonObj;
    }

    public JsonObject get(String path) {
        if (jsonObj == null) {
            NIBanking.LOGGER.error("Config was not loaded before grabbing data.");

            return null;
        }

        return jsonObj.getAsJsonObject(path);
    }

    public JsonElement getElement(String path) {
        if (jsonObj == null) {
            NIBanking.LOGGER.error("Config was not loaded before grabbing data.");

            return null;
        }

        return jsonObj.get(path);
    }

    public boolean isModEnabled() {
        if (jsonObj == null) {
            NIBanking.LOGGER.error("Config was not loaded before grabbing data.");

            return true;
        }

        return jsonObj.get("Enabled").getAsBoolean();
    }

    public void verboseConfig() {
        if (jsonObj == null) {
            return;
        }

        JsonObject questsObject = jsonObj.getAsJsonObject("Quests");

        NIBanking.LOGGER.info("Config Details:");

        for (String key : jsonObj.keySet()) {
            NIBanking.LOGGER.info("Root: {}", key);
        }

        for (String key : questsObject.keySet()) {
            NIBanking.LOGGER.info("Quest: {}", key);
        }

        List<JsonObject> sectionObjects = new ArrayList<>();

        for (String key : questsObject.keySet()) {
            JsonObject sectionObject = questsObject.getAsJsonObject(key);

            sectionObjects.add(sectionObject);

            for (String key2 : sectionObject.keySet()) {

                NIBanking.LOGGER.info("{}: {}: {}", key, key2, sectionObject.get(key2));
            }
        }

        for (JsonObject sectionObject : sectionObjects) {
            for (String key : sectionObject.keySet()) {
                if (key.equals("Weight")) {
                    continue;
                }

                JsonObject questObject = sectionObject.getAsJsonObject(key);

                for (String key2 : questObject.keySet()) {
                    NIBanking.LOGGER.info("{}: {}: {}", key, key2, questObject.get(key2));
                }
            }
        }

    }

}

