package fr.avenirsesr.portfolio.security;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import static org.mockito.Mockito.verify;


// Mainly for coverage report %.
@SpringBootTest
class AvenirsPortfolioSecurityApplicationTest {

    @SpyBean
    private AvenirsPortfolioSecurityApplication application;

   @Test
    void testMain() {
        try (var mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            mockedSpringApplication
                    .when(() -> SpringApplication.run(AvenirsPortfolioSecurityApplication.class, new String[]{}))
                    .thenReturn(null);

            AvenirsPortfolioSecurityApplication.main(new String[]{});
            mockedSpringApplication.verify(() -> SpringApplication.run(AvenirsPortfolioSecurityApplication.class, new String[]{}));
        }
    }
}