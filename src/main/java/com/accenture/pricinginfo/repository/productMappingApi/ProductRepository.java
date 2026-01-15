package com.accenture.pricinginfo.repository.productMappingApi;

import com.accenture.pricinginfo.exception.ApiErrorResponse;
import com.accenture.pricinginfo.exception.BadRequestException;
import com.accenture.pricinginfo.repository.productMappingApi.dto.ProductIdResponse;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/*
* Added Imports
* */
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Repository
public class ProductRepository {

    private final WebClient webClient;

    private final Logger log = LoggerFactory.getLogger(ProductRepository.class);

    @Value("${product.microservice.url}")
    private String productMappingEndpoint;

    @Value("${product.microservice.getProductPath}")
    private String productMappingPath;

    public ProductRepository(WebClient webClient) {
        this.webClient = webClient;
    }

    public ProductIdResponse getProductId(String productCode) {
        return webClient.get().uri(productMappingEndpoint + productMappingPath, productCode)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleDownstreamError)
                .bodyToMono(ProductIdResponse.class)
                .doOnError(error -> log.error("message='Unexpected downstream response!'"))
                .block();
    }

    private Mono<Throwable> handleDownstreamError(ClientResponse clientResponse) {
        final HttpStatusCode httpStatusCode = clientResponse.statusCode();
        ServiceException unknownException = new ServiceException("Unknown downstream error.");
        return clientResponse
            .bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiError -> {
                Exception exception;
                switch (httpStatusCode) {
                    case BAD_REQUEST:
                    case INTERNAL_SERVER_ERROR:
                        exception = new BadRequestException(
                                apiError.getErrorCode(),
                                apiError.getErrorMessage(),
                                apiError.getErrorDetails()
                        );
                        break;
                    default:
                        exception = unknownException;
                        break;
                }
                return Mono.error(exception);
            });
    }
}
