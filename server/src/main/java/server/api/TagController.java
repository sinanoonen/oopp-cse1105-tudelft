package server.api;

import commons.User;
import commons.transactions.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.TagRepository;




/**
 * A controller to access the tags of the database.
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagRepository repo;

    public TagController(TagRepository repo) {
        this.repo = repo;
    }

    /**
     * Endpoint to get a list of all tags.
     *
     * @return found tags
     */
    @GetMapping(path = {"", "/"})
    public List<Tag> getAllTags() {
        System.out.println("/tags: Received valid GET request");
        return repo.findAll();
    }

    /**
     * Endpoint to get a tag by its name.
     *
     * @param name name of the tag to query by
     * @return found tag
     */
    @GetMapping("/{name}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String name) {
        List<Tag> tags = repo.findByName(name);
        if (tags == null || tags.isEmpty()) {
            System.out.println("/tags: Received a bad GET request");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("/tags: Received a valid GET request");
        return ResponseEntity.ok(tags.getFirst());
    }

    /**
     * Endpoint to add a new tag to the database.
     *
     * @param tag tag to be added
     * @return added tag
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag) {
        if (tag == null || tag.getName() == null) {
            System.out.println("/tags: Received bad POST request");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("/users: Received valid POST request:");
        System.out.println(tag);
        Tag saved = repo.save(tag);
        return ResponseEntity.ok(saved);
    }
}
