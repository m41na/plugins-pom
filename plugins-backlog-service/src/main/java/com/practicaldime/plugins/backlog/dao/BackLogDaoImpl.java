package com.practicaldime.plugins.backlog.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.practicaldime.common.entity.todos.BackLogItem;
import com.practicaldime.common.entity.todos.BackLogList;
import com.practicaldime.common.entity.users.AccRole;
import com.practicaldime.common.entity.users.AccStatus;
import com.practicaldime.common.entity.users.Account;
import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.SqliteDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class BackLogDaoImpl implements BackLogDao {

    private final NamedParameterJdbcTemplate template;

    @Autowired
    public BackLogDaoImpl(DataSource ds) {
        template = new NamedParameterJdbcTemplate(ds);
    }

    @Override
    public AResult<BackLogList> createList(BackLogList list) {
        Map<String, Object> params = new HashMap<>();
        params.put("list_title", list.getTitle());
        params.put("list_owner", list.getOwner().getId());

        String sql = "insert into tbl_backlog_list (list_title, list_owner, list_created_ts) values (:list_title, :list_owner, datetime('now'))";

        KeyHolder holder = new GeneratedKeyHolder();
        int res = template.update(sql, new MapSqlParameterSource(params), holder);
        list.setId(holder.getKey().longValue());

        return (res > 0) ? new AResult<>(list) : new AResult<>("failed to create new backlog list", ROW_NOT_FOUND);
    }

    @Override
    public AResult<Boolean> existsInList(Long listId, String item) {
        Map<String, Object> params = new HashMap<>();
        params.put("list_id", listId);
        params.put("item_name", item);

        String sql = "select count(item_name) from tbl_backlog_item where item_name=:item_name and fk_list_id=:list_id";
        Boolean exists = template.queryForObject(sql, params, Boolean.class);
        return new AResult<>(exists);
    }

    @Override
    public AResult<BackLogList> addToList(Long list, String item) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_list_id", list);
        params.put("item_name", item);
        params.put("is_done", false);

        String sql = "insert into tbl_backlog_item (item_name, is_done, fk_list_id, item_created_ts) values (:item_name, :is_done, :fk_list_id, datetime('now'))";

        KeyHolder holder = new GeneratedKeyHolder();
        int res = template.update(sql, new MapSqlParameterSource(params), holder);

        return (res > 0) ? findListById(list) : new AResult<>( "error while adding item to list", ROW_NOT_FOUND);
    }

    @Override
    public AResult<BackLogList> dropFromList(Long list, Long item) {
        Map<String, Object> params = new HashMap<>();
        params.put("list_id", list);
        params.put("item_id", item);

        String sql = "delete from tbl_backlog_item where fk_list_id = :list_id and item_id = :item_id";

        int res = template.update(sql, params);
        return (res > 0) ? findListById(list) : new AResult<>("error while dropping item from list", ROW_NOT_FOUND);
    }

    @Override
    public AResult<BackLogList> updateCompleted(Long list, Long item) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_list_id", list);
        params.put("item_id", item);

        String sql = "update tbl_backlog_item set is_done=not is_done where fk_list_id=:fk_list_id and item_id=:item_id";

        int res = template.update(sql, params);

        return (res > 0) ? findListById(list) : new AResult<>("error while updating done status of item in list", ROW_NOT_FOUND);
    }

    @Override
    public AResult<BackLogList> updateItem(Long list, Long item, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_list_id", list);
        params.put("item_id", item);
        params.put("item_name", name);

        String query = "update tbl_backlog_item set item_name=:item_name where fk_list_id = :fk_list_id and item_id = :item_id";
        int rows = template.update(query, params);

        return (rows > 0) ? findListById(list) : new AResult<>("error while updating item in list", ROW_NOT_FOUND);
    }

    @Override
    public AResult<BackLogList> renameItem(Long list, String item, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_list_id", list);
        params.put("item_name", item);
        params.put("new_name", name);

        String sql = "update tbl_backlog_item set item_name=:new_name where fk_list_id=:fk_list_id and item_name=:item_name";

        int res = template.update(sql, params);

        return (res > 0) ? findListById(list) : new AResult<>("error while renaming item in list", ROW_NOT_FOUND);
    }

    @Override
    public AResult<BackLogList> findListById(Long listId) {
        Map<String, Object> params = new HashMap<>();
        params.put("list_id", listId);

        String sql = "SELECT * FROM tbl_backlog_list l"
                + " left join tbl_backlog_item t on t.fk_list_id = l.list_id WHERE l.list_id=:list_id";

        BackLogList list = template.query(sql, params, (rs) -> {
            BackLogList BackLogList = new BackLogList();
            BackLogList.setItems(new ArrayList<>());
            int rowNum = 0;
            while (rs.next()) {
                BackLogItem BackLogItem = BackLogItemMapper().mapRow(rs, ++rowNum);
                if (BackLogItem.getId() > 0) {
                    BackLogList.getItems().add(BackLogItem);
                }
                //populate list attributes
                if (BackLogList.getId() != listId) {
                    BackLogList.setId(rs.getLong("list_id"));
                }
                if (BackLogList.getCreatedTs() == null) {
                    BackLogList.setCreatedTs(SqliteDate.fromString(rs.getString("list_created_ts")));
                }
                if (BackLogList.getTitle() == null) {
                    BackLogList.setTitle(rs.getString("list_title"));
                }
                if (BackLogList.getOwner() == null) {
                    Account owner = new Account();
                    owner.setId(rs.getLong("list_owner"));
                    BackLogList.setOwner(owner);
                }
            }
            return BackLogList;
        });
        return list.getId() == 0 ? new AResult<>("No list found", ROW_NOT_FOUND) : new AResult<>(list);
    }

    @Override
    public AResult<List<BackLogList>> findListsByOwner(Long ownerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("account_id", ownerId);

        String sql = "SELECT * FROM tbl_backlog_list l "
                + "inner join tbl_account a on l.list_owner = a.account_id "
                + "left join tbl_backlog_item t on t.fk_list_id = l.list_id WHERE l.list_owner=:account_id order by l.list_id";

        List<BackLogList> listOfBackLogLists = template.query(sql, params, (rs) -> {
            List<BackLogList> result = new ArrayList<>();
            BackLogList backlogList = null;
            int rowNum = 0;
            while (rs.next()) {
                long listId = rs.getLong("list_id");
                if (backlogList == null || backlogList.getId() != listId) {
                    backlogList = BackLogListMapper().mapRow(rs, rowNum);
                    Account listOwner = accountMapper().mapRow(rs, rowNum);
                    backlogList.setOwner(listOwner);
                    result.add(backlogList);
                }
                BackLogItem backlogItem = BackLogItemMapper().mapRow(rs, ++rowNum);
                if (backlogItem.getId() != null && backlogItem.getId() > 0) {
                    backlogList.getItems().add(backlogItem);
                }
            }
            return result;
        });
        return new AResult<>(listOfBackLogLists);
    }

    @Override
    public AResult<Map<String, List<BackLogList>>> findAllLists(int start, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", start);
        params.put("limit", size);

        String sql = "SELECT * FROM tbl_backlog_list l "
                + "inner join tbl_account a on a.account_id = l.list_owner "
                + "left join tbl_backlog_item t on t.fk_list_id = l.list_id order by l.list_owner, l.list_id "
                + "limit :limit offset :offset";

        Map<String, List<BackLogList>> mapOfBackLogList = template.query(sql, params, (ResultSet rs) -> {
            Map<String, List<BackLogList>> result = new HashMap<>();
            List<BackLogList> list = null;
            BackLogList backLogList = null;
            Account listOwner = null;
            int rowNum = 0;
            while (rs.next()) {
                Long accountId = rs.getLong("list_owner");
                if (listOwner == null || listOwner.getId() != accountId) {
                    listOwner = accountMapper().mapRow(rs, rowNum);
                    //create lists array
                    list = new ArrayList<>();
                    result.put(rs.getString("username"), list);
                }
                Long listId = rs.getLong("list_id");
                if (backLogList == null || backLogList.getId() != listId) {
                    backLogList = BackLogListMapper().mapRow(rs, rowNum);
                    backLogList.setOwner(listOwner);
                    list.add(backLogList);
                }
                BackLogItem backLogItem = BackLogItemMapper().mapRow(rs, rowNum++);
                if (backLogItem.getId() != null && backLogItem.getId() > 0) {
                    backLogList.getItems().add(backLogItem);
                }
            }
            return result;
        });

        return new AResult<>(mapOfBackLogList);
    }

    @Override
    public AResult<Integer> renameBackLogList(Long listId, String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("list_id", listId);
        params.put("list_title", title);

        String query = "update tbl_backlog_list set list_title=:list_title where list_id = :list_id";
        int rows = template.update(query, params);
        return new AResult<>(rows);
    }

    @Override
    public AResult<Integer> deleteBackLogList(Long listId) {
        Map<String, Object> params = new HashMap<>();
        params.put("list_id", listId);

        String check = "select count(fk_list_id) as rows from tbl_backlog_item where fk_list_id = :list_id";
        int count = template.queryForObject(check, params, Integer.class);

        if (count == 0) {
            String query = "delete from tbl_backlog_list where list_id = :list_id";
            int rows = template.update(query, params);
            return new AResult<>(rows);
        } else {
            return new AResult<>("The list is not empty", 403);
        }
    }

    @Override
    public AResult<Integer> emptyBackLogList(Long listId) {
        Map<String, Object> params = new HashMap<>();
        params.put("list_id", listId);

        String query = "delete from tbl_backlog_item where fk_list_id = :list_id";
        int rows = template.update(query, params);
        return new AResult<>(rows);
    }

    private RowMapper<BackLogList> BackLogListMapper() {
        return (rs, rowNum) -> {
            BackLogList list = new BackLogList();
            list.setId(rs.getLong("list_id"));
            list.setTitle(rs.getString("list_title"));
            list.setCreatedTs(SqliteDate.fromString(rs.getString("list_created_ts")));
            list.setItems(new ArrayList<>());
            return list;
        };
    }

    private RowMapper<BackLogItem> BackLogItemMapper() {
        return (rs, rowNum) -> {
            BackLogItem item = new BackLogItem();
            if (rs.getLong("item_id") != 0) {
                item.setId(rs.getLong("item_id"));
                item.setDone(rs.getBoolean("is_done"));
                item.setCreatedTs(SqliteDate.fromString(rs.getString("item_created_ts")));
                item.setTask(rs.getString("item_name"));
            }
            return item;
        };
    }

    private RowMapper<Account> accountMapper() {
        return (rs, rowNum) -> {
            Account acc = new Account();
            acc.setId(rs.getLong("account_id"));
            acc.setUsername(rs.getString("username"));
            acc.setRole(AccRole.valueOf(rs.getString("acc_role")));
            acc.setStatus(AccStatus.valueOf(rs.getString("acc_status")));
            acc.setCreatedTs(SqliteDate.fromString(rs.getString("account_created_ts")));
            return acc;
        };
    }

    protected <T> ResultSetExtractor<T> resultDump() {
        return (rs) -> {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) {
                        System.out.print(",  ");
                    }
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println();
            }
            return null;
        };
    }
}
