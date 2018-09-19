package com.yuanyang.xiaohu.greendaodemo.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.yuanyang.xiaohu.door.model.CardBean;

import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cardBeanDaoConfig;

    private final CardBeanDao cardBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        cardBeanDaoConfig = daoConfigMap.get(CardBeanDao.class).clone();
        cardBeanDaoConfig.initIdentityScope(type);

        cardBeanDao = new CardBeanDao(cardBeanDaoConfig, this);

        registerDao(CardBean.class, cardBeanDao);
    }
    
    public void clear() {
        cardBeanDaoConfig.clearIdentityScope();
    }

    public CardBeanDao getCardBeanDao() {
        return cardBeanDao;
    }

}
