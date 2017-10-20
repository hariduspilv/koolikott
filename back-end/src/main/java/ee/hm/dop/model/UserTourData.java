package ee.hm.dop.model;

import javax.persistence.*;

@Entity
public class UserTourData implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean editTour = false;

    @Column(nullable = false)
    private boolean generalTour = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEditTour() {
        return editTour;
    }

    public void setEditTour(boolean editTour) {
        this.editTour = editTour;
    }

    public boolean isGeneralTour() {
        return generalTour;
    }

    public void setGeneralTour(boolean generalTour) {
        this.generalTour = generalTour;
    }
}
