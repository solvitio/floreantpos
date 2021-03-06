/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.model.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.floreantpos.PosException;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;

public class MenuGroupDAO extends BaseMenuGroupDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public MenuGroupDAO() {
	}

	@SuppressWarnings("unchecked")
	public List<MenuGroup> findEnabledByParent(MenuCategory category) throws PosException {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(MenuGroup.PROP_VISIBLE, Boolean.TRUE));
			criteria.add(Restrictions.eq(MenuGroup.PROP_PARENT, category));

			List<MenuGroup> list = criteria.list();
			for (MenuGroup menuGroup : list) {
				menuGroup.setParent(category);
			}
			
			return list;
		} finally {
			closeSession(session);
		}
	}

	public List<MenuGroup> findByParent(MenuCategory category) throws PosException {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(MenuGroup.PROP_PARENT, category));

			return criteria.list();
		} finally {
			closeSession(session);
		}
	}

	public void releaseParent(List<MenuGroup> menuGroupList) {
		if(menuGroupList == null) {
			return;
		}

		Session session = null;
		Transaction tx = null;

		try {
			session = createNewSession();
			tx = session.beginTransaction();

			for (MenuGroup menuGroup : menuGroupList) {
				menuGroup.setParent(null);
				session.saveOrUpdate(menuGroup);
			}

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			LogFactory.getLog(ShopTableDAO.class).error(e);
			throw new RuntimeException(e);
		} finally {
			closeSession(session);
		}
	}
}