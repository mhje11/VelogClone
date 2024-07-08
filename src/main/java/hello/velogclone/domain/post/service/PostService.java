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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public List<PostResponseDto> findAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAllByBlogIdAndTemporal(Long blogId, boolean temporal, Pageable pageable) {
        return postRepository.findAllByBlogIdAndTemporal(blogId, temporal, pageable)
                .map(PostResponseDto::new);
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
    public Post createPost(PostRequestDto postRequestDto, User user) {
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
        return post;
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
        log.info("시리즈 : " + post.getSeries());
        existingPost.setSeries(post.getSeries());
        existingPost.setTags(tags);
        existingPost.setTemporal(post.isTemporal());
        existingPost.setUpdatedAt(LocalDateTime.now());
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

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAll(PageRequest pageRequest) {
        Page<Post> posts = postRepository.findAll(pageRequest);
        return posts.map(this::convertToDto);
    }


    private PostResponseDto convertToDto(Post post) {
        Long likeCount = post.getLikes() == null ? 0L : (long) post.getLikes().size();

        List<String> tags = Arrays.asList(post.getTags().stream()
                .map(Tag::getName)
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" ")).split(" "));

        String seriesName = post.getSeries() != null ? post.getSeries().getSeriesName() : "";

        String thumbnailUrl = post.getPostImages().isEmpty() ? "/images/posts/default_thumbnail.png" : post.getPostImages().getFirst().getUrl();

        Long commentCount = post.getComments() == null ? 0L : (long) post.getComments().size();

        String profileImageUrl = (post.getUser().getProfileImage() != null && post.getUser().getProfileImage().getUrl() != null) ? post.getUser().getProfileImage().getUrl()
                : "/images/profiles/default-profile.png";

        return new PostResponseDto(post.getId(), post.getTitle(), post.getContent(), post.getBlog().getId(), likeCount, tags
                , seriesName, post.isTemporal(), thumbnailUrl
                , post.getCreatedAt(), commentCount, post.getUser().getLoginId(), profileImageUrl);
    }

    public String cleanFileName(String fileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9._-]");
        return pattern.matcher(normalized).replaceAll("");
    }

    public Page<PostResponseDto> findAllBySeriesId(Long seriesId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllBySeriesId(seriesId, pageable);
        return posts.map(this::convertToDto);
    }


}
