package com.example.demo.controllers;

import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.PagedResult;
import com.example.demo.dtos.PostDto;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/blogPost")
public class BlogsController {
    private final int pageSize=5;
    private final PostService postService;
    private final CommentService commentService;
    @Autowired
    BlogsController(PostService postService,CommentService commentService){
        this.postService=postService;
        this.commentService=commentService;
    }

    @GetMapping("/")
    public String Homepage(){
        return "homePage";
    }
    //creation of new blog post
    @GetMapping("/blogCreationForm")
    public String postFormForNewPost(Model model){
        System.out.println("creation form controller");
        model.addAttribute("post",new PostDto());
        return "blogCreationForm";
    }

    @PostMapping("/newPost")
    public  String createNewPost(@ModelAttribute PostDto postDto, Model model){
        System.out.println(" new post controller");
        PostDto insertedInstance = postService.createNewPost(postDto);
        if(insertedInstance!=null){
            //redirecting to singlePOstPost
            return "redirect:/blogPost/"+insertedInstance.getId();
        }
        else return "redirect:/blogPost/blogCreationForm";
    }

    //all blogs + sorting // sortField="publishedAt"&order="asc"
    @GetMapping("/allblogs")//all posts
    public  String getAllBlogs(
            Model model,
            @RequestParam(required = false)String search,
            @RequestParam(required = false)String authorName,
            @RequestParam(required = false)String tagNames,
            @RequestParam(required = false, defaultValue = "publishedAt")String sortField,
            @RequestParam(required = false, value="order",defaultValue = "desc")String order,
            @RequestParam(required = false, defaultValue = "0") int page
    ){
        System.out.println("allBlogs controller"+search+authorName+tagNames);
        List<String>tagNamesList;
        if(tagNames!=null && !tagNames.isEmpty())tagNamesList=Arrays.asList(tagNames.split(","));
        else tagNamesList=new ArrayList<>();
        List<PostDto>postDtos;
        Map<String, Object> result =postService.getFilteredPosts(
                search,authorName,tagNamesList,sortField,order,page,pageSize
        );

        model.addAttribute("allPosts", result.get("posts"));
        model.addAttribute("currentPage", result.get("currentPage"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("totalItems", result.get("totalItems"));
        model.addAttribute("search",search);
        model.addAttribute("authorName",authorName);
        model.addAttribute("tagNames",tagNames);
        model.addAttribute("sortField",sortField);
        model.addAttribute("order",order);

        return "allBlogsPage";
    }

    //find by postId
    @GetMapping("/{postId}")
    public  String viewPost(
            @PathVariable int postId,
            @RequestParam(defaultValue = "0")int commentPage,
            @RequestParam(value = "commentId", defaultValue = "-1") int commentId,
            Model model
    ){


        System.out.println("single page controller");

        PostDto postDto=postService.findById(postId);
        PagedResult<CommentDto>commentResult=commentService.findCommentsByPostId(postId,commentPage,pageSize);
        postDto.setComments(commentResult.getContent());
        model.addAttribute("post",postDto);
        model.addAttribute("commentCurrentPage", commentResult.getCurrentPage() );
        model.addAttribute("commentTotalPages", commentResult.getTotalPages());
        model.addAttribute("updateCommentId",commentId);
        model.addAttribute("commentTotalItems", commentResult.getTotalItems());
        return "singlePostPage";
    }

    @GetMapping("/updatePostForm/{id}")
    public  String updatePostForm(@PathVariable String id, Model model){
        System.out.println("updateForm ctrlr");
        model.addAttribute("post",postService.findById(Integer.parseInt(id)));
        return "updatePostForm";
    }

    @PostMapping("/update")
    public String updateBlogPost(@ModelAttribute PostDto postDto){
        System.out.println(
                "updatePost ctrlr"+
                postDto.getTitle()+
                (postDto.getTags())
        );
        postService.updatePost(postDto);
        return "redirect:/blogPost/"+ postDto.getId();
    }

    //    @DeleteMapping("/deletePost/{id}")//browsers not supporting but we can
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable String id){
        System.out.println("deleteBy Id Ctrlr");
        postService.deleteById(Integer.parseInt(id));
        return "redirect:/blogPost/allblogs";
    }
//    //editing comment Form
//    @GetMapping("/editCommentForm")
//    public  String updateCommentById(
//            @RequestParam(value = "commentId") int commentId,
//            @RequestParam(value = "postId") int postId,
//            @RequestParam(value = "commentPage", defaultValue = "0")int commentPage,
//            Model model
//    ){
//        System.out.println("editCommentForm from blogCntr");
//        PostDto postDto=postService.findById(postId);
//        PagedResult<CommentDto>commentResult=commentService.findCommentsByPostId(postId,commentPage,pageSize);
//        postDto.setComments(commentResult.getContent());
//
//        model.addAttribute("post",postDto);
//        model.addAttribute("updateCommentId",commentId);
//        model.addAttribute("commentCurrentPage", commentResult.getCurrentPage() );
//        model.addAttribute("commentTotalPages", commentResult.getTotalPages());
//        model.addAttribute("commentTotalItems", commentResult.getTotalItems());
//        return "singlePostWithEditCommentForm";
//    }
}