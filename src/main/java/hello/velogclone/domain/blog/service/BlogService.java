package hello.velogclone.domain.blog.service;

import hello.velogclone.domain.blog.dto.BlogDto;
import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.global.exception.BlogNotFoundException;
import hello.velogclone.global.exception.UserNotFoundException;
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
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        Blog blog = new Blog();
        blog.setUser(user);
        blog.setTitle(blogDto.getTitle());
        return blogRepository.save(blog);
    }

    @Transactional(readOnly = true)
    public Optional<Blog> findBlogByUserLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        return blogRepository.findBlogByUser(user);
    }

    public void updateBlog(BlogDto blogDto, String loginId) {
        Optional<Blog> optionalBlog = findBlogByUserLoginId(loginId);
        Blog blog = optionalBlog.orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));
        blog.setTitle(blogDto.getTitle());
        blogRepository.save(blog);
    }

    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId).orElse(null);
    }

}
