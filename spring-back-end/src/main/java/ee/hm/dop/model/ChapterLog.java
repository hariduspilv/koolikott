package ee.hm.dop.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "Chapter_Log")
public class ChapterLog implements AbstractEntity  {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @ManyToMany(fetch = EAGER, cascade = ALL)
    @Fetch(FetchMode.SELECT)
    @OrderColumn(name = "rowOrder", nullable = false)
    @JoinTable(
            name = "Chapter_ChapterBlock_Log",
            joinColumns = {@JoinColumn(name = "chapter")},
            inverseJoinColumns = {@JoinColumn(name = "chapterBlock")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"chapter", "chapterBlock", "rowOrder"}))
    private List<ChapterBlockLog> blocks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChapterBlockLog> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<ChapterBlockLog> blocks) {
        this.blocks = blocks;
    }
}
