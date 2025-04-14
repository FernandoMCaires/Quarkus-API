package org.acme;

import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
class GreetingResourceIT extends GreetingResourceTest {
    // Este arquivo vai herdar os testes do GreetingResourceTest e executá-los no modo empacotado
}
