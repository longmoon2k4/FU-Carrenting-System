package he186065.fucarrentingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID")
    private Integer customerId;

    @Column(name = "CustomerName", nullable = false)
    private String customerName;

    @Column(name = "Mobile", nullable = false)
    private String mobile;

    @Column(name = "Birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "IdentityCard", nullable = false)
    private String identityCard;

    @Column(name = "LicenceNumber", nullable = false)
    private String licenceNumber;

    @Column(name = "LicenceDate", nullable = false)
    private LocalDate licenceDate;

    @Column(name = "Email", nullable = false)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "AccountID", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "customer")
    private List<CarRental> rentals = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Review> reviews = new ArrayList<>();

    public Customer() {}

    // getters / setters omitted for brevity (generate as needed)
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getIdentityCard() { return identityCard; }
    public void setIdentityCard(String identityCard) { this.identityCard = identityCard; }

    public String getLicenceNumber() { return licenceNumber; }
    public void setLicenceNumber(String licenceNumber) { this.licenceNumber = licenceNumber; }

    public LocalDate getLicenceDate() { return licenceDate; }
    public void setLicenceDate(LocalDate licenceDate) { this.licenceDate = licenceDate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public List<CarRental> getRentals() { return rentals; }
    public void setRentals(List<CarRental> rentals) { this.rentals = rentals; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
