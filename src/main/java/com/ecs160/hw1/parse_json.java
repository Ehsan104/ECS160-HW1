package com.ecs160.hw1;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;

public class parse_json {
    public void parseJson() {
        JsonElement element = JsonParser.parseReader(new InputStreamReader(
                parse_json.class.getClassLoader().getResourceAsStream("input.json")));

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            JsonArray feedArray = jsonObject.get("feed").getAsJsonArray();
            for (JsonElement feedObject : feedArray) {
                if (feedObject.getAsJsonObject().has("thread")) {
                    // Parse the post and any replies recursively
                }
            }
        }
    }
}
