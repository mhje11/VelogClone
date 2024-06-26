package hello.velogclone.domain.post.service;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.likes.repository.LikeRepository;
import hello.velogclone.domain.post.dto.PostRequestDto;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.repository.PostRepository;
import hello.velogclone.domain.tag.entity.Tag;
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
    public List<PostResponseDto> findAllByBlogIdAndTemporal(Long blogId, boolean temporal) {
        List<PostResponseDto> post = postRepository.findAllByBlogIdAndTemporal(blogId, temporal).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return post;
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
        Optional<Blog> blogOptional = blogRepository.findById(user.getBlog().getId());
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setBlog(blogOptional.get());
        post.setUser(user);
        post.setTemporal(postRequestDto.isTemporal());
        List<String> tagNames = postRequestDto.getTags();
        List<Tag> tags = new ArrayList<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            tags = tagService.findOrCreateTags(tagNames);
        }
        post.setTags(tags);
        post.setSeries(postRequestDto.getSeries());
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long id, Post post, String loginId) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!existingPost.getUser().getLoginId().equals(loginId)) {
            throw new SecurityException("권한이 없습니다.");
        }
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        List<Tag> tags = tagService.findOrCreateTags(post.getTags().stream().map(Tag::getName).toList());
        existingPost.setTags(tags);
        postRepository.save(existingPost);
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
        String seriesName = post.getSeries() != null ? post.getSeries().getSeriesName() : "";

        return new PostResponseDto(post.getId(), post.getTitle(), post.getContent(), post.getBlog().getId(), likeCount, tags ,seriesName, post.isTemporal());
    }

}
