package org.beginningee6.book.ejb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.beginningee6.book.ejb.util.UnitTest;
import org.beginningee6.book.jpa.CD;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CDEJBTest {

	@Test
	public void testFindCDs() throws Exception {
		// Setup
		CD cd1 = new CD();
		cd1.setTitle("cd1 title");
		CD cd2 = new CD();
		cd2.setTitle("cd2 title");

		EntityManager em = mock(EntityManager.class);
		@SuppressWarnings("unchecked")
		TypedQuery<CD> query = mock(TypedQuery.class);
		when(em.createNamedQuery("findAllCDs", CD.class)).thenReturn(query);
		
		List<CD> expected = new ArrayList<CD>();
		expected.add(cd1);
		expected.add(cd2);
		when(query.getResultList()).thenReturn(expected);
		
		CDEJB ejb = new CDEJB();
		ejb.em = em;
		
		// Execute
		List<CD> cds = ejb.findCDs();
		
		// Verify
		verify(em).createNamedQuery("findAllCDs", CD.class);
		verify(query).getResultList();

		assertThat(cds.size(), is(2));
		assertThat(cds.get(0).getTitle(), is("cd1 title"));
		assertThat(cds.get(1).getTitle(), is("cd2 title"));
	}
	
	@Test
	public void testCreateCD() throws Exception {
		// Setup
		EntityManager em = mock(EntityManager.class);
		CDEJB ejb = new CDEJB();
		ejb.em = em;
		
		CD cd = new CD();
		cd.setTitle("cd title");
		
		// Execute
		CD persisted = ejb.createCD(cd);
		
		// Verify
		verify(em).persist(cd);

		assertThat(persisted.getTitle(), is("cd title"));
	}
}
