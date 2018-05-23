package com.cqmc.cqcity.constant;

import com.cqmc.cqcity.BuildConfig;
import com.cqmc.cqcity.R;
import com.cqmc.opsrc.blankj.utilcode.util.AppUtils;
import com.cqmc.opsrc.blankj.utilcode.util.ResUtils;
import com.cqmc.opsrc.blankj.utilcode.util.SDCardUtils;

import java.io.File;

/**
 * 环境常量定义
 *
 * @author mos
 * @date 2017.03.07
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class ENVs {
    /** 请求码 -- 通过相机获取照片 */
    public static final String PLATFORM_NAME = BuildConfig.APP_PLATFORM;
    /** SD卡文件名 */
    public static final String FILE_PATH_DATA = SDCardUtils.getSDCardPath() + "wicity";
    /** SD卡图片 */
    public static final String FILE_PATH_DATA_IMAGE = FILE_PATH_DATA + File.separator + "images" + File.separator;

    /** 广告图片文件名 */
    public static final String LAUNCHER_ADS_NAME = "launcher_ads";
    /** 默认区域ID */
    public static final String AREA_ID_DEF = "500100";
    /** 默认区域名 */
    public static final String AREA_NAME_DEF = ResUtils.getString(R.string.countylist_default_county);
    /** 重新创建会话最短间隔(毫秒) */
    public static final int RECREATE_SESSION_MIN_INTERVAL = 10000;
    /** 启动页停留时间(秒) */
    public static final int LAUNCHER_PAGE_DISPLAY_TIME = 3;
    /** 启动页(有广告)默认停留时间(秒) */
    public static final int LAUNCHER_PAGE_WITH_ADS_DISPLAY_TIME_DEF = 5;
    /** 启动页检查广告超时 */
    public static final int LAUNCHER_PAGE_CHECK_ADS_TIMEOUT = 3000;
    /** 启动页下载广告超时 */
    public static final int LANCHER_PAGE_DOWNLOAD_ADS_TIMEOUT = 5000;
    /** 点击Back退出判定间隔(秒) */
    public static final int BACK_TO_EXIT_INTERVAL = 5;
    /** 天气图片分辨率 */
    public static final String MAIN_HOME_WEATHER_ICON_RESULOTION = "480";
    /** 首页标题栏起始透明度 */
    public static final int MAIN_HOME_TITLE_BAR_START_ALPHA = 0;
    /** 首页标题栏背景消失距离(px) */
    public static final int MAIN_HOME_TITLE_BAR_BACKGROUND_HIDE_DISTANCE = 50;
    /** 首页标题栏渐显距离(px) */
    public static final int MAIN_HOME_TITLE_BAR_FADE_IN_DISTANCE = 500;
    /** 首页模块入口一页的行数 */
    public static final int MAIN_HOME_MODULE_ENTRY_ROW_NUM = 2;
    /** 首页模块入口一页的列数 */
    public static final int MAIN_HOME_MODULE_ENTRY_COL_NUM = 5;
    /** 卡片标题宽高比 */
    public static final float MAIN_HOME_MODULE_ASPECT_RATIO_TITLE = 9.0f;
    /** 小火车动画最短展示时间(毫秒) */
    public static final int MAIN_HOME_TRAIN_ANIM_MINIMUM_DELAY = 1000;

    /** 禁用获取验证码倒计时(秒) */
    public static final int DISABLE_VERIFY_CODE_COUNT_DOWN = 60;

    /** 请求码 -- 通过相机获取照片 */
    public static final int REQUEST_CODE_IMAGE_PICKER_FROM_CAMERA = 10001;
    /** 请求码 -- 通过相机获取照片 */
    public static final int REQUEST_CODE_IMAGE_PICKER_FROM_GALLERY = 10002;
    /** 请求码 -- 通过区县列表获取切换的区县 */
    public static final int REQUEST_CODE_COUNTY_LIST = 10003;
    /** 请求码 -- 通过二维码扫描获取结果 */
    public static final int REQUEST_CODE_SCAN = 10004;

    /** 掌厅响应码 -- 成功 */
    public static final int WEIDIAN_STATE_SUCCESS = 0;
    /** 掌厅响应码 -- 未知错误 */
    public static final int WEIDIAN_STATE_UNKNOWN = -1;
    /** 掌厅响应码 -- 低版本，不支持sdk */
    public static final int WEIDIAN_STATE_LOW_VERSION = -2;
    /** 掌厅响应码 -- 未找到手厅app */
    public static final int WEIDIAN_STATE_NOT_FIND = -3;
    /** 掌厅响应码 -- 用户取消 */
    public static final int WEIDIAN_STATE_CANCEL = -4;
    /** 掌厅响应码 -- 授权接口异常 */
    public static final int WEIDIAN_STATE_API_ERROR = -5;

    /** WebView的通用header */
    public static final String WEBVIEW_COMMON_HEADER = "wxcitycq";
    /** WebView的UserAgent后缀 */
    public static final String WEBVIEW_USER_AGENT_SUFFIX = WEBVIEW_COMMON_HEADER + "/" + AppUtils.getAppVersionName();

    /** 在线客服的appkey */
    public static final String ONLINE_SERVICE_APP_KEY = "cqc_sdk#CQ05281013802";
    /** 在线客服的companyId */
    public static final String ONLINE_SERVICE_COMPANY_ID = "cqc_sdk";
    /** 在线客服省编码 */
    public static final String ONLINE_SERVICE_PROVINCE_CODE = "023";
    /** 在线客服城市编码 */
    public static final String ONLINE_SERVICE_CITY_CODE = "023";
    /** 在线客服语音id */
    public static final String ONLINE_SERVICE_VOICE_APP_ID = "59b104de";
}
