package hello.velogclone.domain.postimage.service;

import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.repository.PostRepository;
import hello.velogclone.domain.postimage.entity.PostImage;
import hello.velogclone.domain.postimage.repository.PostImageRepository;
import hello.velogclone.global.exception.PostImageNotFoundException;
import hello.velogclone.global.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostImageService {
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;

    public void uploadPostImage(Long postId, MultipartFile file) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
        String uuid = UUID.randomUUID().toString();
        String fileName = postId + "_" + file.getOriginalFilename();
        String imagePathString = "src/main/resources/static/images/posts" + File.separator + fileName;
        Path imagePath = Paths.get(imagePathString);

        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());

        PostImage postImage = new PostImage(file.getContentType(), "/images/posts/" + fileName, post);
        postImageRepository.save(postImage);
    }

    @Transactional(readOnly = true)
    public PostImage findPostImageByUuid(String uuid) {
        return postImageRepository.findByUuid(uuid).orElseThrow(() -> new PostImageNotFoundException("이미지를 찾을 수 없습니다."));
    }

    public void deletePostImage(String uuid) {
        PostImage postImage = postImageRepository.findByUuid(uuid).orElseThrow(() -> new PostImageNotFoundException("이미지를 찾을 수 없습니다."));
        String imagePathString = "src/main/resources/static" + postImage.getUrl();
        Path imagePath = Paths.get(imagePathString);

        try {
            Files.deleteIfExists(imagePath);
            postImageRepository.delete(postImage);
        } catch (IOException e) {
            log.info("이미지를 삭제하는데 실패 했습니다." + "  " + e.getMessage());
        }

    }
}
