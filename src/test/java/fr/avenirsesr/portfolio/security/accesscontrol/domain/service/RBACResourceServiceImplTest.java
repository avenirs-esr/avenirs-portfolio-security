package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResourceType;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACResourceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  @Nested
  class GivenARBACResourceService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC resource service");
    }

    @Nested
    class WhenGettingResourceById {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting resource by id");
      }

      @Nested
      class AndTheResourceExists {
        private RBACResource resource;
        private Optional<RBACResource> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the resource exists");

          resource = resource();

          when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));

          result = service.getResourceById(RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnResource() {
          BddLogger.then("it should return resource");

          assertTrue(result.isPresent());
          assertEquals(resource, result.orElseThrow());

          verify(resourceRepository).findById(RESOURCE_ID);
          verifyNoMoreInteractions(resourceRepository);
        }
      }

      @Nested
      class AndTheResourceDoesNotExist {
        private Optional<RBACResource> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the resource does not exist");

          when(resourceRepository.findById(UNKNOWN_RESOURCE_ID)).thenReturn(Optional.empty());

          result = service.getResourceById(UNKNOWN_RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(resourceRepository).findById(UNKNOWN_RESOURCE_ID);
          verifyNoMoreInteractions(resourceRepository);
        }
      }
    }

    @Nested
    class WhenGettingAllResources {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all resources");
      }

      @Nested
      class AndTheRepositoryContainsResources {
        private RBACResource resource;
        private List<RBACResource> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains resources");

          resource = resource();

          when(resourceRepository.findAll()).thenReturn(List.of(resource));

          result = service.getAllResources();
        }

        @Test
        void thenItShouldReturnRepositoryResources() {
          BddLogger.then("it should return repository resources");

          assertThat(result).containsExactly(resource);

          verify(resourceRepository).findAll();
          verifyNoMoreInteractions(resourceRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<RBACResource> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(resourceRepository.findAll()).thenReturn(List.of());

          result = service.getAllResources();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(resourceRepository).findAll();
          verifyNoMoreInteractions(resourceRepository);
        }
      }
    }

    @Nested
    class WhenCreatingResource {
      private RBACResource resourceToCreate;
      private RBACResource savedResource;
      private RBACResource result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating resource");

        resourceToCreate = resourceWithoutId();
        savedResource = resource();

        when(resourceRepository.save(resourceToCreate)).thenReturn(savedResource);

        result = service.createResource(resourceToCreate);
      }

      @Test
      void thenItShouldSaveResource() {
        BddLogger.then("it should save resource");

        assertEquals(savedResource, result);
        assertEquals(RESOURCE_ID, result.id());

        verify(resourceRepository).save(resourceToCreate);
        verifyNoMoreInteractions(resourceRepository);
      }
    }

    @Nested
    class WhenUpdatingResource {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("updating resource");
      }

      @Nested
      class AndTheResourceExists {
        private RBACResource resource;
        private RBACResource result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the resource exists");

          resource = resource();

          when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
          when(resourceRepository.save(resource)).thenReturn(resource);

          result = service.updateResource(resource);
        }

        @Test
        void thenItShouldSaveResource() {
          BddLogger.then("it should save resource");

          assertEquals(resource, result);

          verify(resourceRepository).findById(RESOURCE_ID);
          verify(resourceRepository).save(resource);
          verifyNoMoreInteractions(resourceRepository);
        }
      }

      @Nested
      class AndTheResourceDoesNotExist {
        private RBACResource resource;
        private AccessControlNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the resource does not exist");

          resource = resource();

          when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());

          exception =
              assertThrows(
                  AccessControlNotFoundException.class, () -> service.updateResource(resource));
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertEquals(
              "Resource not found, ID: 00000000-0000-0000-0000-000000000001",
              exception.getMessage());

          verify(resourceRepository).findById(RESOURCE_ID);
          verifyNoMoreInteractions(resourceRepository);
        }
      }
    }

    @Nested
    class WhenDeletingResource {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting resource");

        service.deleteResource(RESOURCE_ID);
      }

      @Test
      void thenItShouldDeleteById() {
        BddLogger.then("it should delete by id");

        verify(resourceRepository).deleteById(RESOURCE_ID);
        verifyNoMoreInteractions(resourceRepository);
      }
    }
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
