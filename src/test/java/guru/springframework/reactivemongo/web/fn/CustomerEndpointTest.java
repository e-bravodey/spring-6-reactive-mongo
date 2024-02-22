package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.services.BeerServiceImplTest;
import guru.springframework.reactivemongo.services.CustomerServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndpointTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    @Order(999)
    void testDeleteCustomerNotFound() {
        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 99)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
    @Test
    @Order(999)
    void testDeleteCustomer() {
        CustomerDTO testCustomerDTO = getSavedTestCustomer();
        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testCustomerDTO.getId())
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(3)
    void testUpdateCustomerNotFound() {
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 99)
                .body(Mono.just(getCustomerDto()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void testUpdateCustomerBadRequest() {
        CustomerDTO testCustomerDTO = getSavedTestCustomer();
        testCustomerDTO.setCustomerName(" ");
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testCustomerDTO.getId())
                .body(Mono.just(testCustomerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testUpdateCustomer() {
        CustomerDTO testCustomerDTO = getSavedTestCustomer();
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testCustomerDTO.getId())
                .body(Mono.just(testCustomerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }


    @Test
    void testCreateCustomerBadRequest() {
        CustomerDTO testCustomer = getCustomerDto();
        testCustomer.setCustomerName(" ");

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();

    }
    @Test
    void testCreateCustomer() {

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(getCustomerDto()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    @Order(1)
    void testGetByIdNotFound() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    @Order(2)
    void testGetById() {
        CustomerDTO customerDTO = getSavedTestCustomer();
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    @Test
    @Order(1)
    void testListCustomers() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    public static CustomerDTO getCustomerDto() {
        return CustomerDTO.builder()
                .customerName("Test Customer")
                .build();
    }


    public CustomerDTO getSavedTestCustomer(){
        FluxExchangeResult<CustomerDTO> customerDTOFluxExchangeResult = webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(getCustomerDto()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(CustomerDTO.class);

        List<String> location = customerDTOFluxExchangeResult.getResponseHeaders().get("Location");

        return webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange().returnResult(CustomerDTO.class).getResponseBody().blockFirst();
    }
}
