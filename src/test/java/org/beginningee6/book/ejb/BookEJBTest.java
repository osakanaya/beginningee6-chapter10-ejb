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
import org.beginningee6.book.jpa.Book;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BookEJBTest {

	@Test
	public void testFindBooks() throws Exception {
		// Setup
		Book book1 = new Book();
		book1.setTitle("book1 title");
		Book book2 = new Book();
		book2.setTitle("book2 title");

		EntityManager em = mock(EntityManager.class);
		@SuppressWarnings("unchecked")
		TypedQuery<Book> query = mock(TypedQuery.class);
		when(em.createNamedQuery("findAllBooks", Book.class)).thenReturn(query);
		
		List<Book> expected = new ArrayList<Book>();
		expected.add(book1);
		expected.add(book2);
		when(query.getResultList()).thenReturn(expected);
		
		BookEJB ejb = new BookEJB();
		ejb.em = em;
		
		// Execute
		List<Book> books = ejb.findBooks();
		
		// Verify
		verify(em).createNamedQuery("findAllBooks", Book.class);
		verify(query).getResultList();

		assertThat(books.size(), is(2));
		assertThat(books.get(0).getTitle(), is("book1 title"));
		assertThat(books.get(1).getTitle(), is("book2 title"));
	}
	
	@Test
	public void testCreateBook() throws Exception {
		// Setup
		EntityManager em = mock(EntityManager.class);
		BookEJB ejb = new BookEJB();
		ejb.em = em;
		
		Book book = new Book();
		book.setTitle("book title");
		
		// Execute
		Book persisted = ejb.createBook(book);
		
		// Verify
		verify(em).persist(book);

		assertThat(persisted.getTitle(), is("book title"));
	}
}
