package server.api;


import commons.transactions.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * Endpoint to update an existing tag.
     *
     * @param name name of the tag to update
     * @param update updated tag information
     * @return updated tag
     */
    @PutMapping("/{name}")
    public ResponseEntity<Tag> updateTag(@PathVariable String name, @RequestBody Tag update) {
        if (update == null) {
            System.out.println("/tags: Received bad PUT request");
            return ResponseEntity.badRequest().build();
        }
        if (isNullOrEmpty(name) || !update.getName().equals(name)) {
            System.out.println("/tags: Received bad PUT request");
            return ResponseEntity.badRequest().build();
        }
        var existingTags = repo.findByName(name);
        System.out.println("/tags: Received valid PUT request");
        for (Tag tag : existingTags) {
            String tagName = update.getName();
            int tagColor = update.getColor();
            if (repo.updateTagColor(tagColor, tagName) != 1) {
                return ResponseEntity.badRequest().build();
            }
            repo.save(tag);
        }
        return ResponseEntity.ok(update);
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
        System.out.println("/tags: Received valid POST request");
        System.out.println(tag);
        repo.save(tag);
        return ResponseEntity.ok(tag);
    }

    /**
     * Endpoint to delete a tag from the database.
     *
     * @param name name of the tag to delete
     * @return ResponseEntity indicating success or failure of the deletion operation
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteTag(@PathVariable String name) {
        int res = repo.deleteTagByName(name);
        if (res != 1) {
            System.out.println("/tags: Received a bad DELETE request");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("/tags: Received a valid DELETE request");
        return ResponseEntity.ok().build();
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
