package pe.edu.vallegrande.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.app.entity.ModerationResult;
import pe.edu.vallegrande.app.service.ContentModeratorService;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@CrossOrigin(origins = "*")
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

    @PostMapping("/crear")
    public Mono<ModerationResult> crear(@RequestBody ModerationResult moderationResult) throws Exception {
        return contentModeratorService.crear(moderationResult);
    }

    @PutMapping("/update/{id}")
    public Mono<ModerationResult> update(@PathVariable Integer id, @RequestBody ModerationResult moderationResult) {
        return contentModeratorService.update(id, moderationResult);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return contentModeratorService.delete(id);
    }
    
    @PutMapping("/reactivate/{id}")
    public Mono<Void> reactivate(@PathVariable Integer id) {
        return contentModeratorService.reactivate(id);
    }

    @GetMapping("/hello")
    public Mono<String> helloWorld() {
        return Mono.just("Hello, World!");
    }
    
    @GetMapping("/{id}")
    public Mono<ModerationResult> getById(@PathVariable Integer id) {
        return contentModeratorService.getById(id);
    }
    
    @GetMapping("/active")
    public Flux<ModerationResult> getActive() {
        return contentModeratorService.findByActiveTrue();
    }

    @GetMapping("/inactive")
    public Flux<ModerationResult> getInactive() {
        return contentModeratorService.findByInactiveTrue();
    }
}