package me.dodo.readingnotes.controller;

import jakarta.validation.Valid;
import me.dodo.readingnotes.dto.book.LinkBookRequest;
import me.dodo.readingnotes.service.BookLinkService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/records")
public class RecordLinkController {

    private final BookLinkService bookLinkService;

    public RecordLinkController(BookLinkService bookLinkService) {
        this.bookLinkService = bookLinkService;
    }

    // 매칭 확정
    @PostMapping("/{id}/link")
    public Boolean link(@PathVariable Long id, @RequestBody @Valid LinkBookRequest req) {
        bookLinkService.linkRecord(id, req);
        return true;
    }

    // 매핑 정보 삭제
    @PostMapping("/{id}/remove")
    public Boolean remove(@PathVariable Long id) {
        bookLinkService.removeBookMatch(id);
        return true;
    }
}
