package hello.velogclone.domain.post.service;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.post.dto.PostRequestDto;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BlogRepository blogRepository;


    @Transactional(readOnly = true)
    public List<PostResponseDto> findAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponseDto findPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional
    public void createPost(PostRequestDto postRequestDto) {
        Optional<Blog> blogOptional = blogRepository.findById(postRequestDto.getBlogId());
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setBlog(blogOptional.get());
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long id, PostRequestDto postRequestDto) {
        Optional<Post> postOptional = postRepository.findById(id);
        Post post = postOptional.get();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    private PostResponseDto convertToDto(Post post) {
        return new PostResponseDto(post.getTitle(),post.getContent(),post.getBlog().getId(),post.getLikes());
    }
}
