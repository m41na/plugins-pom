package com.practicaldime.plugins.backlog.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.common.util.AppResult;
import com.practicaldime.common.util.CatchExceptions;
import com.practicaldime.common.util.Validatable;
import com.practicaldime.domain.backlog.BackLogItem;
import com.practicaldime.domain.backlog.BackLogList;
import com.practicaldime.plugins.backlog.dao.BackLogDao;

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
    public AppResult<BackLogList> createBackLogList(BackLogList list) {
        return backLogDao.createList(list);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public AppResult<BackLogList> addBackLogItem(long list, BackLogItem item) {
        //check if item has id
        AppResult<Boolean> checkExists = backLogDao.existsInList(list, item.getTask());
        if(checkExists.getError() == null){
            Boolean exists = checkExists.getEntity();
            if(!exists){
                return backLogDao.addToList(list, item.getTask());
            }
            else{
                return new AppResult<>(400, "Todo Item already exists in this list");
            }
        }
        else{
            return new AppResult<>(checkExists.getStatus());
        }
    }

    @Override
    public AppResult<BackLogList> updateCompleted(long list, long item) {
        return backLogDao.updateCompleted(list, item);
    }

    @Override
	public AppResult<BackLogList> updateBackLogItem(Long listId, Long itemId, String name){
    	return backLogDao.updateItem(listId, itemId, name);
    }

    @Override
    public AppResult<BackLogList> renameBackLogItem(long list, String item, String name) {
        return backLogDao.renameItem(list, item, name);
    }

    @Override
    public AppResult<BackLogList> removeBackLogItem(long list, long item) {
        return backLogDao.dropFromList(list, item);
    }

    @Override
    public AppResult<BackLogList> getBackLogListById(long listId) {
        return backLogDao.findListById(listId);
    }

    @Override
    public AppResult<List<BackLogList>> getAllBackLogListsByOwner(long ownerId) {
        return backLogDao.findListsByOwner(ownerId);
    }

    @Override
    public AppResult<Map<String, List<BackLogList>>> getAllBackLogLists(int start, int size) {
        return backLogDao.findAllLists(start, size);
    }

    @Override
    public AppResult<Integer> renameBackLogList(Long listId, String title) {
        return backLogDao.renameBackLogList(listId, title);
    }

    @Override
    public AppResult<Integer> deleteBackLogList(long listId) {
        return backLogDao.deleteBackLogList(listId);
    }

    @Override
    public AppResult<Integer> emptyBackLogList(long listId) {
        return backLogDao.emptyBackLogList(listId);
    }
}
