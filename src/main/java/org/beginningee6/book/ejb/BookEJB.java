package org.beginningee6.book.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.beginningee6.book.jpa.Book;

/**
 * Bookエンティティの永続化や検索を処理する
 * ステートレス・セッションBean。
 */
@Stateless
public class BookEJB {

	/**
	 * EJBコンテナによって注入される永続性コンテキスト
	 */
	@PersistenceContext(unitName = "Chapter10ProductionPU")
	EntityManager em;

	/**
	 * データベースから永続化されているBookエンティティを全て取得する。
	 * 
	 * @return データベースに永続化されているBookエンティティのリスト
	 */
	public List<Book> findBooks() {
		TypedQuery<Book> query = em.createNamedQuery("findAllBooks", Book.class);
		
		return query.getResultList();
	}

	/**
	 * データベースにBookエンティティを永続化する。
	 * 
	 * @param book 永続化するBookエンティティ
	 * @return 永続化されたBookエンティティ（主キーが採番されている）
	 */
	public Book createBook(Book book) {
		em.persist(book);
		
		return book;
	}
}
