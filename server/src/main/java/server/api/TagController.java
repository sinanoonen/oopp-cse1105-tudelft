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
        return repo.findAll();
    }

    /**
     * Endpoint to get a tag by its name.
     *
     * @param id id of the tag to query by
     * @return found tag
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagByName(@PathVariable long id) {
        List<Tag> tags = repo.findById(id);
        if (tags == null || tags.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tags.getFirst());
    }

    /**
     * Endpoint to update an existing tag.
     *
     * @param id id of the tag to update
     * @param update updated tag information
     * @return updated tag
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable long id, @RequestBody Tag update) {
        if (update == null) {
            return ResponseEntity.badRequest().build();
        }
        if (repo.findById(id) == null || repo.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var existingTags = repo.findById(id);
        for (Tag tag : existingTags) {
            String tagName = update.getName();
            int tagColor = update.getColor();
            if (repo.updateTag(id, tagColor, tagName) != 1) {
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
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        repo.save(tag);
        return ResponseEntity.ok(tag);
    }

    /**
     * Endpoint to delete a tag from the database.
     *
     * @param id id of the tag to delete
     * @return ResponseEntity indicating success or failure of the deletion operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable long id) {
        int res = repo.deleteTagByName(id);
        if (res != 1) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
