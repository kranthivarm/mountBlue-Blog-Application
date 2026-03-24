package com.example.demo.controllers;

import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.PagedResult;
import com.example.demo.dtos.PostDto;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public String postFormForNewPost(Model model, Authentication auth){
        System.out.println("creation form controller");

        PostDto postDto=new PostDto();
//        postDto.setAuthor(auth.getName());//author/admin name
        if(!hasRole(auth,"ADMIN")){//default is User so assigning Admin
            postDto.setAuthor(auth.getName());
        }
        model.addAttribute("post",postDto);
        model.addAttribute("isAdmin",hasRole(auth,"ADMIN"));

        return "blogCreationForm";
    }

    @PostMapping("/newPost")
    public  String createNewPost(@ModelAttribute PostDto postDto, Model model,Authentication auth){
        System.out.println(" new post controller");

        //non Admin
        if(!hasRole(auth,"ADMIN"))postDto.setAuthor(auth.getName());
        else {
            // when author provided is null or empty
            if (postDto.getAuthor() == null || postDto.getAuthor().isBlank()) {
                postDto.setAuthor(auth.getName()); //  admin email as author
            }
        }

        try {
            PostDto insertedInstance = postService.createNewPost(postDto);
            if (insertedInstance != null) {
                //redirecting to singlePOstPost
                return "redirect:/blogPost/" + insertedInstance.getId();
            }
        }catch (Exception e){
            model.addAttribute("post", postDto);
            model.addAttribute("isAdmin", true);
            model.addAttribute("error", e.getMessage());
            return "blogCreationForm";
        }
         return "redirect:/blogPost/blogCreationForm";
    }

    //all blogs + sorting // sortField="publishedAt"&order="asc"
    @GetMapping("/allblogs")//all posts
    public  String getAllBlogs(
            Model model,
            @RequestParam(required = false)String search,
            @RequestParam(required = false)String authorName,
            @RequestParam(required = false)List<String> tagNames,
            @RequestParam(required = false, defaultValue = "publishedAt")String sortField,
            @RequestParam(required = false, value="order",defaultValue = "desc")String order,
            @RequestParam(required = false,defaultValue = "1900-01-01")String startDate,
            @RequestParam(required = false)String endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            Authentication auth
    ){
        System.out.println("allBlogs controller"+search+authorName+tagNames);
        page=Math.max(0,page);
        if(endDate==null || endDate.isEmpty())endDate= LocalDate.now().toString();
        LocalDate start=LocalDate.parse(startDate);
        LocalDate end=LocalDate.parse(endDate);

        model.addAttribute("allAuthors",postService.getAllAuthors());
        model.addAttribute("allTags",postService.getAllTags());

        List<String>selectedTagsList=(tagNames!=null)?tagNames:new ArrayList<>();
        List<PostDto>postDtos;

        Map<String, Object> result =postService.getFilteredPosts(
                search,authorName,selectedTagsList,sortField,order,
                start,end,page,pageSize
        );
        model.addAttribute("allPosts", result.get("posts"));
        model.addAttribute("currentPage", result.get("currentPage"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("totalItems", result.get("totalItems"));
        model.addAttribute("search",search);
        model.addAttribute("selectedAuthor",authorName);
        model.addAttribute("selectedTagsList",selectedTagsList);
        model.addAttribute("sortField",sortField);
        model.addAttribute("order",order);
        model.addAttribute("startDate",startDate);
        model.addAttribute("endDate",endDate);

        if(auth!=null){
            model.addAttribute("currentUserEmail",auth.getName());
            model.addAttribute("isAdmin",hasRole(auth,"ADMIN"));
            model.addAttribute("isUser",hasRole(auth,"USER"));
        }
        return "allBlogsPage";
    }

    //find by postId
    @GetMapping("/{postId}")
    public  String viewPost(
            @PathVariable int postId,
            @RequestParam(defaultValue = "0")int commentPage,
            @RequestParam(value = "commentId", defaultValue = "-1") int commentId,//this is for comment update
            Model model,
            Authentication auth
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

        if(auth !=null){
            model.addAttribute("currentUserEmail",auth.getName());
            model.addAttribute("isAdmin",hasRole(auth,"ADMIN"));
            model.addAttribute("isUser",hasRole(auth,"USER"));
            model.addAttribute("isOwner", postDto.getAuthor().equals(auth.getName()));
            model.addAttribute("updateCommentId",commentId);
        }
        return "singlePostPage";
    }

    @GetMapping("/updatePostForm/{id}")
    public  String updatePostForm(@PathVariable int id, Model model,Authentication auth){
        System.out.println("updateForm ctrlr");
        PostDto postDto=postService.findById(id);
        if(!canModifyPost(auth,postDto)){
            return "redirect:/blogPost/"+id+ "?error=unauthorized";
        }
        model.addAttribute("post",postDto);
        model.addAttribute("isAdmin",hasRole(auth,"ADMIN"));
        return "updatePostForm";
    }

    @PostMapping("/update")
    public String updateBlogPost(@ModelAttribute PostDto postDto, Authentication auth){
        System.out.println("updatePost ctrlr"+ postDto.getTitle()+ (postDto.getTags()));
            try {
                PostDto existing = postService.findById(postDto.getId());
                if (!canModifyPost(auth, existing))
                    return "redirect:/blogPost/" + postDto.getId() + "?error=unauthorized";
                if (!hasRole(auth, "ADMIN")) {
                    postDto.setAuthor(auth.getName());
                }
                postService.updatePost(postDto);
                return "redirect:/blogPost/" + postDto.getId();
            }catch (Exception e){
                System.out.println(e.getMessage());
                return "redirect:/blogPost/updatePostForm/"+postDto.getId();
            }
    }

    //    @DeleteMapping("/deletePost/{id}")//browsers not supporting but we can
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable int id,Authentication auth){
        System.out.println("deleteBy Id Ctrlr");

        PostDto post = postService.findById(id);
        if (!canModifyPost(auth, post)) return "redirect:/blogPost/" + id + "?error=unauthorized";

        postService.deleteById(id);
        return "redirect:/blogPost/allblogs";
    }

    private boolean hasRole(Authentication auth, String role) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private boolean canModifyPost(Authentication auth, PostDto post) {
        if (auth == null) return false;
        if (hasRole(auth, "ADMIN")) return true;
        // AUTHOR can only modify their own posts
        return hasRole(auth, "USER") && post.getAuthor().equals(auth.getName());
    }
}