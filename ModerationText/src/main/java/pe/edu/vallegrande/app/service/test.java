package pe.edu.vallegrande.app.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pe.edu.vallegrande.app.entity.ModerationResult;
import org.json.JSONObject;

@Slf4j
public class test {

    public static void main(String[] args) throws Exception {
        save();
    }

    public static void save() throws Exception {
        ModerationResult moderationResult = new ModerationResult();

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType,
                "Is this a crap email abcdef@abcd.com, phone: 6657789887, IP: 255.255.255.255, 1 Microsoft Way, Redmond, WA 98052");
        Request request = new Request.Builder()
                .url("https://eastus.api.cognitive.microsoft.com/contentmoderator/moderate/v1.0/ProcessText/Screen?classify=True")
                .method("POST", body)
                .addHeader("Host", "eastus.api.cognitive.microsoft.com")
                .addHeader("Content-Type", "text/plain")
                .addHeader("Ocp-Apim-Subscription-Key", "c99a2eed86824caa91af9408c2bf979f")
                .build();
        Response response = client.newCall(request).execute();

        JSONObject jsonObject = new JSONObject(response.body().string());

        moderationResult.setOriginalText(jsonObject.getString("OriginalText"));
        moderationResult.setNormalizedText(jsonObject.getString("NormalizedText"));

        log.info("Mostrando JSON = " + jsonObject);
        log.info("ORIGINAL TEXT = " + moderationResult.getOriginalText());
        log.info("NORMALIZED TEXT = " + moderationResult.getNormalizedText());
    }
}
