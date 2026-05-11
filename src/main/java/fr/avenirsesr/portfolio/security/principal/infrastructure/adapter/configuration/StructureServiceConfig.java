package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.principal.domain.port.input.StructureService;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import fr.avenirsesr.portfolio.security.principal.domain.service.StructureServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StructureServiceConfig {
  @Bean
  public StructureService structureService(StructureRepository structureRepository) {
    return new StructureServiceImpl(structureRepository);
  }
}
