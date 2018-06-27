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
package com.akaxin.site.business.impl.tai;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.exceptions.ZalyException;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.GroupProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.site.ApiGroupAddMemberProto;
import com.akaxin.proto.site.ApiGroupCreateProto;
import com.akaxin.proto.site.ApiGroupDeleteProto;
import com.akaxin.proto.site.ApiGroupListProto;
import com.akaxin.proto.site.ApiGroupMembersProto;
import com.akaxin.proto.site.ApiGroupMuteProto;
import com.akaxin.proto.site.ApiGroupNonMembersProto;
import com.akaxin.proto.site.ApiGroupProfileProto;
import com.akaxin.proto.site.ApiGroupQuitProto;
import com.akaxin.proto.site.ApiGroupRemoveMemberProto;
import com.akaxin.proto.site.ApiGroupSettingProto;
import com.akaxin.proto.site.ApiGroupUpdateProfileProto;
import com.akaxin.proto.site.ApiGroupUpdateSettingProto;
import com.akaxin.site.business.constant.GroupConfig;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserGroupBean;
import com.google.common.collect.Lists;
import com.google.protobuf.ProtocolStringList;

/**
 * 扩展服务器与站点之间通过hai接口，管理群组功能
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-13 21:48:35
 */
public class ApiGroupService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(ApiGroupService.class);

	/**
	 * 获取用户群列表 <br>
	 * 无权限限制
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupListProto.ApiGroupListRequest request = ApiGroupListProto.ApiGroupListRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(siteUserId) && siteUserId.equals(siteUserId)) {
				List<SimpleGroupBean> groupBeanList = UserGroupDao.getInstance().getUserGroupList(siteUserId);
				ApiGroupListProto.ApiGroupListResponse.Builder responseBuilder = ApiGroupListProto.ApiGroupListResponse
						.newBuilder();
				for (SimpleGroupBean groupBean : groupBeanList) {
					GroupProto.SimpleGroupProfile.Builder groupProfileBuilder = GroupProto.SimpleGroupProfile
							.newBuilder();
					groupProfileBuilder.setGroupId(groupBean.getGroupId());
					if (StringUtils.isNotEmpty(groupBean.getGroupName())) {
						groupProfileBuilder.setGroupName(groupBean.getGroupName());
					}
					if (StringUtils.isNotEmpty(groupBean.getGroupPhoto())) {
						groupProfileBuilder.setGroupIcon(groupBean.getGroupPhoto());
					}
					responseBuilder.addList(groupProfileBuilder.build());
				}
				ApiGroupListProto.ApiGroupListResponse response = responseBuilder.build();
				commandResponse.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 用户创建群，并添加初始群成员 <br>
	 * 无权限限制
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse create(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupCreateProto.ApiGroupCreateRequest request = ApiGroupCreateProto.ApiGroupCreateRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();// group owner
			String groupName = request.getGroupName();
			ProtocolStringList groupMembers = request.getSiteUserIdsList();
			List<String> groupMemberIds = Lists.newArrayList(groupMembers);// copy a new list
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (!SiteConfig.allowAddGroups()) {
				throw new ZalyException(ErrorCode2.ERROR2_GROUP_NOTALLOW);
			}

			if (StringUtils.isAnyEmpty(siteUserId, groupName) || groupMemberIds == null) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			// 检查用户是否被封禁，或者不存在
			for (String groupMemberId : groupMemberIds) {
				SimpleUserBean bean = UserProfileDao.getInstance().getSimpleProfileById(groupMemberId);
				if (bean == null || bean.getUserStatus() == 1) {
					groupMemberIds.remove(groupMemberId);
				}
			}

			if (!groupMemberIds.contains(siteUserId)) {
				groupMemberIds.add(siteUserId);
			}

			if (groupMemberIds.size() < 3) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_MEMBERLESS3);
			}

			GroupProfileBean groupBean = UserGroupDao.getInstance().createGroup(siteUserId, groupName, groupMemberIds);
			if (groupBean != null && StringUtils.isNotEmpty(groupBean.getGroupId())) {
				GroupProto.GroupProfile.Builder groupProfileBuilder = GroupProto.GroupProfile.newBuilder();
				groupProfileBuilder.setId(groupBean.getGroupId());
				if (StringUtils.isNotEmpty(groupBean.getGroupName())) {
					groupProfileBuilder.setName(groupBean.getGroupName());
				}
				if (StringUtils.isNotEmpty(groupBean.getGroupPhoto())) {
					groupProfileBuilder.setIcon(String.valueOf(groupBean.getGroupPhoto()));
				}

				ApiGroupCreateProto.ApiGroupCreateResponse response = ApiGroupCreateProto.ApiGroupCreateResponse
						.newBuilder().setProfile(groupProfileBuilder.build()).build();
				commandResponse.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_GROUP_WHEN_CREATE;
			}

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * <pre>
	 * 用户删除群，此时需要验证用户是否具有权限 <br>
	 * 目前：具有权限的仅为群的创建者 (群主)
	 * </pre>
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse delete(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupDeleteProto.ApiGroupDeleteRequest request = ApiGroupDeleteProto.ApiGroupDeleteRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNoneEmpty(siteUserId, groupId)) {

				if (!checkGroupStatus(groupId)) {
					throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
				}

				String groupMasterId = UserGroupDao.getInstance().getGroupMaster(groupId);
				if (siteUserId.equals(groupMasterId)) {
					if (UserGroupDao.getInstance().deleteGroup(groupId)) {
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					errCode = ErrorCode2.ERROR_NOPERMISSION;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 获取群资料信息，一般由以下几部分组成 <br>
	 * 1.群资料GroupProfile <br>
	 * 2.群主基本资料GroupMaster，群主通过GroupProfile获取 <br>
	 * 3.群成员人数以及排在最前列的四位用户 <br>
	 * 4.无权限限制
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupProfileProto.ApiGroupProfileRequest request = ApiGroupProfileProto.ApiGroupProfileRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			int pageNum = 1;
			int pageSize = GroupConfig.GROUP_MIN_MEMBER_COUNT;
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupStatus(groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			GroupProfileBean groupBean = UserGroupDao.getInstance().getGroupProfile(groupId);
			if (groupBean == null || StringUtils.isEmpty(groupBean.getGroupId())) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_QUERY_PROFILE);
			}

			SimpleUserBean ownerProfileBean = UserProfileDao.getInstance()
					.getSimpleProfileById(groupBean.getCreateUserId());
			logger.debug("get groupId={},groupOwner={}", groupId, ownerProfileBean.toString());

			int groupMembersCount = UserGroupDao.getInstance().getGroupMemberCount(groupId);
			logger.debug("get groupId={},groupMembers={}", groupId, groupMembersCount);

			List<GroupMemberBean> groupMemberList = UserGroupDao.getInstance().getGroupMemberList(groupId, pageNum,
					pageSize);

			UserProto.UserProfile ownerProfile = UserProto.UserProfile.newBuilder()
					.setSiteUserId(String.valueOf(ownerProfileBean.getUserId()))
					.setUserPhoto(String.valueOf(ownerProfileBean.getUserPhoto()))
					.setUserName(String.valueOf(ownerProfileBean.getUserName())).build();
			GroupProto.GroupProfile groupProfile = GroupProto.GroupProfile.newBuilder().setId(groupBean.getGroupId())
					.setName(String.valueOf(groupBean.getGroupName()))
					.setIcon(String.valueOf(groupBean.getGroupPhoto())).build();

			ApiGroupProfileProto.ApiGroupProfileResponse.Builder responseBuilder = ApiGroupProfileProto.ApiGroupProfileResponse
					.newBuilder();
			responseBuilder.setOwner(ownerProfile);
			responseBuilder.setProfile(groupProfile);
			responseBuilder.setGroupMemberCount(groupMembersCount);

			for (GroupMemberBean memberBean : groupMemberList) {
				UserProto.UserProfile memberProfile = UserProto.UserProfile.newBuilder()
						.setSiteUserId(String.valueOf(memberBean.getUserId()))
						.setUserPhoto(String.valueOf(memberBean.getUserPhoto()))
						.setUserName(String.valueOf(memberBean.getUserName())).build();
				GroupProto.GroupMemberProfile groupMemberProfile = GroupProto.GroupMemberProfile.newBuilder()
						.setProfile(memberProfile).build();
				responseBuilder.addGroupLastestMember(groupMemberProfile);
			}
			// 是否可以邀请群聊（除了群主以外）
			responseBuilder.setCloseInviteGroupChat(groupBean.isCloseInviteGroupChat());
			ApiGroupProfileProto.ApiGroupProfileResponse response = responseBuilder.build();

			commandResponse.setParams(response.toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 用户更新群资料<br>
	 * 群主／管理员权限限制
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse updateProfile(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupUpdateProfileProto.ApiGroupUpdateProfileRequest request = ApiGroupUpdateProfileProto.ApiGroupUpdateProfileRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getProfile().getId();
			String photoId = request.getProfile().getIcon();
			String groupName = request.getProfile().getName();
			String groupNotice = request.getProfile().getGroupNotice();
			// 新的群群主
			String newGroupOwner = request.getNewGroupOwner();
			// 是否可以邀请群聊（除了群主以外的其他群成员）
			boolean closeInviteGroupChat = request.getCloseInviteGroupChat();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupStatus(groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			// 判断是否具有权限，群主拥有权限
			String groupMasterId = UserGroupDao.getInstance().getGroupMaster(groupId);
			if (siteUserId.equals(groupMasterId)) {
				GroupProfileBean gprofileBean = new GroupProfileBean();
				gprofileBean.setGroupId(groupId);
				gprofileBean.setGroupName(groupName);
				gprofileBean.setGroupPhoto(photoId);
				gprofileBean.setGroupNotice(groupNotice);
				gprofileBean.setCreateUserId(newGroupOwner);
				gprofileBean.setCloseInviteGroupChat(closeInviteGroupChat);

				if (StringUtils.isNotEmpty(groupName)) {
					if (UserGroupDao.getInstance().updateGroupProfile(gprofileBean)) {
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					if (UserGroupDao.getInstance().updateGroupIGC(gprofileBean)) {
						errCode = ErrorCode2.SUCCESS;
					}
				}
			} else {
				errCode = ErrorCode2.ERROR_NOPERMISSION;
			}

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 添加群成员，支持群成员拉取好友进群，因此无群主权限限制<br>
	 * 无管理员权限限制 -> 添加群资料中是否允许添加成员
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse addMember(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupAddMemberProto.ApiGroupAddMemberRequest request = ApiGroupAddMemberProto.ApiGroupAddMemberRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			ProtocolStringList memberList = request.getUserListList();
			List<String> addMemberList = Lists.newArrayList(memberList);// copy a new list
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, groupId) || addMemberList == null) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}
			// 群是否存在
			if (!checkGroupStatus(groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			// 校验用户,删除禁封的用户
			for (String groupMemberId : addMemberList) {
				SimpleUserBean bean = UserProfileDao.getInstance().getSimpleProfileById(groupMemberId);
				if (bean == null || bean.getUserStatus() == 1) {
					addMemberList.remove(groupMemberId);
				}
			}

			GroupProfileBean bean = UserGroupDao.getInstance().getGroupProfile(groupId);
			// 校验权限
			if (checkAddMemberPermission(siteUserId, bean)) {
				int currentSize = UserGroupDao.getInstance().getGroupMemberCount(groupId);
				int maxSize = SiteConfig.getMaxGroupMemberSize();
				if (currentSize + addMemberList.size() <= maxSize) {
					if (UserGroupDao.getInstance().addGroupMember(siteUserId, groupId, addMemberList)) {
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					errCode = ErrorCode2.ERROR_GROUP_MAXMEMBERCOUNT;
				}
			} else {
				errCode = ErrorCode2.ERROR_GROUP_INVITE_CHAT_CLOSE;
			}

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * <pre>
	 * 添加群成员权限：
	 * 		1.关闭的开关是打开的
	 * 		2.是管理员操作
	 * </pre>
	 *
	 * @param siteUserId
	 * @param bean
	 * @return
	 */
	private boolean checkAddMemberPermission(String siteUserId, GroupProfileBean bean) {
		if (bean != null) {
			if (!bean.isCloseInviteGroupChat() || siteUserId.equals(bean.getCreateUserId())) {
				return true;
			}
		}
		return false;
	}

	public CommandResponse removeMember(Command command) {
		return deleteMember(command);
	}

	/**
	 * 群主以及管理员删除群成员<br>
	 * 群主／管理员权限限制
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse deleteMember(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupRemoveMemberProto.ApiGroupRemoveMemberRequest request = ApiGroupRemoveMemberProto.ApiGroupRemoveMemberRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			ProtocolStringList deleteMemberIds = request.getSiteUserIdList();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyBlank(siteUserId, groupId) || deleteMemberIds == null) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupStatus(groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			String groupMasterId = UserGroupDao.getInstance().getGroupMaster(groupId);
			if (siteUserId.equals(groupMasterId)) {
				if (UserGroupDao.getInstance().deleteGroupMember(groupId, deleteMemberIds)) {
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_NOPERMISSION;
			}

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 用户退群 <br>
	 * 无权限限制
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse quit(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupQuitProto.ApiGroupQuitRequest request = ApiGroupQuitProto.ApiGroupQuitRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNoneEmpty(siteUserId, groupId)) {
				if (UserGroupDao.getInstance().quitGroup(groupId, siteUserId)) {
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 获取群成员 <br>
	 * 无权限控制
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse members(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupMembersProto.ApiGroupMembersRequest request = ApiGroupMembersProto.ApiGroupMembersRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String siteGroupId = request.getGroupId();
			int pageNum = 1;
			int pageSize = GroupConfig.GROUP_MAX_MEMBER_COUNT;
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, siteGroupId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupStatus(siteGroupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			List<GroupMemberBean> memberList = UserGroupDao.getInstance().getGroupMemberList(siteGroupId, pageNum,
					pageSize);
			ApiGroupMembersProto.ApiGroupMembersResponse.Builder responseBuilder = ApiGroupMembersProto.ApiGroupMembersResponse
					.newBuilder();
			for (GroupMemberBean member : memberList) {
				GroupProto.GroupMemberRole memberRole = GroupProto.GroupMemberRole.forNumber(member.getUserRole());
				UserProto.UserProfile memberProfile = UserProto.UserProfile.newBuilder()
						.setSiteUserId(member.getUserId()).setUserName(String.valueOf(member.getUserName()))
						.setUserPhoto(String.valueOf(member.getUserPhoto())).build();
				GroupProto.GroupMemberProfile groupMember = GroupProto.GroupMemberProfile.newBuilder()
						.setRole(memberRole).setProfile(memberProfile).build();
				responseBuilder.addList(groupMember);
			}
			commandResponse.setParams(responseBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 获取用户群组中，不存在的好友用户
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse nonMembers(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupNonMembersProto.ApiGroupNonMembersRequest request = ApiGroupNonMembersProto.ApiGroupNonMembersRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			if (pageNum == 0 && pageSize == 0) {
				pageSize = 100;
			}
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupStatus(groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			List<SimpleUserBean> userFriendList = UserGroupDao.getInstance().getUserFriendNonGroupMemberList(siteUserId,
					groupId, pageNum, pageSize);
			ApiGroupNonMembersProto.ApiGroupNonMembersResponse.Builder responseBuilder = ApiGroupNonMembersProto.ApiGroupNonMembersResponse
					.newBuilder();
			for (SimpleUserBean friendBean : userFriendList) {
				UserProto.SimpleUserProfile friendProfile = UserProto.SimpleUserProfile.newBuilder()
						.setSiteUserId(friendBean.getUserId()).setUserName(String.valueOf(friendBean.getUserName()))
						.setUserPhoto(String.valueOf(friendBean.getUserPhoto())).build();
				responseBuilder.addProfile(friendProfile);
			}
			commandResponse.setParams(responseBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 获取个人对群的设置
	 *
	 * @param command
	 * @return
	 */
	@Deprecated
	public CommandResponse setting(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupSettingProto.ApiGroupSettingRequest request = ApiGroupSettingProto.ApiGroupSettingRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNoneEmpty(siteUserId, groupId)) {
				UserGroupBean bean = UserGroupDao.getInstance().getUserGroupSetting(siteUserId, groupId);
				if (bean != null) {
					ApiGroupSettingProto.ApiGroupSettingResponse response = ApiGroupSettingProto.ApiGroupSettingResponse
							.newBuilder().setMessageMute(bean.isMute()).build();
					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
				} else {
					errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 个人更新群设置信息
	 *
	 * @param command
	 * @return
	 */
	@Deprecated
	public CommandResponse updateSetting(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupUpdateSettingProto.ApiGroupUpdateSettingRequest request = ApiGroupUpdateSettingProto.ApiGroupUpdateSettingRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			boolean isMute = request.getMessageMute();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNoneEmpty(siteUserId, groupId)) {
				UserGroupBean bean = new UserGroupBean();
				bean.setSiteGroupId(groupId);
				bean.setMute(isMute);
				if (UserGroupDao.getInstance().updateUserGroupSetting(siteUserId, bean)) {
					errCode = ErrorCode2.SUCCESS;
				} else {
					errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	public CommandResponse mute(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupMuteProto.ApiGroupMuteRequest request = ApiGroupMuteProto.ApiGroupMuteRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String siteGroupId = request.getSiteGroupId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, siteGroupId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupStatus(siteGroupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			UserGroupBean bean = UserGroupDao.getInstance().getUserGroupSetting(siteUserId, siteGroupId);
			if (bean != null) {
				ApiGroupSettingProto.ApiGroupSettingResponse response = ApiGroupSettingProto.ApiGroupSettingResponse
						.newBuilder().setMessageMute(bean.isMute()).build();
				commandResponse.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
			}

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * 个人更新群设置信息
	 *
	 * @param command
	 * @return
	 */
	public CommandResponse updateMute(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiGroupUpdateSettingProto.ApiGroupUpdateSettingRequest request = ApiGroupUpdateSettingProto.ApiGroupUpdateSettingRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			boolean isMute = request.getMessageMute();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isAnyEmpty(siteUserId, groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupStatus(groupId)) {
				throw new ZalyException(ErrorCode2.ERROR_GROUP_DELETED);
			}

			UserGroupBean bean = new UserGroupBean();
			bean.setSiteGroupId(groupId);
			bean.setMute(isMute);
			if (UserGroupDao.getInstance().updateUserGroupSetting(siteUserId, bean)) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
			}

		} catch (Exception e) {
			if (e instanceof ZalyException) {
				errCode = ((ZalyException) e).getErrCode();
			} else {
				errCode = ErrorCode2.ERROR_SYSTEMERROR;
			}
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	// 检测群主是否存在
	private boolean checkGroupStatus(String groupId) {
		int status = UserGroupDao.getInstance().getGroupStatus(groupId);
		return status == 1;
	}
}
