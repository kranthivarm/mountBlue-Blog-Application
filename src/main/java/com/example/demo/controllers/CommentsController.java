package com.example.demo.controllers;

import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.PostDto;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/addComment/{postId}")
    public String newCommentCreation(
         @PathVariable int postId,
         @ModelAttribute CommentDto commentDto
     ){
        System.out.println("comment ctrl adding"+commentDto);
        commentDto.setPostId(postId);
        commentService.createComment(commentDto);
        return "redirect:/blogPost/"+postId;
    }

    @PostMapping("/updateComment")
    public String updateComment(@ModelAttribute CommentDto commentDto, Authentication auth){
        System.out.println("comment update ctrl"+commentDto);
        if(!canModifyComment(auth,commentDto.getPostId())){
            return "redirect:/blogPost/" + commentDto.getPostId() + "?error=unauthorized";
        }
        commentService.updateComment(commentDto);
        return "redirect:/blogPost/"+commentDto.getPostId();
    }

    @GetMapping("/deleteComment")//commentId and postId @requestParams
    public String deleteComment(
         @RequestParam(value = "commentId") int commentId,
         @RequestParam(value = "postId") int postId,
         Authentication auth
    ){
        System.out.println("Comment delete cntrl");
        if(!canModifyComment(auth,postId)){
            return "redirect:/blogPost/" + postId + "?error=unauthorized";
        }
        commentService.deleteCommentBycommentId(commentId);
        return "redirect:/blogPost/"+postId;
    }

    private boolean canModifyComment(Authentication auth, int postId) {
        if (auth == null) return false;
        if (hasRole(auth, "ADMIN")) return true;
        PostDto post = postService.findById(postId);
        return post.getAuthor().equals(auth.getName());
    }
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
