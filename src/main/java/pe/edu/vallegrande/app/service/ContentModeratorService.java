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

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ContentModeratorService {

    private final ModerationResultRepository moderationResultRepository;

    private static final List<String> FORBIDDEN_WORDS = Arrays.asList("fucking", "shit", "damn", "crap", "bitch", "hell", "bullshit", "kill", "suck my dick", "fuck", "mierda", "carajo", "demonios");

    @Autowired
    public ContentModeratorService(ModerationResultRepository moderationResultRepository) {
        this.moderationResultRepository = moderationResultRepository;
    }

    public Mono<ModerationResult> crear(ModerationResult moderationResult) {
        moderationResult.setConsultText("Is this a crap email abcdef@abcd.com, phone: 6657789887, IP: 255.255.255.255, 1 Microsoft Way, Redmond, WA 98052");
        return updateModerationText(moderationResult);
    }

    public Flux<ModerationResult> getAll() {
        log.info("Mostrando datos");
        return moderationResultRepository.findAll();
    }

    public Mono<ModerationResult> save(ModerationResult moderationResult) throws Exception {
        moderationResult.setConsultText("Is this a crap email abcdef@abcd.com, phone: 6657789887, IP: 255.255.255.255, 1 Microsoft Way, Redmond, WA 98052");

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, moderationResult.getConsultText());

        Request request = new Request.Builder()
                .url("https://eastus.api.cognitive.microsoft.com/contentmoderator/moderate/v1.0/ProcessText/Screen?classify=True")
                .method("POST", body)
                .addHeader("Host", "eastus.api.cognitive.microsoft.com")
                .addHeader("Content-Type", "text/plain")
                .addHeader("Ocp-Apim-Subscription-Key", "8f913e4d4ed941e4ad170e822f62c95b")
                .build();

        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());

            moderationResult.setOriginalText(jsonObject.getString("OriginalText"));

            return updateModerationText(moderationResult);
        } catch (Exception e) {
            log.error("Error al procesar el texto moderado: " + e.getMessage());
            return Mono.error(e);
        }
    }

    public Mono<ModerationResult> update(Integer id, ModerationResult moderationResult) {
        return moderationResultRepository.findById(id)
                .flatMap(existingResult -> {
                    // Actualiza el texto original con el valor proporcionado
                    existingResult.setOriginalText(moderationResult.getOriginalText());
                    
                    // Establece los valores de normalizedText y consultText mediante el servicio de moderación
                    return updateModerationText(existingResult);
                })
                .onErrorResume(e -> {
                    log.error("Error al actualizar el texto moderado: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    private Mono<ModerationResult> updateModerationText(ModerationResult moderationResult) {
        try {
            // Normaliza el texto eliminando la censura
            String normalizedText = moderationResult.getOriginalText();
            for (String word : FORBIDDEN_WORDS) {
                normalizedText = normalizedText.replaceAll("(?i)" + word, ""); // (?i) hace la búsqueda case-insensitive
            }

            // Establece normalizedText sin censura y consultText con el texto original
            moderationResult.setNormalizedText(normalizedText.trim());
            moderationResult.setConsultText(moderationResult.getOriginalText());
            moderationResult.setStatus("A");

            return moderationResultRepository.save(moderationResult);
        } catch (Exception e) {
            log.error("Error al actualizar el texto moderado: " + e.getMessage());
            return Mono.error(e);
        }
    }

    public Mono<Void> delete(Integer id) {
        return moderationResultRepository.findById(id)
                .flatMap(existingResult -> {
                    existingResult.setStatus("I"); // Marcado como inactivo
                    return moderationResultRepository.save(existingResult).then();
                });
    }

    public Mono<Void> reactivate(Integer id) {
        return moderationResultRepository.findById(id)
                .flatMap(existingResult -> {
                    existingResult.setStatus("A"); // Cambiar el estado a activo
                    return moderationResultRepository.save(existingResult).then();
                });
    }

    public Flux<ModerationResult> findByActiveTrue() {
        return moderationResultRepository.findByActiveTrue();
    }
}
