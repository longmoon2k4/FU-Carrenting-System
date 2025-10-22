package he186065.fucarrentingsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CarRental")
public class CarRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarRentalID")
    private Integer carRentalId;

    @ManyToOne
    @JoinColumn(name = "CustomerID", nullable = false)
    @JsonBackReference("customer-rentals")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "CarID", nullable = false)
    @JsonBackReference("car-rentals")
    private Car car;

    @Column(name = "PickupDate", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "ReturnDate", nullable = false)
    private LocalDate returnDate;

    @Column(name = "RentPrice", nullable = false)
    private BigDecimal rentPrice;

    @Column(name = "Status", nullable = false)
    private String status;

    public CarRental() {}

    public Integer getCarRentalId() { return carRentalId; }
    public void setCarRentalId(Integer carRentalId) { this.carRentalId = carRentalId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public BigDecimal getRentPrice() { return rentPrice; }
    public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
