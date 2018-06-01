/**
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.common.constant;

/**
 * <pre>
 * 站点服务端的错误信息
 * 		1.每个请求，成功状态只有一种状态
 * 		2.错误需要提示用户使用code=error.alert，客户端能够展示错误信息
 * 		3.其他错误使用code=error 客户端默认提示请求失败或者不展示错误提示
 * </pre>
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-02 18:05:31
 */
public enum ErrorCode2 implements IErrorCode {
	SUCCESS("success", ""), // 操作成功

	ERROR_SYSTEMERROR("error.alert", "系统异常"), //
	ERROR_NOPERMISSION("error.alert", "用户无权限该操作"), // 用户无权限错误
	ERROR_UNSUPPORT_PROTOCOL("error.alert", "协议版本错误"), // 服务端不支持该功能
	ERROR_ILLEGALREQUEST("error.alert", "非法请求"), // 非法的请求
	ERROR_INVALIDPACKAGEACTION("error.alert", "无效的包名称"), // 无效的包action
	ERROR_DATABASE_EXECUTE("error.alert", "数据库执行错误"), // 无效的包action

	ERROR_PARAMETER("error.alert", "请求参数错误"), // 请求参数错误
	ERROR_PARAMETER_NICKNAME("error.alert", "昵称长度错误"), // 昵称格式错误

	ERROR_LOGINID_LENGTH("error.alert", "站点账号,格式错误"), // 昵称格式错误
	ERROR_LOGINID_EXIST("error.alert", "站点账号只允许设置一次"), // 昵称格式错误

	ERROR_SESSION("error.session", "用户身份认证失败"), // session验证失败

	ERROR_REGISTER("error.alert", "用户注册失败"), // 用户注册失败
	ERROR_REGISTER_USERID_UNIQUE("error.unique.sui", "用户ID已存在"), // 用户注册失败
	ERROR_REGISTER_SAVEPROFILE("error.alert", "保存用户数据失败"), // 用户注册失败
	ERROR_REGISTER_UIC("error.alert", "用户邀请码错误"), // 用户邀请码错误
	ERROR_REGISTER_PHONEID("error.alert", "用户实名手机号验证错误"), // 验证手机号失败
	ERROR_REGISTER_PHONETOKEN("error.phone.token", "用户实名认证失败"), // 验证手机号失败

	ERROR2_MESSAGE_SEND_FAIL("error.alert", "消息发送失败"), //

	ERROR2_USER_NOUSER("error.alert", "无该用户信息"), //
	ERROR2_USER_SAVE_PUSHTOKEN("error.alert", "保存数据失败"), //
	ERROR2_USER_UPDATE_PROFILE("error.alert", "更新数据库用户身份失败"), //
	ERROR2_USER_NOLIST("error.alert", "暂无用户"), //

	ERROR2_USER_NO_FRIEND("error.no.friends", "用户暂无好友"), //
	ERROR2_USER_NO_GROUP("error.no.groups", "用户暂无群组"), //

	ERROR2_FRIEND_IS("error.alert", "用户已经是你的好友"), //
	ERROR2_FRIEND_APPLYSELF("error.alert", "用户不能添加自己为好友"), //
	ERROR2_FRIEND_APPLYCOUNT("error.alert", "添加好友最多为5次"), //
	ERROR2_FRIEND_sealUped("error.alert", "你已经被封禁,无法执行操作"), //

	ERROR_GROUP_DELETED("error.group.deleted", "该群聊不存在"), //
	ERROR_GROUP_WHEN_CREATE("error.alert", "创建群聊出错，请稍后重试"), //
	ERROR_GROUP_QUERY_PROFILE("error.alert", "查询用户资料出错，请返回重试"), //
	ERROR_GROUP_INVITE_CHAT_CLOSE("error.alert", "群主已关闭邀请群聊功能"), //
	ERROR_GROUP_MAXMEMBERCOUNT("error.alert", "超过群人数上限"), // 添加群成员，人数超过上限
	ERROR_GROUP_MEMBERLESS3("error.alert", "创建群成员少于三人"), //

	ERROR2_LOGGIN_USERID_EMPTY("error.alert", "用户身份为空"), // 用户身份校验失败
	ERROR2_LOGGIN_DEVICEID_EMPTY("error.alert", "设备身份为空"), // 用户身份校验失败
	ERROR2_LOGGIN_UPDATE_DEVICE("error.alert", "更新设备失败"), // 更新设备失败
	ERROR2_LOGGIN_UPDATE_SESSION("error.alert", "保存session失败"), // 保存session
	ERROR2_LOGGIN_ERRORSIGN("error.alert", "用户身份校验失败，请重新登陆"), // 用户身份校验失败
	ERROR2_LOGGIN_NOREGISTER("error.login.need_register", ""), // 用户需要在该站点注册
	ERROR2_LOGGIN_SEALUPUSER("error.alert", "当前用户无权登陆"), // 用户需要在该站点注册

	// ERROR2_IMAUTH_FAIL("error.alert", "im连接认证失败"), // 用户需要在该站点注册

	ERROR2_FILE_DOWNLOAD("error.file.download", ""), //

	ERROR2_PHONE_SAME("error.phone.same", "本机身份已与此号码实名绑定"), //
	ERROR2_PHONE_BIND_USER("error.phone.hasUser", "该手机号码已经绑定其他账号"), //
	ERROR2_PHONE_EXIST("error.alert", "该手机号码已经绑定其他账号"), //
	ERROR2_PHONE_REALNAME_EXIST("error.alert", "此账号已经绑定手机号码"), //
	ERROR2_PHONE_VERIFYCODE("error.alert", "验证码验证失败"), //
	ERROR2_PHONE_GETVERIFYCODE("error.alert", "获取验证码失败"), //
	ERROR2_PHONE_FORMATTING("error.alert", "不支持的手机号"), //
	ERROR2_PHONE_HAVE_NO("error.alert", "手机号不存在"), //

	ERROR2_PLUGIN_STATUS("error.alert", "扩展状态错误"), //

	ERROR2_SECRETCHAT_CLOSE("error.alert", "站点服务不支持绝密聊天"), //

	ERROR2_HTTP_URL("error.alert", "请求使用的url错误"), //

	ERROR("error.alert", "请求失败"); // 默认未知错误

	private String code;
	private String info;

	ErrorCode2(String code, String info) {
		this.code = code;
		this.info = info;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getInfo() {
		return this.info;
	}

	@Override
	public boolean isSuccess() {
		return "success".equals(this.code) ? true : false;
	}

	@Override
	public String toString() {
		return "errCode:" + this.code + " errInfo:" + this.info;
	}
}
