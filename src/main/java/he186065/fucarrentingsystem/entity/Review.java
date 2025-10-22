package he186065.fucarrentingsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "CarID", nullable = false)
    private Car car;

    @Column(name = "ReviewStar", nullable = false)
    private Integer reviewStar;

    @Column(name = "Comment", nullable = false)
    private String comment;

    public Review() {}

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public Integer getReviewStar() { return reviewStar; }
    public void setReviewStar(Integer reviewStar) { this.reviewStar = reviewStar; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
