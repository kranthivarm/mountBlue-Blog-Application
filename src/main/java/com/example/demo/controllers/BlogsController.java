package com.example.demo.controllers;

import com.example.demo.models.PostModel;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blogPost")
public class BlogsController {
    private final PostService postService;
    @Autowired
    BlogsController(PostService postService){
        this.postService=postService;
    }

    //all blogs + sorting
    @GetMapping("/allblogs")//all posts
    public  String getAllBlogs(Model model, @RequestParam(value = "sort", defaultValue = "false") String sort){
        System.out.println("all blogs controller");
        if(sort.equals("false")) model.addAttribute("allPosts",postService.findAll());
        else model.addAttribute("allPosts",postService.findAllOrderByPublishedAt());

        return "allBlogsPage";
    }

    @GetMapping("/{id}")
    public  String viewPost(@PathVariable String id,Model model ){
        System.out.println("single page controller");
        model.addAttribute("post",postService.findById(Integer.parseInt(id)));
        return "singlePostPage";
    }

    //creation of new blog post
    @GetMapping("/blogCreationForm")
    public String postFormForNewPost(Model model){
        System.out.println("creation form controller");
        model.addAttribute("post",new PostModel());
        return "blogCreationForm";
    }

    @PostMapping("/newPost")
    public  String createNewPost(@ModelAttribute PostModel postModel,Model model){
        System.out.println(" new post controller");
        PostModel insertedInstance = postService.createNewPost(postModel);
        if(insertedInstance!=null){
            //redirecting to singlePOstPost
            return "redirect:/blogPost/"+insertedInstance.getId();
        }
        else return "redirect:/blogPost/blogCreationForm";
    }

//    @DeleteMapping("/deletePost/{id}")//browsers not supporting but we can
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable String id){
        System.out.println("deleteBy Id Ctllr");
        postService.deleteById(Integer.parseInt(id));
        return "redirect:/blogPost/allblogs";
    }

    @PostMapping("/updatePostForm/{id}")
    public  String updatePostForm(@PathVariable String id, Model model){
        model.addAttribute("post",postService.findById(Integer.parseInt(id)));
        return "updatePostForm";
    }


}
