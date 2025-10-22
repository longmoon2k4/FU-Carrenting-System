package he186065.fucarrentingsystem.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CarProducer")
public class CarProducer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProducerID")
    private Integer producerId;

    @Column(name = "ProducerName", nullable = false)
    private String producerName;

    @Column(name = "Address", nullable = false)
    private String address;

    @Column(name = "Country", nullable = false)
    private String country;

    @OneToMany(mappedBy = "producer")
    private List<Car> cars = new ArrayList<>();

    public CarProducer() {}

    public Integer getProducerId() { return producerId; }
    public void setProducerId(Integer producerId) { this.producerId = producerId; }

    public String getProducerName() { return producerName; }
    public void setProducerName(String producerName) { this.producerName = producerName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public List<Car> getCars() { return cars; }
    public void setCars(List<Car> cars) { this.cars = cars; }
}
