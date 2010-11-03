package com.todoroo.astrid.data;

import android.content.Context;

import com.todoroo.andlib.data.ContentResolverDao;
import com.todoroo.andlib.data.TodorooCursor;
import com.todoroo.andlib.sql.Criterion;
import com.todoroo.andlib.sql.Functions;
import com.todoroo.andlib.sql.Query;

/**
 * Data access object for accessing Astrid's {@link Task} table. If you
 * are looking to store extended information about a Task, you probably
 * want to use the {@link MetadataApiDao} object.
 *
 * @author Tim Su <tim@todoroo.com>
 *
 */
public class TaskApiDao extends ContentResolverDao<Task> {

    public TaskApiDao(Context context) {
        super(Task.class, context, Task.CONTENT_URI);
    }

    /**
     * Generates SQL clauses
     */
    public static class TaskCriteria {

        /** @return tasks by id */
        public static Criterion byId(long id) {
            return Task.ID.eq(id);
        }

        /** @return tasks that were deleted */
        public static Criterion isDeleted() {
            return Task.DELETION_DATE.neq(0);
        }

        /** @return tasks that were not deleted */
        public static Criterion notDeleted() {
            return Task.DELETION_DATE.eq(0);
        }

        /** @return tasks that have not yet been completed or deleted */
        public static Criterion activeAndVisible() {
            return Criterion.and(Task.COMPLETION_DATE.eq(0),
                    Task.DELETION_DATE.eq(0),
                    Task.HIDE_UNTIL.lt(Functions.now()));
        }

        /** @return tasks that have not yet been completed or deleted */
        public static Criterion isActive() {
            return Criterion.and(Task.COMPLETION_DATE.eq(0),
                    Task.DELETION_DATE.eq(0));
        }

        /** @return tasks that are not hidden at current time */
        public static Criterion isVisible() {
            return Task.HIDE_UNTIL.lt(Functions.now());
        }

        /** @return tasks that have a due date */
        public static Criterion hasDeadlines() {
            return Task.DUE_DATE.neq(0);
        }

        /** @return tasks that are due before a certain unixtime */
        public static Criterion dueBeforeNow() {
            return Criterion.and(Task.DUE_DATE.gt(0), Task.DUE_DATE.lt(Functions.now()));
        }

        /** @return tasks that are due after a certain unixtime */
        public static Criterion dueAfterNow() {
            return Task.DUE_DATE.gt(Functions.now());
        }

        /** @return tasks completed before a given unixtime */
        public static Criterion completed() {
            return Criterion.and(Task.COMPLETION_DATE.gt(0), Task.COMPLETION_DATE.lt(Functions.now()));
        }

        /** @return tasks that have a blank or null title */
        @SuppressWarnings("nls")
        public static Criterion hasNoTitle() {
            return Criterion.or(Task.TITLE.isNull(), Task.TITLE.eq(""));
        }

    }

    /**
     * Count tasks matching criterion
     * @param criterion
     * @return # of tasks matching
     */
    public int countTasks(Criterion criterion) {
        TodorooCursor<Task> cursor = query(Query.select(Task.ID).where(criterion));
        try {
            return cursor.getCount();
        } finally {
            cursor.close();
        }
    }

    /**
     * Count tasks matching query tepmlate
     * @param queryTemplate
     * @return # of tasks matching
     */
    public int countTasks(String queryTemplate) {
        TodorooCursor<Task> cursor = query(Query.select(Task.ID).withQueryTemplate(queryTemplate));
        try {
            return cursor.getCount();
        } finally {
            cursor.close();
        }
    }

}
