package hello.velogclone.domain.follow.service;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.follow.dto.FollowDto;
import hello.velogclone.domain.follow.entity.Follow;
import hello.velogclone.domain.follow.repository.FollowRepository;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.global.exception.BlogNotFoundException;
import hello.velogclone.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final BlogRepository blogRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void followBlog(Long blogId, String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));

        Follow existingFollow = followRepository.findByBlogAndUser(blog, user);
        if (existingFollow == null) {
            Follow follow = new Follow();
            follow.setBlog(blog);
            follow.setUser(user);
            followRepository.save(follow);
        } else {
            followRepository.delete(existingFollow);
        }
    }

    @Transactional(readOnly = true)
    public List<FollowDto> findAllFollowers(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));
        return followRepository.findByBlog(blog).stream()
                .map(follow -> {
                    String profileImageUrl = Objects.nonNull(follow.getUser().getProfileImage()) ?
                            follow.getUser().getProfileImage().getUrl() : "/images/profiles/default-profile.png";
                    return new FollowDto(follow.getUser().getLoginId(), profileImageUrl);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FollowDto> findAllFollowingsByUser(User user) {
        return followRepository.findByUser(user).stream()
                .map(follow -> {
                    String profileImageUrl = Objects.nonNull(follow.getBlog().getUser().getProfileImage()) ?
                            follow.getBlog().getUser().getProfileImage().getUrl() : "/images/profiles/default-profile.png";
                    return new FollowDto(follow.getBlog().getUser().getLoginId(), profileImageUrl);
                })
                .collect(Collectors.toList());
    }


    public void deleteByUser(User user) {
        followRepository.deleteByUser(user);
    }


    @Transactional(readOnly = true)
    public int getFollowingCount(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));
        return followRepository.countByUser(blog.getUser());
    }

    @Transactional(readOnly = true)
    public int getFollowerCount(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));
        return followRepository.countByBlog(blog);
    }
}
