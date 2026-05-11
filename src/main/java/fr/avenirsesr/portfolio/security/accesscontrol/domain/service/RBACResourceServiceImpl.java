package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACResourceService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACResourceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RBACResourceServiceImpl implements RBACResourceService {

  private final RBACResourceRepository resourceRepository;

  @Override
  public Optional<RBACResource> getResourceById(UUID resourceId) {
    log.trace("getResourceById, resourceId: {}", resourceId);
    return resourceRepository.findById(resourceId);
  }

  @Override
  public List<RBACResource> getAllResources() {
    log.trace("getAllResources");
    return resourceRepository.findAll();
  }

  @Override
  public RBACResource createResource(RBACResource resource) {
    log.trace("createResource, resource: {}", resource);
    return resourceRepository.save(resource);
  }

  @Override
  public RBACResource updateResource(RBACResource resource) {
    log.trace("updateResource, resource: {}", resource);

    resourceRepository
        .findById(resource.id())
        .orElseThrow(() -> AccessControlNotFoundException.resource(resource.id()));

    return resourceRepository.save(resource);
  }

  @Override
  public void deleteResource(UUID resourceId) {
    log.trace("deleteResource, resourceId: {}", resourceId);
    resourceRepository.deleteById(resourceId);
  }
}
