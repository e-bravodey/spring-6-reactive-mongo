package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.mappers.CustomerMapper;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public Flux<CustomerDTO> listCustomer() {
        return repository.findAll().map(mapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDTO> saveCustomer(CustomerDTO customerDTO) {
        return repository.save(mapper.customerDtoToCustomer(customerDTO))
                .map(mapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDTO> saveCustomer(Mono<CustomerDTO> customerDTO) {
        return customerDTO.map(mapper::customerDtoToCustomer)
                .flatMap(repository::save)
                .map(mapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDTO> getById(String customerId) {
        return repository.findById(customerId)
                .map(mapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDTO> updateCustomer(String customerId, CustomerDTO customerDTO) {
        return repository.findById(customerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(foundCustomer -> {
                   foundCustomer.setCustomerName(customerDTO.getCustomerName());
                   return foundCustomer;
                }).flatMap(repository::save)
                .map(mapper::customerToCustomerDto);
    }

    @Override
    public Mono<Void> deleteCustomerById(String customerId) {
        return repository.deleteById(customerId);
    }
}
