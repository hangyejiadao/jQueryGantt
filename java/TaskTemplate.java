/**
 * 任务模板表Entity
 * @author zwh
 * @version 2019-09-27
 */
public class TaskTemplate extends DataEntity<TaskTemplate> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 任务编码
	private String projectId;		// 项目模板id
	private String name;		// 任务名称
	private Integer taskSort;		// 整型 任务排序号
	private Double taskScore;		// 浮点型 任务品牌分
	private Date realStart;		// 任务实际开始时间
	private Date realEnd;		// 任务实际结束时间
	private String taskDescription;		// 任务描述信息
	private Integer level;		// 任务节点所处级别
	private String hasChild;		// 是否有子节点
	private String milestone;		// 是否是里程碑(0任务 1里程碑)
	private String collapsed;		// 是否自动打开下级节点
	private String depends;		// 任务依赖关系字符串7:3,8
	private String description;		// 任务描述
	private Integer duration;		// 任务完成周期 天
	private Integer progress;		// 任务完成进度 最大100
	private String progressByWorklog;		// 布尔值 工作日志进度
	private Integer relevance;		// 关联 整型数据
	private Date start;		// 任务计划开始时间
	private String startIsMilestone;		// 布尔值 开始里程碑
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

	@JsonIgnore
	public String getDbName() {
		return Global.getConfig("jdbc.type");
	}
}