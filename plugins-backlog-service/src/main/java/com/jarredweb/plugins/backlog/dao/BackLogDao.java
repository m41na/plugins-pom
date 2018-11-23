package com.jarredweb.plugins.backlog.dao;

import java.util.List;
import java.util.Map;

import com.jarredweb.common.util.AppResult;
import com.jarredweb.common.util.DaoStatus;
import com.jarredweb.domain.backlog.BackLogList;

public interface BackLogDao extends DaoStatus{

    AppResult<BackLogList> createList(BackLogList list);
    
    AppResult<Boolean> existsInList(Long listId, String item);

    AppResult<BackLogList> addToList(Long listId, String item);

    AppResult<BackLogList> dropFromList(Long listId, Long item);

    AppResult<BackLogList> updateCompleted(Long listId, Long item);

	AppResult<BackLogList> updateItem(Long list, Long item, String name);

    AppResult<BackLogList> renameItem(Long list, String item, String name);

    AppResult<BackLogList> findListById(Long listId);

    AppResult<List<BackLogList>> findListsByOwner(Long ownerId);

    AppResult<Map<String, List<BackLogList>>> findAllLists(int start, int size);

    AppResult<Integer> renameBackLogList(Long listId, String title);

    AppResult<Integer> deleteBackLogList(Long listId);

    AppResult<Integer> emptyBackLogList(Long listId);
}
