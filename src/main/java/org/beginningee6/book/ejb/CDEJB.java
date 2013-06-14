package org.beginningee6.book.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.beginningee6.book.jpa.CD;

/**
 * CDエンティティの永続化や検索を処理する
 * ステートレス・セッションBean。
 */
@Stateless
public class CDEJB {
	
	/**
	 * EJBコンテナによって注入される永続性コンテキスト
	 */
	@PersistenceContext(unitName = "Chapter10ProductionPU")
	EntityManager em;
	
	/**
	 * データベースから永続化されているCDエンティティを全て取得する。
	 * 
	 * @return データベースに永続化されているCDエンティティのリスト
	 */
	public List<CD> findCDs() {
		TypedQuery<CD> query = em.createNamedQuery("findAllCDs", CD.class);
		
		return query.getResultList();
	}

	/**
	 * データベースにCDエンティティを永続化する。
	 * 
	 * @param cd 永続化するCDエンティティ
	 * @return 永続化されたCDエンティティ（主キーが採番されている）
	 */
	public CD createCD(CD cd) {
		em.persist(cd);
		
		return cd;
	}
}
