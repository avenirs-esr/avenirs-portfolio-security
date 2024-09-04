package fr.avenirsesr.portfolio.security.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.repositories.RBACScopeSpecification;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures.sql")
@Transactional
class RBACScopeServiceTest {
	
	private static final Long RES1_ID=1L;
	private static final Long RES2_ID=2L;
	private static final Long RES3_ID=3L;
	private static final Long RES4_ID=4L;
	
	private final static String[] EXPECTED_SCOPES_RES1 = {"scope_0000", "scope_0003"};
	private final static String[] EXPECTED_SCOPES_RES3 = {"scope_0002"};
	private final static String[] EXPECTED_SCOPES_RES1_RES3 = {"scope_0000",  "scope_0002", "scope_0003"};
	private final static String[] EXPECTED_SCOPES_RES_ALL = {"scope_0000",  "scope_0001",  "scope_0002", "scope_0003"};
	
	
	
	@Autowired 
	private RBACScopeService scopeService;
	
	@Test
	void testGetAllScopesByPredicate() {
		List<RBACScope> actual = scopeService.getAllScopesByPredicate(RBACScopeSpecification.filterByResources(RES1_ID));
		assertThat(actual).hasSize(EXPECTED_SCOPES_RES1.length);
		assertThat(actual.stream().map(s->s.getName())).contains(EXPECTED_SCOPES_RES1);
		
		actual = scopeService.getAllScopesByPredicate(RBACScopeSpecification.filterByResources(RES3_ID));
		assertThat(actual).hasSize(EXPECTED_SCOPES_RES3.length);
		assertThat(actual.stream().map(s->s.getName())).contains(EXPECTED_SCOPES_RES3);
		
		actual = scopeService.getAllScopesByPredicate(RBACScopeSpecification.filterByResources(RES4_ID));
		assertThat(actual).isEmpty();
		
		
		actual = scopeService.getAllScopesByPredicate(RBACScopeSpecification.filterByResources(RES1_ID, RES3_ID));
		assertThat(actual).hasSize(EXPECTED_SCOPES_RES1_RES3.length);
		assertThat(actual.stream().map(s->s.getName())).contains(EXPECTED_SCOPES_RES1_RES3);
		
		// Res4 is not referenced by any scope, so adding it id should not change the result.
		actual = scopeService.getAllScopesByPredicate(RBACScopeSpecification.filterByResources(RES1_ID, RES4_ID));
		assertThat(actual).hasSize(EXPECTED_SCOPES_RES1.length);
		assertThat(actual.stream().map(s->s.getName())).contains(EXPECTED_SCOPES_RES1);
		
		actual = scopeService.getAllScopesByPredicate(RBACScopeSpecification.filterByResources(RES1_ID, RES2_ID, RES3_ID,RES4_ID));
		assertThat(actual).hasSize(EXPECTED_SCOPES_RES_ALL.length);
		assertThat(actual.stream().map(s->s.getName())).contains(EXPECTED_SCOPES_RES_ALL);
		
	}

}
