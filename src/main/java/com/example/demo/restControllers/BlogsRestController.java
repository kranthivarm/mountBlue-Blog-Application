package com.example.demo.restControllers;

import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.PagedResult;
import com.example.demo.dtos.PostDto;
import com.example.demo.dtos.UserDto;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blogPost")
public class BlogsRestController {
    private final int pageSize=5;
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    private boolean hasRole(Authentication auth, String role) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
    private boolean canModifyPost(Authentication auth, PostDto post) {
        if (auth == null) return false;
        if (hasRole(auth, "ADMIN")) return true;
        return hasRole(auth, "USER") && post.getAuthor().equals(auth.getName());
    }

    @GetMapping("/allblogs")
    public ResponseEntity<?> getAllBlogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(required = false, defaultValue = "publishedAt") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false, defaultValue = "1900-01-01") String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            Authentication auth
    ){
        try {
            page=Math.max(0,page);
            if(endDate==null || endDate.isEmpty())endDate= LocalDate.now().toString();

            LocalDate start= LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<String>selectedTagsList=
                    (tagNames!=null)?tagNames:new ArrayList<>();
            Map<String,Object>result=postService.getFilteredPosts(
                    search,authorName,selectedTagsList,
                    sortField,order,start,end,page,pageSize
            );
            result.put("allAuthors",postService.getAllAuthors());
            result.put("allTags",postService.getAllTags());

            if(auth!=null){
                result.put("currentUserEmail",auth.getName());
                result.put("isAdmin", hasRole(auth, "ADMIN"));
                result.put("isUser",  hasRole(auth, "USER"));
            }
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(Map.of("error",e.getMessage()));
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> viewPost(
            @PathVariable int postId,
            @RequestParam(defaultValue = "0") int commentPage,
            Authentication auth
    ) {
        try {
            PostDto postDto = postService.findById(postId);
            if (postDto == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Post not found"));

            PagedResult<CommentDto> commentResult =
                    commentService.findCommentsByPostId(postId, commentPage, pageSize);
            postDto.setComments(commentResult.getContent());

            Map<String, Object> response = new HashMap<>();
            response.put("post",               postDto);
            response.put("commentCurrentPage", commentResult.getCurrentPage());
            response.put("commentTotalPages",  commentResult.getTotalPages());
            response.put("commentTotalItems",  commentResult.getTotalItems());

            if (auth != null) {
                response.put("currentUserEmail", auth.getName());
                response.put("isAdmin",  hasRole(auth, "ADMIN"));
                response.put("isUser",   hasRole(auth, "USER"));
                response.put("isOwner",  postDto.getAuthor().equals(auth.getName()));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/newPost")
    public ResponseEntity<?> createNewPost(
            @RequestBody PostDto postDto,
            Authentication auth
    ) {
        try {
            if (!hasRole(auth, "ADMIN")) {
                postDto.setAuthor(auth.getName());
            } else {
                // admin — assign themselves if author left blank
                if (postDto.getAuthor() == null || postDto.getAuthor().isBlank()) {
                    postDto.setAuthor(auth.getName());
                }
            }

            PostDto inserted = postService.createNewPost(postDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(inserted);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateBlogPost(
            @RequestBody PostDto postDto,
            Authentication auth
    ) {
        try {
            PostDto existingPostDto = postService.findById(postDto.getId());
            if (existingPostDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Post not Found"));
            }
            if (!canModifyPost(auth, existingPostDto)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "UnAuthorised"));
            }
            if (!hasRole(auth, "ADMIN")) {
                postDto.setAuthor(auth.getName());
            }
            postService.updatePost(postDto);
            return ResponseEntity.ok(Map.of("message", "post updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/deletePost/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable int id,
            Authentication auth
    ) {
        try {
            PostDto post = postService.findById(id);
            if (post == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Post not found"));

            if (!canModifyPost(auth, post))
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));

            postService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
