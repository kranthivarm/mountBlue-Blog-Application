package com.example.demo.controllers;


import com.example.demo.dtos.CommentDto;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentsController {
    private final CommentService commentService;
    @Autowired
    CommentsController(CommentService commentService){
        this.commentService=commentService;
    }
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

    @GetMapping("/editCommentForm/{commentId}")
    public String editCommentById(@PathVariable int commentId, Model model){
        System.out.println("coment edit ctrl");
        CommentDto commentDto=commentService.findByCommentId(commentId);
        model.addAttribute("comment",commentDto);
        return "editCommentForm";
    }
    @PostMapping("/updateComment")
    public String updateComment(@ModelAttribute CommentDto commentDto){
        System.out.println("comment update ctrl");
        commentService.updateComment(commentDto);
        return "redirect:/blogPost/"+commentDto.getPostId();
    }

}
