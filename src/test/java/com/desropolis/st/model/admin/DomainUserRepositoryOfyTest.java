package com.desropolis.st.model.admin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/test-context.xml" })
public class DomainUserRepositoryOfyTest {

	@Autowired
	private LocalServiceTestHelper helper;

	@Autowired
	@Qualifier("ofy")
	DomainUserRepository repo;

	@Autowired
	ObjectifyFactory objectifyFactory;

	@Before
	public void setUp() {

		helper.setUp();

		Objectify ofy = objectifyFactory.begin();

		List<String> roles = new ArrayList<String>();
		roles.add("ROLE_USER");

		DomainUser joyce = new DomainUser();
		joyce.setEmail("joyce@desropolis.com");
		joyce.setDomain("desropolis.com");
		joyce.setRoles(roles);
		ofy.put(joyce);

		roles = new ArrayList<String>();
		roles.add("ROLE_USER");
		roles.add("ROLE_DOMAIN_ADMIN");
		DomainUser dean = new DomainUser();
		dean.setOpenSocialViewerId("1234567890");
		dean.setEmail("dean@desropolis.com");
		dean.setDomain("desropolis.com");
		dean.setRoles(roles);
		ofy.put(dean);

	}

	@Test
	public void findUserByEmail() {

		DomainUser dean = repo.findByEmail("desropolis.com",
				"dean@desropolis.com");
		assertNotNull(dean);
		assertEquals(2, dean.getRoles().size());

		repo.findByEmail(null, null);

	}

	@Test
	public void findUserByOpenSocialViewerId() {

		DomainUser dean = repo.findByOpenSocialViewerId("desropolis.com",
				"1234567890");
		assertNotNull(dean);
		DomainUser joyce = repo
				.findByOpenSocialViewerId("desropolis.com", null);
		assertNull(joyce);

	}

	@Test
	public void saveUser() {

		DomainUser joyce = repo.findByEmail("desropolis.com",
				"joyce@desropolis.com");
		assertNotNull(joyce);
		assertEquals(1, joyce.getRoles().size());

		joyce.setOpenSocialViewerId("0987654321");
		repo.save(joyce);

		repo.findByOpenSocialViewerId("desropolis.com", "0987654321");
		assertNotNull(joyce);
		assertEquals("0987654321", joyce.getOpenSocialViewerId());
		assertEquals("desropolis.com", joyce.getDomain());
		assertEquals("joyce@desropolis.com", joyce.getEmail());

		DomainUser brian = new DomainUser();
		brian.setDomain("desropolis.com");
		brian.setEmail("brian@desropolis.com");
		brian.setOpenSocialViewerId("13579");

		repo.save(brian);
		DomainUser brian2 = repo.findByOpenSocialViewerId("desropolis.com",
				"13579");
		assertEquals(brian, brian2);
		brian2 = repo.findByEmail("desropolis.com", "brian@desropolis.com");
		assertEquals(brian, brian2);

	}

	@Test
	public void allUsers() {

		List<DomainUser> users = repo.listAll("desropolis.com");
		assertNotNull(users);
		assertEquals(2, users.size());

	}

}
