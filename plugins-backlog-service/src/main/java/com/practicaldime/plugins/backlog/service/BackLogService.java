package com.practicaldime.plugins.backlog.service;

import java.util.List;
import java.util.Map;

import com.practicaldime.common.util.AppResult;
import com.practicaldime.domain.backlog.BackLogItem;
import com.practicaldime.domain.backlog.BackLogList;

public interface BackLogService {

    //BackLogList functionality
    AppResult<BackLogList> createBackLogList(BackLogList list);

    AppResult<BackLogList> addBackLogItem(long listId, BackLogItem backlog);

    AppResult<BackLogList> updateCompleted(long listId, long itemId);

	AppResult<BackLogList> updateBackLogItem(Long listId, Long itemId, String name);

    AppResult<BackLogList> renameBackLogItem(long listId, String listItem, String newName);

    AppResult<BackLogList> removeBackLogItem(long listId, long itemId);

    AppResult<BackLogList> getBackLogListById(long listId);

    AppResult<List<BackLogList>> getAllBackLogListsByOwner(long ownerId);

    AppResult<Map<String, List<BackLogList>>> getAllBackLogLists(int start, int size);

    AppResult<Integer> renameBackLogList(Long listId, String title);

    AppResult<Integer> deleteBackLogList(long listId);

    AppResult<Integer> emptyBackLogList(long listId);
}
