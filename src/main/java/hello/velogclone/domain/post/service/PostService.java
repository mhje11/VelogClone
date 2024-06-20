package hello.velogclone.domain.post.service;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.likes.entity.Likes;
import hello.velogclone.domain.likes.repository.LikeRepository;
import hello.velogclone.domain.post.dto.PostRequestDto;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.repository.PostRepository;
import hello.velogclone.domain.tag.entity.Tag;
import hello.velogclone.domain.tag.repository.TagRepository;
import hello.velogclone.domain.tag.service.TagService;
import hello.velogclone.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final LikeRepository likeRepository;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public List<PostResponseDto> findAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> findAllPostByBlogId(Long blogId) {
        return postRepository.findAllByBlogId(blogId).stream()
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
    public void createPost(PostRequestDto postRequestDto, User user) {
        Optional<Blog> blogOptional = blogRepository.findById(postRequestDto.getBlogId());
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setBlog(blogOptional.get());
        post.setUser(user);
        List<Tag> tags = tagService.findOrCreateTags(postRequestDto.getTags());
        post.setTags(tags);
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
    public void deletePost(Long postId, String loginId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Post post = postOptional.get();
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }
        postRepository.delete(post);
    }
    @Transactional(readOnly = true)
    public Post findPostEntityById(Long id) {
        return postRepository.findById(id)
                .orElse(null);
    }



    private PostResponseDto convertToDto(Post post) {
        Long likeCount = post.getLikes().stream()
                .filter(likes -> likeRepository.existsById(likes.getId()))
                .count();

        List<String> tags = post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        return new PostResponseDto(post.getId(), post.getTitle(), post.getContent(), post.getBlog().getId(), likeCount, tags);
    }

}
