package org.beginningee6.book.ejb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.beginningee6.book.ejb.util.IntegrationTest;
import org.beginningee6.book.jpa.Book;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Bookエンティティの永続化や検索を処理する
 * ステートレス・セッションBeanのテスト。
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class BookEJBIntegrationTest {
	
	private static final Logger logger = Logger.getLogger(BookEJBIntegrationTest.class
			.getName());

	@Deployment
	public static Archive<?> createDeployment() {
		File dependenciesDir = new File("target/dependency");
		File[] dependencyLibs = dependenciesDir.listFiles();
		
		WebArchive archive = ShrinkWrap
				.create(WebArchive.class)
				.addPackage(BookEJB.class.getPackage())
				.addPackage(IntegrationTest.class.getPackage())
				.addAsLibraries(dependencyLibs)
				.addAsWebInfResource("jbossas-ds.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		return archive;
	}

	@PersistenceContext
	EntityManager em;

	@Inject
	UserTransaction userTransaction;

	@EJB
	BookEJB bookEJB;	// Bookエンティティを扱うステートレス・セッションBeanの注入

	@Before
	public void setUp() throws Exception {
		clearData();
	}

	private void clearData() throws Exception {
		userTransaction.begin();
		em.joinTransaction();

		logger.info("Dumping old records...");

		em.createQuery("delete from Book").executeUpdate();
		userTransaction.commit();
	}

	/**
	 * ステートレス・セッションBeanによる
	 * Bookエンティティを永続化するテスト。
	 */
	@Test
	public void testCreateABook() throws Exception {
		
		///// 準備 /////
		Book book = new Book();
		book.setTitle("The Hitchhiker's Guide to the Galaxy");
		book.setPrice(12.5F);
		book.setDescription("Science fiction comedy book");
		book.setIsbn("1-84023-742-2");
		book.setNbOfPage(354);
		book.setIllustrations(false);

        ///// テスト /////
        
		Book returned = bookEJB.createBook(book);
        
        ///// 検証 /////
		assertThat(returned.getId(), is(notNullValue()));
        assertThat(returned.getTitle(), 		is("The Hitchhiker's Guide to the Galaxy"));
        assertThat(returned.getPrice(), 		is(12.5F));
        assertThat(returned.getDescription(), 	is("Science fiction comedy book"));
        assertThat(returned.getIsbn(), 		is("1-84023-742-2"));
        assertThat(returned.getNbOfPage(), 	is(354));
        assertThat(returned.getIllustrations(),is(false));

		em.clear();
		Book persisted = em.find(Book.class, returned.getId());

		assertThat(persisted.getId(), is(returned.getId()));
        assertThat(persisted.getTitle(), 		is("The Hitchhiker's Guide to the Galaxy"));
        assertThat(persisted.getPrice(), 		is(12.5F));
        assertThat(persisted.getDescription(), 	is("Science fiction comedy book"));
        assertThat(persisted.getIsbn(), 		is("1-84023-742-2"));
        assertThat(persisted.getNbOfPage(), 	is(354));
        assertThat(persisted.getIllustrations(),is(false));
	}
	
	/**
	 * 
	 * Bookエンティティが永続化されている
	 * 状況でステートレス・セッションBeanにより
	 * Bookエンティティを検索により全件取得するテスト。
	 */
	@Test
	public void testFindBooks() throws Exception {
		
		///// 準備 /////
		
		Book book = new Book();
		book.setTitle("The Hitchhiker's Guide to the Galaxy");
		book.setPrice(12.5F);
		book.setDescription("Science fiction comedy book");
		book.setIsbn("1-84023-742-2");
		book.setNbOfPage(354);
		book.setIllustrations(false);

		book = bookEJB.createBook(book);

        ///// テスト /////
        em.clear();
		List<Book> books = bookEJB.findBooks();
        
        ///// 検証 /////
        
		assertThat(books.size(), is(1));
		Book found = books.get(0);
		
		assertThat(found.getId(), 			is(book.getId()));
        assertThat(found.getTitle(), 		is("The Hitchhiker's Guide to the Galaxy"));
        assertThat(found.getPrice(), 		is(12.5F));
        assertThat(found.getDescription(), 	is("Science fiction comedy book"));
        assertThat(found.getIsbn(), 		is("1-84023-742-2"));
        assertThat(found.getNbOfPage(), 	is(354));
        assertThat(found.getIllustrations(),is(false));
	}
	
	@Test
	public void testFindBooksWhenNoBooksCreated() throws Exception {
		
        ///// テスト /////
		List<Book> books = bookEJB.findBooks();
        
        ///// 検証 /////
		assertThat(books.size(), is(0));
	}

}
