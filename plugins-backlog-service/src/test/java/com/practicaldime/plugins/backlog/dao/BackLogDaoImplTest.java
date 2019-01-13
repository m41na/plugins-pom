package com.practicaldime.plugins.backlog.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.practicaldime.common.util.AppResult;
import com.practicaldime.domain.backlog.BackLogItem;
import com.practicaldime.domain.backlog.BackLogList;
import com.practicaldime.domain.users.Account;
import com.practicaldime.plugins.backlog.dao.BackLogDao;

import com.practicaldime.plugins.backlog.config.BackLogDaoTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BackLogDaoTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Sql({"/sql/create-tables.sql"})
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class BackLogDaoImplTest {

    @Autowired
    private BackLogDao tDao;

    @Test
    public void testCreateBackLogList() {
        Account owner = new Account();
        owner.setId(1l);
        // create to-do list
        BackLogList list = new BackLogList();
        list.setTitle("christman list");
        list.setOwner(owner);
        AppResult<BackLogList> result = tDao.createList(list);
        assertEquals("Expecting '5'", 5, result.getEntity().getId());
    }

    @Test
    public void testAddToList() {
        BackLogList list = tDao.findListById(2l).getEntity();
        int initialSize = list.getItems().size();
        int size = 3;
        for (int i = 0; i < size; i++) {
            String item = "name_" + i;
            // add item to list
            AppResult<BackLogList> res3 = tDao.addToList(list.getId(), item);
            assertNotNull(res3);
        }
        BackLogList newList = tDao.findListById(2l).getEntity();
        int newSize = newList.getItems().size();
        assertEquals("Expecting increase by value of 'size'", (initialSize + size), newSize);
    }

    @Test
    public void testDropFromList() {
        //get todo list
        BackLogList list = tDao.findListById(2l).getEntity();
        int itemsInList = list.getItems().size();

        //add item
        String item = "testDropFromList";
        AppResult<BackLogList> addRes = tDao.addToList(list.getId(), item);
        list = addRes.getEntity();
        //assert size is more by 1
        assertEquals("Expected " + (itemsInList + 1), itemsInList + 1, list.getItems().size());
        itemsInList = list.getItems().size();
        
        long itemId = list.getItems().stream().filter(e->e.getTask().equals(item)).findFirst().get().getId();

        //drop item
        AppResult<BackLogList> dropRes = tDao.dropFromList(list.getId(), itemId);
        //get todo list and assert size is less by 1
        assertEquals("Expected " + (itemsInList - 1), itemsInList - 1, dropRes.getEntity().getItems().size());
    }

    @Test
    public void testMarkAsDone() {
        //get todo list
        BackLogList list = tDao.findListById(2l).getEntity();

        //add item
        String item = "testMarkAsDone";
        AppResult<BackLogList> addResult = tDao.addToList(list.getId(), item);
        //get first item no marked as done
        BackLogItem targetItem = addResult.getEntity().getItems().stream().filter(entry -> {
            return !entry.isDone();
        }).findFirst().get();
        //mark item retrieved as done
        targetItem.setDone(true);
        AppResult<BackLogList> updatedList = tDao.updateCompleted(list.getId(), targetItem.getId());

        //verify item matching id of 'addedItem' is done
        updatedList.getEntity().getItems().stream().filter((i) -> (i.getId() == targetItem.getId())).forEachOrdered((i) -> {
            assertTrue(i.isDone());
        });
    }

    @Test
    public void testRenameListItem() {
        //get todo list
        BackLogList list = tDao.findListById(2l).getEntity();

        //add item
        String item = "jonny";
        //get first item in list
        BackLogItem targetItem = list.getItems().get(0);
        AppResult<BackLogList> updatedList = tDao.renameItem(list.getId(), targetItem.getTask(), item);

        //verify item was renamed
        updatedList.getEntity().getItems().stream().filter((i) -> (i.getId() == targetItem.getId())).forEachOrdered((i) -> {
            assertEquals("Expecting same name", item, i.getTask());
        });
    }

    @Test
    public void testFindById() {
        long id = 1;
        BackLogList list = tDao.findListById(id).getEntity();
        assertNotNull(list);
    }

    @Test
    public void testFindListsByOwner() {
        long accountId = 1;
        List<BackLogList> list = tDao.findListsByOwner(accountId).getEntity();
        assertNotNull(list);
        assertEquals("Expecting 3 lists", 3, list.size());
    }

    @Test
    public void testFindAllLists() {
    	int start = 1;
    	int size = 10;
        Map<String, List<BackLogList>> lists = tDao.findAllLists(start, size).getEntity();
        assertNotNull(lists);
        assertEquals("Expecting 3 lists", 3, lists.get("admin").size());
    }

    @Test
    public void testRenameList() {
        //define new name
        String newTitle = "testRenameList";
        AppResult<Integer> updatedList = tDao.renameBackLogList(2l, newTitle);

        //verify item was renamed
        assertEquals("Expecting 1", 1, updatedList.getEntity().intValue());
    }
}
