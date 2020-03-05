package com.practicaldime.plugins.backlog.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.practicaldime.common.entity.todos.BackLogItem;
import com.practicaldime.common.entity.todos.BackLogList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.common.util.AResult;

import com.practicaldime.plugins.backlog.config.BackLogServiceTestConfig;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BackLogServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Sql({"/sql/create-tables.sql"})
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class BackLogServiceImplTest {

    @Autowired
    private BackLogService service;

    @Test
    public void testAddBackLogItem() {
        BackLogList list = service.getBackLogListById(1).data;
        int listSize = list.getItems().size();
        
        AResult<BackLogList> addResult = service.addBackLogItem(list.getId(), new BackLogItem("testAddBackLogItem"));
        assertEquals("Expecting " + (listSize + 1), listSize + 1, addResult.data.getItems().size());
    }

    @Test
    public void testRemoveBackLogItem() {
        BackLogList list = service.getBackLogListById(1).data;
        int listSize = list.getItems().size();
        // get first item in list
        BackLogItem toDrop = list.getItems().get(0);
        AResult<BackLogList> dropResult = service.removeBackLogItem(list.getId(), toDrop.getId());
        assertEquals("Expecting " + (listSize - 1), listSize - 1, dropResult.data.getItems().size());
    }

    @Test
    public void testMarkItemAsDone() {
        BackLogList list = service.getBackLogListById(1).data;
        long notDoneCount = list.getItems().stream().filter(item -> {
            return !item.isDone();
        }).count();
        // get first item in list
        BackLogItem toComplete = list.getItems().get(0);
        toComplete.setDone(true);
        AResult<BackLogList> doneResult = service.updateCompleted(list.getId(), toComplete.getId());
        long newNotDoneCount = doneResult.data.getItems().stream().filter(item -> {
            return !item.isDone();
        }).count();
        assertEquals("Expecting " + (notDoneCount - 1), (notDoneCount - 1), newNotDoneCount);
    }

    @Test
    public void testRenameBackLogItem() {
        BackLogList list = service.getBackLogListById(1).data;
        String item = "testRenameBackLogItem";
        // get first item in list
        BackLogItem todo = list.getItems().get(0);
        AResult<BackLogList> doneResult = service.renameBackLogItem(list.getId(), todo.getTask(), item);
        assertEquals("Expecting  1 item", 1, doneResult.data.getItems().stream().filter(e->e.getTask().equals(item)).count());
    }

    @Test
    public void testGetBackLogListById() {
        AResult<BackLogList> listById = service.getBackLogListById(1);
        assertEquals("Expecting 2", 2, listById.data.getItems().size());
    }

    @Test
    public void testGetBackLogListsByOwner() {
        AResult<List<BackLogList>> listById = service.getAllBackLogListsByOwner(1);
        assertEquals("Expecting 3 lists", 3, listById.data.size());
    }

    @Test
    public void testRenameBackLogList() {
        BackLogList list = service.getBackLogListById(1).data;
        String title = "fancyList";
        AResult<Integer> doneResult = service.renameBackLogList(list.getId(),  title);
        assertEquals("Expecting  1 item", 1, doneResult.data.intValue());
    }
}
