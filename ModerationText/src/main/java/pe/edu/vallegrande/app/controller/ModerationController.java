package pe.edu.vallegrande.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.app.entity.ModerationResult;
import pe.edu.vallegrande.app.service.ContentModeratorService;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/moderation")
public class ModerationController {

    private final ContentModeratorService contentModeratorService;

    @Autowired
    public ModerationController(ContentModeratorService contentModeratorService) {
        this.contentModeratorService = contentModeratorService;
    }

    @GetMapping()
    public Flux<ModerationResult> getAll() {
        return contentModeratorService.getAll();
    }
    
    @PostMapping("/save")
    public Mono<ModerationResult> save(@RequestBody ModerationResult moderationResult) throws Exception {
        return contentModeratorService.save(moderationResult);
    }

    @PutMapping("/update/{id}")
    public Mono<ModerationResult> update(@PathVariable Integer id, @RequestBody ModerationResult moderationResult) {
        return contentModeratorService.update(id, moderationResult);
    }
    
    @DeleteMapping("/delete/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return contentModeratorService.delete(id);
    }

}