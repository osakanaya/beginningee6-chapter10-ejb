package org.beginningee6.book.ejb;

import static org.hamcrest.CoreMatchers.is;
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
import org.beginningee6.book.jpa.CD;
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
 * CDエンティティの永続化や検索を処理する
 * ステートレス・セッションBeanのテスト。
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class CDEJBIntegrationTest {
	
	private static final Logger logger = Logger.getLogger(CDEJBIntegrationTest.class
			.getName());

	@Deployment
	public static Archive<?> createDeployment() {
		File dependenciesDir = new File("target/dependency");
		File[] dependencyLibs = dependenciesDir.listFiles();

		WebArchive archive = ShrinkWrap
				.create(WebArchive.class)
				.addPackage(CDEJB.class.getPackage())
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
	CDEJB cdEJB;	// CDエンティティを扱うステートレス・セッションBeanの注入

	@Before
	public void setUp() throws Exception {
		clearData();
	}

	private void clearData() throws Exception {
		userTransaction.begin();
		em.joinTransaction();

		logger.info("Dumping old records...");

		em.createQuery("delete from CD").executeUpdate();
		userTransaction.commit();
	}

	/**
	 * ステートレス・セッションBeanによる
	 * CDエンティティを永続化するテスト。
	 */
	@Test
	public void testCreateACD() throws Exception {
		
		///// 準備 /////
        CD cd = new CD();
        cd.setTitle("Zoot Allures");
        cd.setPrice(12.5F);
        cd.setDescription("Released in October 1976, it is mostly a studio album");
        cd.setGender("male");
        cd.setMusicCompany("RCA Records");
        cd.setNumberOfCDs(2);
        cd.setTotalDuration(74.5F);
        cd.setCover("Cover Image".getBytes("UTF-8"));

        ///// テスト /////
		CD returned = cdEJB.createCD(cd);
        
        ///// 検証 /////
        assertThat(returned.getTitle(), 
        		is("Zoot Allures"));
        assertThat(returned.getPrice(),
        		is(12.5F));
        assertThat(returned.getDescription(),
        		is("Released in October 1976, it is mostly a studio album"));
        assertThat(returned.getGender(),
        		is("male"));
        assertThat(returned.getMusicCompany(),
        		is("RCA Records"));
        assertThat(returned.getNumberOfCDs(),
        		is(2));
        assertThat(new String(returned.getCover(), "UTF-8"),
        		is("Cover Image"));

		em.clear();
		CD persisted = em.find(CD.class, returned.getId());

		assertThat(persisted.getId(), is(returned.getId()));
        assertThat(persisted.getTitle(), 
        		is("Zoot Allures"));
        assertThat(persisted.getPrice(),
        		is(12.5F));
        assertThat(persisted.getDescription(),
        		is("Released in October 1976, it is mostly a studio album"));
        assertThat(persisted.getGender(),
        		is("male"));
        assertThat(persisted.getMusicCompany(),
        		is("RCA Records"));
        assertThat(persisted.getNumberOfCDs(),
        		is(2));
        assertThat(new String(returned.getCover(), "UTF-8"),
        		is("Cover Image"));
	}
	
	/**
	 * 
	 * CDエンティティが永続化されている
	 * 状況でステートレス・セッションBeanにより
	 * CDエンティティを検索により全件取得するテスト。
	 */
	@Test
	public void testFindBooks() throws Exception {
		
		///// 準備 /////
        CD cd = new CD();
        cd.setTitle("Zoot Allures");
        cd.setPrice(12.5F);
        cd.setDescription("Released in October 1976, it is mostly a studio album");
        cd.setGender("male");
        cd.setMusicCompany("RCA Records");
        cd.setNumberOfCDs(2);
        cd.setTotalDuration(74.5F);
        cd.setCover("Cover Image".getBytes("UTF-8"));

		cd = cdEJB.createCD(cd);

        ///// テスト /////
        em.clear();
		List<CD> cds = cdEJB.findCDs();
        
        ///// 検証 /////
        
		assertThat(cds.size(), is(1));
		CD found = cds.get(0);
		
		assertThat(found.getId(), is(cd.getId()));
        assertThat(found.getTitle(), 
        		is("Zoot Allures"));
        assertThat(found.getPrice(),
        		is(12.5F));
        assertThat(found.getDescription(),
        		is("Released in October 1976, it is mostly a studio album"));
        assertThat(found.getGender(),
        		is("male"));
        assertThat(found.getMusicCompany(),
        		is("RCA Records"));
        assertThat(found.getNumberOfCDs(),
        		is(2));
        assertThat(new String(found.getCover(), "UTF-8"),
        		is("Cover Image"));
	}
	
	@Test
	public void testFindCDsWhenNoCDsCreated() throws Exception {
		
        ///// テスト /////
		List<CD> cds = cdEJB.findCDs();
        
        ///// 検証 /////
		assertThat(cds.size(), is(0));
	}

}
