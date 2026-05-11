package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResourceType;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACResourceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RBACResourceServiceImplTest {

  private static final UUID RESOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  private static final UUID UNKNOWN_RESOURCE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000999");

  private static final UUID RESOURCE_TYPE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000101");

  @Mock private RBACResourceRepository resourceRepository;

  private RBACResourceServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RBACResourceServiceImpl(resourceRepository);
  }

  @Test
  void getResourceByIdReturnsResourceWhenFound() {
    RBACResource resource = resource();

    when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));

    Optional<RBACResource> result = service.getResourceById(RESOURCE_ID);

    assertTrue(result.isPresent());
    assertEquals(resource, result.orElseThrow());

    verify(resourceRepository).findById(RESOURCE_ID);
    verifyNoMoreInteractions(resourceRepository);
  }

  @Test
  void getResourceByIdReturnsEmptyWhenNotFound() {
    when(resourceRepository.findById(UNKNOWN_RESOURCE_ID)).thenReturn(Optional.empty());

    Optional<RBACResource> result = service.getResourceById(UNKNOWN_RESOURCE_ID);

    assertTrue(result.isEmpty());

    verify(resourceRepository).findById(UNKNOWN_RESOURCE_ID);
    verifyNoMoreInteractions(resourceRepository);
  }

  @Test
  void getAllResourcesReturnsRepositoryResources() {
    RBACResource resource = resource();

    when(resourceRepository.findAll()).thenReturn(List.of(resource));

    List<RBACResource> result = service.getAllResources();

    assertThat(result).containsExactly(resource);

    verify(resourceRepository).findAll();
    verifyNoMoreInteractions(resourceRepository);
  }

  @Test
  void getAllResourcesReturnsEmptyListWhenRepositoryIsEmpty() {
    when(resourceRepository.findAll()).thenReturn(List.of());

    List<RBACResource> result = service.getAllResources();

    assertThat(result).isEmpty();

    verify(resourceRepository).findAll();
    verifyNoMoreInteractions(resourceRepository);
  }

  @Test
  void createResourceSavesResource() {
    RBACResource resourceToCreate = resourceWithoutId();
    RBACResource savedResource = resource();

    when(resourceRepository.save(resourceToCreate)).thenReturn(savedResource);

    RBACResource result = service.createResource(resourceToCreate);

    assertEquals(savedResource, result);
    assertEquals(RESOURCE_ID, result.id());

    verify(resourceRepository).save(resourceToCreate);
    verifyNoMoreInteractions(resourceRepository);
  }

  @Test
  void updateResourceSavesResourceWhenExistingResourceIsFound() {
    RBACResource resource = resource();

    when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
    when(resourceRepository.save(resource)).thenReturn(resource);

    RBACResource result = service.updateResource(resource);

    assertEquals(resource, result);

    verify(resourceRepository).findById(RESOURCE_ID);
    verify(resourceRepository).save(resource);
    verifyNoMoreInteractions(resourceRepository);
  }

  @Test
  void updateResourceThrowsWhenResourceDoesNotExist() {
    RBACResource resource = resource();

    when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());

    AccessControlNotFoundException exception =
        assertThrows(AccessControlNotFoundException.class, () -> service.updateResource(resource));

    assertEquals(
        "Resource not found, ID: 00000000-0000-0000-0000-000000000001", exception.getMessage());

    verify(resourceRepository).findById(RESOURCE_ID);
    verifyNoMoreInteractions(resourceRepository);
  }

  @Test
  void deleteResourceDeletesById() {
    service.deleteResource(RESOURCE_ID);

    verify(resourceRepository).deleteById(RESOURCE_ID);
    verifyNoMoreInteractions(resourceRepository);
  }

  private RBACResource resource() {
    return new RBACResource(
        RESOURCE_ID,
        "ptf_0000",
        new RBACResourceType(RESOURCE_TYPE_ID, "PORTFOLIO", "Resource of type portfolio"));
  }

  private RBACResource resourceWithoutId() {
    return new RBACResource(
        null,
        "ptf_0000",
        new RBACResourceType(RESOURCE_TYPE_ID, "PORTFOLIO", "Resource of type portfolio"));
  }
}
