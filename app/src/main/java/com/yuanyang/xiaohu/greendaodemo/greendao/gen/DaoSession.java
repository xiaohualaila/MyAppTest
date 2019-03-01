package com.yuanyang.xiaohu.greendaodemo.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.yuanyang.xiaohu.door.bean.CardBean;
import com.yuanyang.xiaohu.door.bean.CardRecord;
import com.yuanyang.xiaohu.door.bean.CodeRecord;
import com.yuanyang.xiaohu.door.bean.SharepreferenceBean;
import com.yuanyang.xiaohu.door.bean.RecordLogModel;

import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardRecordDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CodeRecordDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.SharepreferenceBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.RecordLogModelDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cardBeanDaoConfig;
    private final DaoConfig cardRecordDaoConfig;
    private final DaoConfig codeRecordDaoConfig;
    private final DaoConfig sharepreferenceBeanDaoConfig;
    private final DaoConfig recordLogModelDaoConfig;

    private final CardBeanDao cardBeanDao;
    private final CardRecordDao cardRecordDao;
    private final CodeRecordDao codeRecordDao;
    private final SharepreferenceBeanDao sharepreferenceBeanDao;
    private final RecordLogModelDao recordLogModelDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        cardBeanDaoConfig = daoConfigMap.get(CardBeanDao.class).clone();
        cardBeanDaoConfig.initIdentityScope(type);

        cardRecordDaoConfig = daoConfigMap.get(CardRecordDao.class).clone();
        cardRecordDaoConfig.initIdentityScope(type);

        codeRecordDaoConfig = daoConfigMap.get(CodeRecordDao.class).clone();
        codeRecordDaoConfig.initIdentityScope(type);

        sharepreferenceBeanDaoConfig = daoConfigMap.get(SharepreferenceBeanDao.class).clone();
        sharepreferenceBeanDaoConfig.initIdentityScope(type);

        recordLogModelDaoConfig = daoConfigMap.get(RecordLogModelDao.class).clone();
        recordLogModelDaoConfig.initIdentityScope(type);

        cardBeanDao = new CardBeanDao(cardBeanDaoConfig, this);
        cardRecordDao = new CardRecordDao(cardRecordDaoConfig, this);
        codeRecordDao = new CodeRecordDao(codeRecordDaoConfig, this);
        sharepreferenceBeanDao = new SharepreferenceBeanDao(sharepreferenceBeanDaoConfig, this);
        recordLogModelDao = new RecordLogModelDao(recordLogModelDaoConfig, this);

        registerDao(CardBean.class, cardBeanDao);
        registerDao(CardRecord.class, cardRecordDao);
        registerDao(CodeRecord.class, codeRecordDao);
        registerDao(SharepreferenceBean.class, sharepreferenceBeanDao);
        registerDao(RecordLogModel.class, recordLogModelDao);
    }
    
    public void clear() {
        cardBeanDaoConfig.clearIdentityScope();
        cardRecordDaoConfig.clearIdentityScope();
        codeRecordDaoConfig.clearIdentityScope();
        sharepreferenceBeanDaoConfig.clearIdentityScope();
        recordLogModelDaoConfig.clearIdentityScope();
    }

    public CardBeanDao getCardBeanDao() {
        return cardBeanDao;
    }

    public CardRecordDao getCardRecordDao() {
        return cardRecordDao;
    }

    public CodeRecordDao getCodeRecordDao() {
        return codeRecordDao;
    }

    public SharepreferenceBeanDao getSharepreferenceBeanDao() {
        return sharepreferenceBeanDao;
    }

    public RecordLogModelDao getRecordLogModelDao() {
        return recordLogModelDao;
    }

}
