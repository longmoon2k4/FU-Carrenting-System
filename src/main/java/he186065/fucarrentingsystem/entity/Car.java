package he186065.fucarrentingsystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarID")
    private Integer carId;

    @Column(name = "CarName", nullable = false)
    private String carName;

    @Column(name = "CarModelYear", nullable = false)
    private Integer carModelYear;

    @Column(name = "Color", nullable = false)
    private String color;

    @Column(name = "Capacity", nullable = false)
    private Integer capacity;

    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "ImportDate", nullable = false)
    private LocalDate importDate;

    @ManyToOne
    @JoinColumn(name = "ProducerID", nullable = false)
    private CarProducer producer;

    @Column(name = "RentPrice", nullable = false)
    private BigDecimal rentPrice;

    @Column(name = "Status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "car")
    private List<CarRental> rentals = new ArrayList<>();

    @OneToMany(mappedBy = "car")
    private List<Review> reviews = new ArrayList<>();

    public Car() {}

    // getters / setters
    public Integer getCarId() { return carId; }
    public void setCarId(Integer carId) { this.carId = carId; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public Integer getCarModelYear() { return carModelYear; }
    public void setCarModelYear(Integer carModelYear) { this.carModelYear = carModelYear; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getImportDate() { return importDate; }
    public void setImportDate(LocalDate importDate) { this.importDate = importDate; }

    public CarProducer getProducer() { return producer; }
    public void setProducer(CarProducer producer) { this.producer = producer; }

    public BigDecimal getRentPrice() { return rentPrice; }
    public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CarRental> getRentals() { return rentals; }
    public void setRentals(List<CarRental> rentals) { this.rentals = rentals; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
