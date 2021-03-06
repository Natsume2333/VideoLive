package com.moxin.videoline.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.moxin.videoline.modle.JsonData;
import com.moxin.videoline.modle.ReadFile;
import com.moxin.videoline.msg.modle.Msg;

import com.moxin.videoline.dao.JsonDataDao;
import com.moxin.videoline.dao.ReadFileDao;
import com.moxin.videoline.dao.MsgDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig jsonDataDaoConfig;
    private final DaoConfig readFileDaoConfig;
    private final DaoConfig msgDaoConfig;

    private final JsonDataDao jsonDataDao;
    private final ReadFileDao readFileDao;
    private final MsgDao msgDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        jsonDataDaoConfig = daoConfigMap.get(JsonDataDao.class).clone();
        jsonDataDaoConfig.initIdentityScope(type);

        readFileDaoConfig = daoConfigMap.get(ReadFileDao.class).clone();
        readFileDaoConfig.initIdentityScope(type);

        msgDaoConfig = daoConfigMap.get(MsgDao.class).clone();
        msgDaoConfig.initIdentityScope(type);

        jsonDataDao = new JsonDataDao(jsonDataDaoConfig, this);
        readFileDao = new ReadFileDao(readFileDaoConfig, this);
        msgDao = new MsgDao(msgDaoConfig, this);

        registerDao(JsonData.class, jsonDataDao);
        registerDao(ReadFile.class, readFileDao);
        registerDao(Msg.class, msgDao);
    }
    
    public void clear() {
        jsonDataDaoConfig.clearIdentityScope();
        readFileDaoConfig.clearIdentityScope();
        msgDaoConfig.clearIdentityScope();
    }

    public JsonDataDao getJsonDataDao() {
        return jsonDataDao;
    }

    public ReadFileDao getReadFileDao() {
        return readFileDao;
    }

    public MsgDao getMsgDao() {
        return msgDao;
    }

}
