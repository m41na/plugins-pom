package com.practicaldime.plugins.backlog.service;

import java.util.List;
import java.util.Map;

import com.practicaldime.common.entity.todos.BackLogItem;
import com.practicaldime.common.entity.todos.BackLogList;
import com.practicaldime.common.util.AResult;

public interface BackLogService {

    //BackLogList functionality
    AResult<BackLogList> createBackLogList(BackLogList list);

    AResult<BackLogList> addBackLogItem(long listId, BackLogItem backlog);

    AResult<BackLogList> updateCompleted(long listId, long itemId);

	AResult<BackLogList> updateBackLogItem(Long listId, Long itemId, String name);

    AResult<BackLogList> renameBackLogItem(long listId, String listItem, String newName);

    AResult<BackLogList> removeBackLogItem(long listId, long itemId);

    AResult<BackLogList> getBackLogListById(long listId);

    AResult<List<BackLogList>> getAllBackLogListsByOwner(long ownerId);

    AResult<Map<String, List<BackLogList>>> getAllBackLogLists(int start, int size);

    AResult<Integer> renameBackLogList(Long listId, String title);

    AResult<Integer> deleteBackLogList(long listId);

    AResult<Integer> emptyBackLogList(long listId);
}
