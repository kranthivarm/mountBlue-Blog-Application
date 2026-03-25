package com.example.demo.filters;

import com.example.demo.Utils.JwtUtil;
import com.example.demo.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthfilter extends OncePerRequestFilter {
    private  final JwtUtil jwtUtil;
    private  final UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected  boolean shouldNotFilter(HttpServletRequest request){
        String path =request.getServletPath();
        return !path.startsWith("/api/");//applying jwt only for /api
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader =request.getHeader("Authorization");
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return ;
        }
        try {
            String jwt=authHeader.substring(7);
            String email=jwtUtil.extractEmail(jwt);
            // authenticates, only if not authenticated already
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
                if(jwtUtil.isTokenValid(jwt,userDetails)){
                    //authorities from token roles
                    List<SimpleGrantedAuthority>authorities=jwtUtil.extractRoles(jwt)
                            .stream()
                            .map(SimpleGrantedAuthority:: new)
                            .collect(Collectors.toList());
                    UsernamePasswordAuthenticationToken authToken=
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,null,authorities
                            );
                    //this not mandatory;just to set additional/metadat like setDetails; useful for loggin etc
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);;
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
            return;
        }
        filterChain.doFilter(request,response);
    }
}
