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
import hello.velogclone.global.exception.PostNotFoundException;
import hello.velogclone.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public void createPost(PostRequestDto postRequestDto, User user) {
        Blog blog = blogRepository.findById(user.getBlog().getId())
                .orElseThrow(() -> new PostNotFoundException("블로그를 찾을 수 없습니다."));
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setBlog(blog);
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
        log.info("게시글 생성 Id : {}", post.getId());
    }

    @Transactional
    public void updatePost(Long id, Post post, String loginId) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        if (!existingPost.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        List<Tag> tags = tagService.findOrCreateTags(post.getTags().stream().map(Tag::getName).toList());
        existingPost.setTags(tags);
        existingPost.setTemporal(post.isTemporal());
        postRepository.save(existingPost);
    }

    @Transactional
    public void deletePost(Long postId, String loginId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }
        postRepository.delete(post);
        log.info("게시글 삭제 Id : {}", postId);
    }
    @Transactional(readOnly = true)
    public Post findPostEntityById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
    }



    private PostResponseDto convertToDto(Post post) {
        Long likeCount = post.getLikes().stream()
                .filter(likes -> likeRepository.existsById(likes.getId()))
                .count();

        String tags = post.getTags().stream()
                .map(Tag::getName)
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));

        String seriesName = post.getSeries() != null ? post.getSeries().getSeriesName() : "";

        return new PostResponseDto(post.getId(), post.getTitle(), post.getContent(), post.getBlog().getId(), likeCount, tags ,seriesName, post.isTemporal());
    }

}
