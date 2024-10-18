package net.banking.customerservice.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "customers")
class Customer {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String identity;
    private LocalDate birth;
    private String email;
    private String address;
}
