package com.example.demo.restControllers;

import com.example.demo.dtos.CommentDto;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentsRestController {
    private final CommentService commentService;

    private boolean canModifyComment(Authentication auth, int commentId){
        if(auth==null )return false;
        if(hasRole(auth,"ADMIN"))return true;
        CommentDto commentDto=commentService.findByCommentId(commentId);
        if(commentDto!=null && commentDto.getEmail()!=null && commentDto.getEmail().equals(auth.getName()))return true;
        return false;
    }
    private boolean hasRole(Authentication auth, String role){
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_"+role));
    }

    @PostMapping("/addComment/{postId}")
    public ResponseEntity<?> addComment(
            @PathVariable int postId,
            @RequestBody CommentDto commentDto,
            Authentication auth
    ) {
        try {
            commentDto.setPostId(postId);
            if(auth!=null)commentDto.setEmail(auth.getName());
            commentService.createComment(commentDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Comment added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/updateComment")
    public ResponseEntity<?> updateComment(
            @RequestBody CommentDto commentDto,
            Authentication auth
    ) {
        System.out.println("Rest updateComment ctrl");
        try {
            if (!canModifyComment(auth, commentDto.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));
            }
            commentService.updateComment(commentDto);
            return ResponseEntity.ok(Map.of("message", "Comment updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(
            @RequestParam int commentId,
            @RequestParam int postId,
            Authentication auth
    ) {
        try {
            if (!canModifyComment(auth, commentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));
            }
            commentService.deleteCommentBycommentId(commentId);
            return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
