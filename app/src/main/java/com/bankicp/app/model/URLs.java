package com.bankicp.app.model;

import java.io.Serializable;

/***
 * 接口实体类
 *
 * @author
 */
public class URLs implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String HTTP = "http://";
    // 用于区分不同公司的
    public final static String ENTERPRISE = "1";
    private static final String URL_BASE = "115.28.145.24:20009";
    //    private static final String URL_BASE = "192.168.0.112:8080";
    // 应用
    public static final String URL_BASE_APP = HTTP + URL_BASE;
    public static final String URL_BASE_PIC = HTTP + URL_BASE;

    /****
     * 电子巡检获取主页列表
     */
    public final static String URL_GET_CABINET_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetCabinetList";
    /**
     * 反馈报告
     */
    public final static String URL_ADDCABINETREPORT = URL_BASE_APP
            + "/MobileApp/Mobile/AddCabinetReport";

    /****
     * 获取事件列表
     */
    public final static String URL_GET_EVENTLIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetEventList";
    /****
     * 添加事件
     */
    public final static String URL_ADD_EVENT_REPORT = URL_BASE_APP
            + "/MobileApp/Mobile/AddEventReport";
    /****
     * 添加日志
     */
    public final static String URL_ADD_EVENT_LOG = URL_BASE_APP
            + "/MobileApp/Mobile/AddEventLog";
    /****
     * 添加交班日志
     */
    public final static String URL_ADDTTANSFER = URL_BASE_APP
            + "/MobileApp/Mobile/AddTtansfer";
    /****
     * 获取交班日志
     */
    public final static String URL_GET_TRANSFER_BYUSERID = URL_BASE_APP
            + "/MobileApp/Mobile/GetTransferByUserID";
    /****
     * 获取接班的人员
     */
    public final static String URL_NEXT_ONDUTY_USERS = URL_BASE_APP
            + "/MobileApp/Mobile/GetNextOnDutyUsers";
    /**
     * 更新接班日志
     */
    public final static String URL_UPDATE_TTANSFER = URL_BASE_APP
            + "/MobileApp/Mobile/UpdateTtansfer";

    /****
     * 获取主页及任务信息
     */
    public final static String URL_GET_TIME_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetTimeList";
    public final static String URL_GET_HOME_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetHomeList";

    public final static String URL_UPDATE = URL_BASE_APP
            + "/MobileApp/Mobile/GetAndroidVersion";
    public final static String URL_UPDATE_CLIENT = URL_BASE_APP
            + "/MobileApp/Mobile/UpdateAndroidClient";
    /***
     * 登陆接口
     ***/
    public static final String URL_LOGIN = URL_BASE_APP
            + "/MobileApp/Mobile/Login";
    /***
     * 主页接口
     ***/
    public static final String URL_GET_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetUploadList";
    /***
     * 获取项目接口
     ***/
    public static final String URL_GET_PROJECT_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetProjectList";
    /***
     * 上传现场信息接口
     ***/
    public static final String URL_UPLOAD_PROJECTT = URL_BASE_APP
            + "/MobileApp/Mobile/SaveUpload";
    /***
     * 删除现场信息接口
     ***/
    public static final String URL_DELETE_UPLOAD = URL_BASE_APP
            + "/MobileApp/Mobile/DeleteUploadByID";
    /***
     * 获取信息列表接口
     ***/
    public static final String URL_GET_MESSAGE_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetMessageList";
    /***
     * 学习接口
     ***/
    public static final String URL_TURN_WORKINSTRUCT = URL_BASE_APP
            + "/MobileApp/Mobile/TurnToWorkInstruct";

    // -------------------------------以下是勘察宝特有的接口-----------------------------------

    /***
     * 获取任务列表接口
     ***/
    public static final String URL_TASK_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetSurveyTaskList";
    /***
     * 获取机房列表
     */
    public static final String URL_DETAIL_TASK_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetDetailTaskList";
    /**
     * 保存任务列表 (同步)
     */
    public static final String URL_SAVE_TASK_LIST = URL_BASE_APP
            + "/MobileApp/Mobile/SaveTaskList";
    /***
     *
     */
    public static final String URL_SAVE_TASK = URL_BASE_APP
            + "/MobileApp/Mobile/SaveTask";
    /**
     * 获取新任务个数
     */
    public static final String URL_GET_HOME_MSG = URL_BASE_APP
            + "/MobileApp/Mobile/GetHomeCount";
    /***
     * 上传信息接口
     ***/
    public static final String URL_UPLOAD_TASK = URL_BASE_APP
            + "/MobileApp/Mobile/SaveSurveyTask";
    /***
     * 认领任务
     ***/
    public static final String URL_SIGN_TASK = URL_BASE_APP
            + "/MobileApp/Mobile/SignInspectionTask";
    /***
     * 完成勘察后，更改任务的状态
     */
    public static final String URL_CHANGE_SURVEY_STATE = URL_BASE_APP
            + "/MobileApp/Mobile/ChangeSurveyState";

    /**
     * 检查勘察任务是否被领取
     */
    public static final String URL_CHECK_SURVEY_IS_TASK = URL_BASE_APP
            + "/MobileApp/Mobile/CheckSurveyIsTask";
    /**
     * 检查勘察任务评量领取
     */
    public static final String URL_GET_SURVEY_FOR_OFFLINE = URL_BASE_APP
            + "/MobileApp/Mobile/GetSurveyForOffLine";

    public static final String URL_APP_SAVEWORKSTATE = URL_BASE_APP
            + "/MobileApp/Mobile/SaveWorkTate";

    public static final String URL_APP_NEARTASK = URL_BASE_APP
            + "/MobileApp/Mobile/GetNearTaskList";
    /**
     * 任务详情
     */
    public static final String URL_APP_TASKDETAIL = URL_BASE_APP
            + "/MobileApp/Mobile/GetContrastSurveyTaskDetail";
    public static final String URL_APP_TASKDGROUP = URL_BASE_APP
            + "/MobileApp/Mobile/GetTaskGroup";
    /***
     *
     */
    public static final String URL_APP_CONTRASTSURVEYTASKLISTL = URL_BASE_APP
            + "/MobileApp/Mobile/GetContrastSurveyTaskList";
    // 获取区域
    public static final String URL_APP_GETJURISDICTION = URL_BASE_APP
            + "/MobileApp/Mobile/GetJurisdiction";
    // 获取区域
    public static final String URL_APP_GETJURISDICTIONLIST = URL_BASE_APP
            + "/MobileApp/Mobile/GetJurisdictionList";
    // 修改密码
    public static final String URL_APP_CHANGEPASSWORD = URL_BASE_APP
            + "/MobileApp/Mobile/changePassword";
    /***
     * 发起任务
     */
    public static final String URL_APP_ADDCONTRASTSURVEYTASK = URL_BASE_APP
            + "/MobileApp/Mobile/AddContrastSurveyTask";
    public static final String URL_APP_HANDLECONTRASTSURVEYTASK = URL_BASE_APP
            + "/MobileApp/Mobile/HandleContrastSurveyTask";

    /**
     *
     */
    public static final String URL_APP_ERROR = URL_BASE_APP
            + "/MobileApp/Mobile/GetAndroidAppError";

}
