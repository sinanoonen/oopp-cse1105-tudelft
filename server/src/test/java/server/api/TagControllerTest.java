package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import commons.transactions.Tag;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import server.database.TagRepository;

/**
 * Tests for the TagController class.
 */
public class TagControllerTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagController tagController;

    private Tag[] tags;

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        tagController = new TagController(tagRepository);
        tags = new Tag[3];
        tags[0] = new Tag("Tag1", new Color(-16776962));
        tags[1] = new Tag("Tag2", new Color(-16776963));
        tags[2] = new Tag("Tag3", new Color(-16776964));
    }

    @Test
    public void getAllTags() {
        List<Tag> emptyList = Collections.emptyList();
        List<Tag> tagList = Arrays.asList(tags);
        when(tagRepository.findAll()).thenReturn(emptyList, tagList);
        List<Tag> res = tagController.getAllTags();
        assertNotNull(res);
        assertTrue(res.isEmpty());
        res = tagController.getAllTags();
        assertEquals(tagList, res);
    }

    @Test
    public void cannotAddNullTag() {
        ResponseEntity<Tag> res = tagController.addTag(null);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void updateTag() {
        List<Tag> existingTags = Collections.singletonList(tags[0]);
        Tag update = new Tag("UpdatedTag1", new Color(-16776961));
        when(tagRepository.findById(1L)).thenReturn(existingTags);
        ResponseEntity<Tag> res = tagController.updateTag(1L, update);
        assertEquals(BAD_REQUEST, res.getStatusCode());
        assertNotEquals(update, res.getBody());
    }


}
