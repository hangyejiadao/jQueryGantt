

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 项目表Service
 *
 * @author zwh
 * @version 2019-09-30
 */
@Service
@Transactional(readOnly = true)
public class ProjectService extends CrudService<ProjectDao, Project> {


    /**
     * 任务创建规则修改：
     * 取第一个没有依赖规则的任务的开始时间作为固定不变的时间（只取是根节点，且开始时间是最早的任务作为项目的固定不变的时间。）
     * 其他没有依赖规则的任务开始和结束时间优先计算，任务开始时间和这个固定不变的工作日时间差来计算任务开始时间。
     * 最后再统一计算有依赖规则的任务的开始和结束时间。
     **/
    public List<Task> initProjectTask(List<TaskTemplate> taskTemplateList, Project project) {
        //获取最早的时间
        TaskTemplate earlyTask = null;
        Date projectStartDate = project.getStartDate();
        int taskPos = 0;
        for (int i = 0; i < taskTemplateList.size(); i++) {
            TaskTemplate taskItem = taskTemplateList.get(i);
            if (taskItem.getLevel().equals(0) && StringUtils.isBlank(taskItem.getDepends())) {
                if (earlyTask == null) {
                    earlyTask = taskItem;
                    taskPos = i;
                } else {
                    if (earlyTask.getStart().getTime() > taskItem.getStart().getTime()) {
                        earlyTask = taskItem;
                        taskPos = i;
                    }
                }
            }
        }
        if (earlyTask == null) {
            return null;//避免程序异常
        }

        List<Task> taskList = new ArrayList<>();
        //复制属性和计算无依赖规则任务时间，不处理任务文件
        for (int i = 0; i < taskTemplateList.size(); i++) {
            TaskTemplate item = taskTemplateList.get(i);
            Task taskItem = new Task();
            BeanUtils.copyProperties(item, taskItem);
            if (item.getMilestone().equals(ProjectConsts.TASK_MILESTONE_YES)) {
                taskItem.setCanDelete("false");
            }
            taskItem.setId(null);
            taskItem.setProjectId(project.getId());
            if (StringUtils.isNotBlank(taskItem.getDepends())) {
                taskList.add(taskItem);
                continue;
            }
            //无依赖规则任务计算
            //初始化开始结束时间。当前任务和最早开始任务的时间差，只包含工作日
            int subDay = DateUtils.getWorkdayTimeInDate(earlyTask.getStart(), item.getStart());
            Date realStartDate = DateUtils.incrementDateByWorkingDays(projectStartDate, subDay);
            taskItem.setStart(realStartDate);
            Date realEndDate = DateUtils.incrementDateByWorkingDays(realStartDate, taskItem.getDuration());
            taskItem.setEnd(realEndDate);

            taskList.add(taskItem);
        }

        //创建任务依赖规则链接，并put到任务列表中
        //创建没有依赖规则的任务开始和结束时间，获取所有任务的更新链接links
        List<TaskLink> links = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            Task taskItem = taskList.get(i);
            if (StringUtils.isBlank(taskItem.getDepends())) {
                continue;//跳过
            }
            String[] depends = taskItem.getDepends().split(",");
            for (int j = 0; j < depends.length; j++) {
                String[] regular = depends[j].split(":");
                TaskLink taskLink = null;
                if (regular.length == 2) {
                    //这个规则的序号从1开始
                    taskLink = new TaskLink(taskList.get(Integer.valueOf(regular[0]) - 1), taskItem, Integer.valueOf(regular[1]));
                } else {
                    taskLink = new TaskLink(taskList.get(Integer.valueOf(regular[0]) - 1), taskItem, 1);
                }
                links.add(taskLink);
            }
        }
		
        refreshTaskLink(taskList, links);
        return taskList;
    }

    /**
     * 刷新taskList中存在依赖规则的开始时间和结束时间
     *
     * @param taskList
     * @param taskLinkList
     */
    public void refreshTaskLink(List<Task> taskList, List<TaskLink> taskLinkList) {
        List<TaskLink> todoList = new ArrayList<>();
        if (taskLinkList.isEmpty()) {
            return;
        }
        TaskLink taskLink = taskLinkList.get(0);
        todoList.add(taskLink);
        if (StringUtils.isNotBlank(taskLink.getFrom().getDepends())) {
            List<TaskLink> linkTmpList = getToLinkList(taskLink.getFrom(), taskLinkList);
            if (linkTmpList != null) {
                todoList.addAll(linkTmpList);
            }
        }
        List<TaskLink> onceDealWithList = new ArrayList<>();
        Task preTo = null;

        //处理第一个节点开始到没有依赖规则为止的嵌套规则列表
        for (int i = todoList.size() - 1; i >= 0; i--) {
            //倒叙处理依赖规则
            TaskLink link = todoList.get(i);
            if (preTo == null) {
                preTo = link.getTo();
            }
            onceDealWithList.add(link);
            //是否还有下一个节点
            if (i - 1 >= 0) {
                if (preTo.getTaskSort().equals(todoList.get(i - 1).getTo().getTaskSort())) {
                    //还是相等，则跳过
                    continue;
                } else {
                    //不相同,处理链接列表，设置任务开始结束时间
                    Task task = taskList.get(link.getTo().getTaskSort() - 1);
                    dealTaskByOneDealWithList(task, onceDealWithList);
                    //重置处理数据
                    onceDealWithList = new ArrayList<>();
                    preTo = null;
                }
            } else {
                //不相同,处理链接列表，设置任务开始结束时间
                Task task = taskList.get(link.getTo().getTaskSort() - 1);
                dealTaskByOneDealWithList(task, onceDealWithList);
                //重置处理数据
                onceDealWithList = new ArrayList<>();
                preTo = null;
            }
        }
        taskLinkList.removeAll(todoList);
        refreshTaskLink(taskList, taskLinkList);

    }

    public void dealTaskByOneDealWithList(Task task, List<TaskLink> dealLinkList) {
        Date superEnd = null;
        for (TaskLink taskLink1 : dealLinkList) {
            if (superEnd == null) {
                superEnd = DateUtils.incrementDateByWorkingDays(taskLink1.getFrom().getEnd(), taskLink1.getLag());
            } else {
                Date curEnd = DateUtils.incrementDateByWorkingDays(taskLink1.getFrom().getEnd(), taskLink1.getLag());
                if (curEnd.getTime() > superEnd.getTime()) {
                    superEnd = curEnd;
                }
            }
        }
        //设置开始和结束时间,开始时间和结束时间计算不用再加1，因为当天表示如2019-11-11开始2019-11-11结束表示1天
        task.setStart(superEnd);
        task.setEnd(DateUtils.incrementDateByWorkingDays(task.getStart(), task.getDuration() - 1));
    }

    /**
     * 递归遍历获得该task节点的所有依赖规则。
     *
     * @param to
     * @param taskLinkList
     * @return
     */
    public List<TaskLink> getToLinkList(Task to, List<TaskLink> taskLinkList) {
        List<TaskLink> list = null;
        for (TaskLink taskLink : taskLinkList) {
            if (taskLink.getTo().getTaskSort().equals(to.getTaskSort())) {
                //序号相同
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(taskLink);
                if (StringUtils.isNotBlank(taskLink.getFrom().getDepends())) {
                    List<TaskLink> childLinkList = getToLinkList(taskLink.getFrom(), taskLinkList);
                    if (childLinkList != null) {
                        list.addAll(childLinkList);
                    }
                }
            }
        }
        return list;
    }

}