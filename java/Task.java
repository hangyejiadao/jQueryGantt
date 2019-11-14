
/**
 * 项目任务表Entity
 * @author zwh
 * @version 2019-09-30
 */
public class Task extends DataEntity<Task> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 任务编码
	private String projectId;		// 项目模板id

	@BeanLog(attrName = "name",title="任务名称")
	private String name;		// 任务名称
	private Integer taskSort;		// 整型 任务排序号
	private Double taskScore;		// 浮点型 任务品牌分
	@BeanLog(attrName = "realStart",title="任务实际开始时间",dataFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date realStart;		// 任务实际开始时间
	@BeanLog(attrName = "realEnd",title="任务实际结束时间",dataFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date realEnd;		// 任务实际结束时间
	@BeanLog(attrName = "taskDescription",title="任务描述信息")
	private String taskDescription;		// 任务描述信息
	private Integer level;		// 任务节点所处级别
	private String hasChild;		// 是否有子节点
	private String milestone;		// 是否是里程碑(0任务 1里程碑)
	private String collapsed;		// 是否自动打开下级节点
	private String depends;		// 任务依赖关系字符串7:3,8
	@BeanLog(attrName = "description",title="任务描述")
	private String description;		// 任务描述
	@BeanLog(attrName = "duration",title="任务完成周期")
	private Integer duration;		// 任务完成周期 天
	private Integer progress;		// 任务完成进度 最大100
	private String progressByWorklog;		// 布尔值 工作日志进度
	private Integer relevance;		// 关联 整型数据
	@BeanLog(attrName = "start",title="任务计划开始时间",dataFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date start;		// 任务计划开始时间
	private String startIsMilestone;		// 布尔值 开始里程碑
	@BeanLog(attrName = "end",title="任务计划结束时间",dataFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end;		// 任务计划结束时间
	private String endIsMilestone;		// 布尔值 结束里程碑
	private String canAdd;		// 布尔值字符串
	private String canAddIssue;		// 布尔值字符串
	private String canDelete;		// 是否能删除
	private String canWrite;		// 是否可写

	private String taskStatus;		// 任务属性状态
	private String type;		// 任务type属性
	private String typeId;		// 任务typeId属性
	private String newRecord;		// 0旧数据 其他为新数据

	private Employee employee;	//创建人(任务修改日志记录)
	private List<TaskFile> fileList; //任务成果清单文件表，存储任务文件信息包括 文件上传地址
	private List<TaskUser> userList; //任务用户列表，这里的用户可以手动填写用户姓名，也可以是系统的用户姓名
	private List<ProjectScore> scoreList; //流程分质量分属性列表

}