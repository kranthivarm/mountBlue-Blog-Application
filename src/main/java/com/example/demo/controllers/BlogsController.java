package com.example.demo.controllers;

import com.example.demo.dtos.PostDto;
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
            @RequestParam(value = "sortField", defaultValue = "false") String sort,
            @RequestParam(value="order",defaultValue = "asc")String order
    ){
        System.out.println("all blogs controller");
        if(!sort.equals("publishedAt")) model.addAttribute("allPosts",postService.findAll());
        else model.addAttribute("allPosts",postService.findAllOrderByPublishedAt(order));
        return "allBlogsPage";
    }

    //find by id
    @GetMapping("/{id}")
    public  String viewPost(@PathVariable String id,Model model ){
        System.out.println("single page controller");
        model.addAttribute("post",postService.findById(Integer.parseInt(id)));
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
        System.out.println("updatePost ctrlr");
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
}
