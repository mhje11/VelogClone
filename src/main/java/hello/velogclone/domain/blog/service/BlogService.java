package hello.velogclone.domain.blog.service;

import hello.velogclone.domain.blog.dto.BlogDto;
import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public Blog createBlog(BlogDto blogDto, String loginId) {
        Optional<User> user = userRepository.findByLoginId(loginId);
        Blog blog = new Blog();
        blog.setUser(user.get());
        blog.setTitle(blogDto.getTitle());
        return blogRepository.save(blog);
    }

    @Transactional(readOnly = true)
    public Optional<Blog> findBlogByUserLoginId(String loginId) {
        Optional<User> user = userRepository.findByLoginId(loginId);
        return blogRepository.findBlogByUser(user.get());
    }

    public void updateBlog(BlogDto blogDto, String loginId) {
        Optional<Blog> optionalBlog = findBlogByUserLoginId(loginId);
        Blog blog = optionalBlog.get();
        blog.setTitle(blogDto.getTitle());
        blogRepository.save(blog);

    }

    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId).orElse(null);
    }



}
