package pe.edu.vallegrande.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pe.edu.vallegrande.app.entity.ModerationResult;
import pe.edu.vallegrande.app.repository.ModerationResultRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.json.JSONObject;

@Slf4j
@Service
public class ContentModeratorService {

	private final ModerationResultRepository moderationResultRepository;
	
	@Autowired
    public ContentModeratorService(ModerationResultRepository moderationResultRepository) {
        this.moderationResultRepository = moderationResultRepository;
    }

    public Flux<ModerationResult> getAll() {
        log.info("Mostrando datos");
        return moderationResultRepository.findAll();
    }

    public Mono<ModerationResult> save(ModerationResult moderationResult) throws Exception {
        moderationResult.setConsultText("Is this a crap email abcdef@abcd.com, phone: 6657789887, IP: 255.255.255.255, 1 Microsoft Way, Redmond, WA 98052");
    	//"Is this a crap email abcdef@abcd.com, phone: 6657789887, IP: 255.255.255.255, 1 Microsoft Way, Redmond, WA 98052"
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        @SuppressWarnings("deprecation")
        RequestBody body = RequestBody.create(mediaType, moderationResult.getConsultText());

        Request request = new Request.Builder()
                .url("https://eastus.api.cognitive.microsoft.com/contentmoderator/moderate/v1.0/ProcessText/Screen?classify=True")
                .method("POST", body)
                .addHeader("Host", "eastus.api.cognitive.microsoft.com")
                .addHeader("Content-Type", "text/plain")
                .addHeader("Ocp-Apim-Subscription-Key", "4a8ac4c3ddea41eab72e59289566934d")
                .build();
        Response response = client.newCall(request).execute();

        JSONObject jsonObject = new JSONObject(response.body().string());
        //moderationResult.setId(1);
        moderationResult.setOriginalText(jsonObject.getString("OriginalText"));
        moderationResult.setNormalizedText(jsonObject.getString("NormalizedText"));
        moderationResultRepository.save(moderationResult);

        log.info("Mostrando JSON " + jsonObject);
        log.info("Mostrando ORIGINAL TEXT " + moderationResult.getOriginalText());
        log.info("Mostrando NORMALIZED TEXT " + moderationResult.getNormalizedText());
        log.info("Mostrando MODELO " + moderationResult.toString());

        return moderationResultRepository.save(moderationResult);
    }

}
