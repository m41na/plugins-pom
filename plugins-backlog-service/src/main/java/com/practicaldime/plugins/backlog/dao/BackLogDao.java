package com.practicaldime.plugins.backlog.dao;

import com.practicaldime.common.entity.todos.BackLogList;
import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.DaoStatus;

import java.util.List;
import java.util.Map;

public interface BackLogDao extends DaoStatus {

    AResult<BackLogList> createList(BackLogList list);
    
    AResult<Boolean> existsInList(Long listId, String item);

    AResult<BackLogList> addToList(Long listId, String item);

    AResult<BackLogList> dropFromList(Long listId, Long item);

    AResult<BackLogList> updateCompleted(Long listId, Long item);

	AResult<BackLogList> updateItem(Long list, Long item, String name);

    AResult<BackLogList> renameItem(Long list, String item, String name);

    AResult<BackLogList> findListById(Long listId);

    AResult<List<BackLogList>> findListsByOwner(Long ownerId);

    AResult<Map<String, List<BackLogList>>> findAllLists(int start, int size);

    AResult<Integer> renameBackLogList(Long listId, String title);

    AResult<Integer> deleteBackLogList(Long listId);

    AResult<Integer> emptyBackLogList(Long listId);
}
