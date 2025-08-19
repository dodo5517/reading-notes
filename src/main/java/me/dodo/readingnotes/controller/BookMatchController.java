package me.dodo.readingnotes.controller;


import me.dodo.readingnotes.dto.book.BookCandidate;
import me.dodo.readingnotes.service.BookCandidateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookMatchController {

    private final BookCandidateService candidateService;

    public BookMatchController(BookCandidateService candidateService) {
        this.candidateService = candidateService;
    }
    
    // 후보를 조회함
    @GetMapping("/candidates")
    public List<BookCandidate> candidates(@RequestParam String rawTitle,
                                          @RequestParam(required = false) String rawAuthor,
                                          @RequestParam(defaultValue = "10") int limit) {
        return candidateService.findCandidates(rawTitle, rawAuthor, limit);
    }
}
