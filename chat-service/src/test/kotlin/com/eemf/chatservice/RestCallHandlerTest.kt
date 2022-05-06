package com.eemf.chatservice

import org.springframework.test.web.reactive.server.WebTestClient

internal class RestCallHandlerTest {

  fun bindToAndTestUpstreamServer() {
    WebTestClient
      .bindToServer()
      .baseUrl("upstream-server-base-url")
      .build()
      .post()
      .uri("/resource")
      .exchange()
      .expectStatus().isCreated()
      .expectHeader().valueEquals("Content-Type", "application/json")
      .expectBody().jsonPath("field").isEqualTo("value")

    /**
     * Read more: https://www.baeldung.com/spring-5-webclient
     *
     * Also check:
     *
     1. We can test a particular RouterFunction by passing it to the bindToRouterFunction method:
     RouterFunction function = RouterFunctions.route(
     RequestPredicates.GET("/resource"),
     request -> ServerResponse.ok().build()
     );

     WebTestClient
     .bindToRouterFunction(function)
     .build().get().uri("/resource")
     .exchange()
     .expectStatus().isOk()
     .expectBody().isEmpty();

     2. The same behavior can be achieved with the bindToWebHandler method, which takes a WebHandler instance:
     WebHandler handler = exchange -> Mono.empty();
     WebTestClient.bindToWebHandler(handler).build();

     3. A more interesting situation occurs when we're using the bindToApplicationContext method.
     It takes an ApplicationContext and analyses the context for controller beans and @EnableWebFlux configurations.
     If we inject an instance of the ApplicationContext, a simple code snippet may look like this:

     @Autowired
     private ApplicationContext context;

     WebTestClient testClient = WebTestClient.bindToApplicationContext(context)
    .build();

     4. A shorter approach would be providing an array of controllers we want to test by the bindToController method. Assuming we've got a Controller class and we injected it into a needed class, we can write:

    @Autowired
    private Controller controller;

    WebTestClient testClient = WebTestClient.bindToController(controller).build();

     *
     */
  }
}