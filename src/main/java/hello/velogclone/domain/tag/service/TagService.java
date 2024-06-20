package hello.velogclone.domain.tag.service;

import hello.velogclone.domain.tag.entity.Tag;
import hello.velogclone.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public List<Tag> findOrCreateTags(String tagsStr) {
        if (tagsStr == null || tagsStr.isEmpty()) {
            return new ArrayList<>();
        }
        String[] tagNames = tagsStr.split("\\s+");
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
    @Transactional
    public Tag createTag(String name) {
        Tag tag = new Tag(name);
        return tagRepository.save(tag);
    }

    public Optional<Tag> findTagByName(String name) {
        return tagRepository.findByName(name);
    }
}
