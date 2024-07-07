package hello.velogclone.domain.tag.service;

import hello.velogclone.domain.tag.entity.Tag;
import hello.velogclone.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public List<Tag> findOrCreateTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            if (tagName.startsWith("#")) {
                tagName = tagName.substring(1);
            }
            String finalTagName = tagName;
            Tag tag = tagRepository.findByName(finalTagName)
                    .orElseGet(() -> createTag(finalTagName));
            tags.add(tag);
        }
        return tags;
    }

    @Transactional(readOnly = true)
    public List<Tag> findTagByPostId(Long postId) {
        return tagRepository.findTagsByPostId(postId);
    }

    @Transactional
    public Tag createTag(String name) {
        Tag tag = new Tag(name);
        return tagRepository.save(tag);
    }

    public Optional<Tag> findTagByName(String name) {
        return tagRepository.findByName(name);
    }
}
