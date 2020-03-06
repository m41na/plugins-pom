package com.practicaldime.plugins.backlog.service;

import com.practicaldime.common.entity.todos.BackLogItem;
import com.practicaldime.common.entity.todos.BackLogList;
import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.CatchExceptions;
import com.practicaldime.common.util.Validatable;
import com.practicaldime.plugins.backlog.dao.BackLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("BackLogService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Validatable
@CatchExceptions
public class BackLogServiceImpl implements BackLogService {

    @Autowired
    private BackLogDao backLogDao;

    public BackLogDao getTodoDao() {
        return backLogDao;
    }

    public void setTodoDao(BackLogDao backLogDao) {
        this.backLogDao = backLogDao;
    }

    @Override
    public AResult<BackLogList> createBackLogList(BackLogList list) {
        return backLogDao.createList(list);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public AResult<BackLogList> addBackLogItem(long list, BackLogItem item) {
        //check if item has id
        AResult<Boolean> checkExists = backLogDao.existsInList(list, item.getTask());
        if (checkExists.errors.isEmpty()) {
            Boolean exists = checkExists.data;
            if (!exists) {
                return backLogDao.addToList(list, item.getTask());
            } else {
                return new AResult<>("Todo Item already exists in this list", 400);
            }
        } else {
            return new AResult<>(checkExists.errorString(), 200);
        }
    }

    @Override
    public AResult<BackLogList> updateCompleted(long list, long item) {
        return backLogDao.updateCompleted(list, item);
    }

    @Override
    public AResult<BackLogList> updateBackLogItem(Long listId, Long itemId, String name) {
        return backLogDao.updateItem(listId, itemId, name);
    }

    @Override
    public AResult<BackLogList> renameBackLogItem(long list, String item, String name) {
        return backLogDao.renameItem(list, item, name);
    }

    @Override
    public AResult<BackLogList> removeBackLogItem(long list, long item) {
        return backLogDao.dropFromList(list, item);
    }

    @Override
    public AResult<BackLogList> getBackLogListById(long listId) {
        return backLogDao.findListById(listId);
    }

    @Override
    public AResult<List<BackLogList>> getAllBackLogListsByOwner(long ownerId) {
        return backLogDao.findListsByOwner(ownerId);
    }

    @Override
    public AResult<Map<String, List<BackLogList>>> getAllBackLogLists(int start, int size) {
        return backLogDao.findAllLists(start, size);
    }

    @Override
    public AResult<Integer> renameBackLogList(Long listId, String title) {
        return backLogDao.renameBackLogList(listId, title);
    }

    @Override
    public AResult<Integer> deleteBackLogList(long listId) {
        return backLogDao.deleteBackLogList(listId);
    }

    @Override
    public AResult<Integer> emptyBackLogList(long listId) {
        return backLogDao.emptyBackLogList(listId);
    }
}
