package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<CustomerDTO> listCustomer();
    Mono<CustomerDTO> saveCustomer(CustomerDTO customerDTO);

    Mono<CustomerDTO> saveCustomer(Mono<CustomerDTO> beerDto);
    Mono<CustomerDTO> getById(String customerId);

    Mono<CustomerDTO> updateCustomer(String customerId, CustomerDTO customerDTO);


    Mono<Void> deleteCustomerById(String customerId);
}
