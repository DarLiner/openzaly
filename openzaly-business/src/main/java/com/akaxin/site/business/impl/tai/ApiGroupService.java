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
import com.akaxin.proto.core.GroupProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.site.ApiGroupAddMemberProto;
import com.akaxin.proto.site.ApiGroupCreateProto;
import com.akaxin.proto.site.ApiGroupDeleteProto;
import com.akaxin.proto.site.ApiGroupListProto;
import com.akaxin.proto.site.ApiGroupMembersProto;
import com.akaxin.proto.site.ApiGroupNonMembersProto;
import com.akaxin.proto.site.ApiGroupProfileProto;
import com.akaxin.proto.site.ApiGroupQuitProto;
import com.akaxin.proto.site.ApiGroupRemoveMemberProto;
import com.akaxin.proto.site.ApiGroupUpdateProfileProto;
import com.akaxin.site.business.constant.GroupConfig;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.business.dao.UserProfileDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupListProto.ApiGroupListRequest request = ApiGroupListProto.ApiGroupListRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
//			String siteUserId = request.getSiteUserId();
			logger.info("api.group.list command={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(siteUserId) && siteUserId.equals(siteUserId)) {
				List<SimpleGroupBean> groupBeanList = UserGroupDao.getInstance().getUserGroups(siteUserId);
				ApiGroupListProto.ApiGroupListResponse.Builder responseBuilder = ApiGroupListProto.ApiGroupListResponse
						.newBuilder();
				for (SimpleGroupBean groupBean : groupBeanList) {
					GroupProto.SimpleGroupProfile groupProfile = GroupProto.SimpleGroupProfile.newBuilder()
							.setGroupId(String.valueOf(groupBean.getGroupId()))
							.setGroupName(String.valueOf(groupBean.getGroupName()))
							.setGroupIcon(String.valueOf(groupBean.getGroupPhoto())).build();
					responseBuilder.addList(groupProfile);
				}
				ApiGroupListProto.ApiGroupListResponse response = responseBuilder.build();
				commandResponse.setParams(response.toByteArray());
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.group.list", e);
		}
		logger.info("api.group.list result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupCreateProto.ApiGroupCreateRequest request = ApiGroupCreateProto.ApiGroupCreateRequest
					.parseFrom(command.getParams());
			String groupName = request.getGroupName();
			ProtocolStringList groupMemberIds = request.getSiteUserIdsList();
			String createUserId = command.getSiteUserId();
			logger.info("api.group.create command={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotEmpty(createUserId) && groupMemberIds != null) {
				if (groupMemberIds.size() >= 3) {
					if (!groupMemberIds.contains(createUserId)) {
						groupMemberIds.add(createUserId);
					}
					GroupProfileBean groupBean = UserGroupDao.getInstance().createGroup(createUserId, groupName,
							groupMemberIds);
					logger.info("siteUserId={} create new groupName={} resultBean={}", groupBean.toString());
					if (StringUtils.isNotEmpty(groupBean.getGroupId())) {
						GroupProto.GroupProfile.Builder groupProfileBuilder = GroupProto.GroupProfile.newBuilder();

						groupProfileBuilder.setId(String.valueOf(groupBean.getGroupId()));
						groupProfileBuilder.setName(String.valueOf(groupBean.getGroupName()));
						groupProfileBuilder.setIcon(String.valueOf(groupBean.getGroupPhoto()));

						ApiGroupCreateProto.ApiGroupCreateResponse response = ApiGroupCreateProto.ApiGroupCreateResponse
								.newBuilder().setProfile(groupProfileBuilder.build()).build();
						commandResponse.setParams(response.toByteArray());
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					errCode = ErrorCode2.ERROR_GROUP_MEMBERLESS3;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api.group.create error.", e);
		}
		logger.info("api.group.create result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * 用户删除群，此时需要验证用户是否具有权限 <br>
	 * 目前：具有权限的仅为群的创建者 <br>
	 * 群主／管理员权限限制
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse delete(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupDeleteProto.ApiGroupDeleteRequest request = ApiGroupDeleteProto.ApiGroupDeleteRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			logger.info("api.group.delete command={},request={}", command.toString(), request.toBuilder());

			if (StringUtils.isNotBlank(siteUserId) && StringUtils.isNotBlank(groupId)) {
				String groupMasterId = UserGroupDao.getInstance().getGroupMaster(groupId);
				if (siteUserId.equals(groupMasterId)) {
					if (UserGroupDao.getInstance().deleteGroup(groupId)) {
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					errCode = ErrorCode2.ERROR_NOPERMISSION;
				}
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("delete group error.", e);
		}
		logger.info("api.group.delete result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupProfileProto.ApiGroupProfileRequest request = ApiGroupProfileProto.ApiGroupProfileRequest
					.parseFrom(command.getParams());
			String groupId = request.getGroupId();
			int pageNum = 1;
			int pageSize = GroupConfig.GROUP_MIN_MEMBER_COUNT;
			logger.info("api.group.profile command={} reques={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(groupId)) {
				GroupProfileBean groupBean = UserGroupDao.getInstance().getGroupProfile(groupId);
				logger.info("get groupId={} groupProfile={}", groupBean.toString());

				if (groupBean != null && StringUtils.isNotBlank(groupBean.getGroupId())) {
					SimpleUserBean ownerProfileBean = UserProfileDao.getInstance()
							.getSimpleProfileById(groupBean.getCreateUserId());

					logger.info("get groupId={},groupOwner={}", groupId, ownerProfileBean.toString());

					int groupMembersCount = UserGroupDao.getInstance().getGroupMemberCount(groupId);

					logger.info("get groupId={},groupMembers={}", groupId, groupMembersCount);

					List<GroupMemberBean> groupMemberList = UserGroupDao.getInstance().getGroupMemberList(groupId,
							pageNum, pageSize);

					UserProto.UserProfile ownerProfile = UserProto.UserProfile.newBuilder()
							.setSiteUserId(String.valueOf(ownerProfileBean.getUserId()))
							.setUserPhoto(String.valueOf(ownerProfileBean.getUserPhoto()))
							.setUserName(String.valueOf(ownerProfileBean.getUserName())).build();
					GroupProto.GroupProfile groupProfile = GroupProto.GroupProfile.newBuilder()
							.setId(groupBean.getGroupId()).setName(String.valueOf(groupBean.getGroupName()))
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
					ApiGroupProfileProto.ApiGroupProfileResponse response = responseBuilder.build();

					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("get group profile error.", e);
		}
		logger.info("api.group.profile result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupUpdateProfileProto.ApiGroupUpdateProfileRequest request = ApiGroupUpdateProfileProto.ApiGroupUpdateProfileRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getProfile().getId();
			String photoId = request.getProfile().getIcon();
			String groupName = request.getProfile().getName();
			String groupNotice = request.getProfile().getGroupNotice();
			logger.info("api.group.updateProfile cmd={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(siteUserId) && StringUtils.isNotBlank(groupId)) {
				String groupMasterId = UserGroupDao.getInstance().getGroupMaster(groupId);

				if (StringUtils.isNotBlank(siteUserId) && siteUserId.equals(groupMasterId)) {
					GroupProfileBean gprofileBean = new GroupProfileBean();
					gprofileBean.setGroupId(groupId);
					gprofileBean.setGroupName(groupName);
					gprofileBean.setGroupPhoto(photoId);
					gprofileBean.setGroupNotice(groupNotice);
					if (UserGroupDao.getInstance().updateGroupProfile(gprofileBean)) {
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					errCode = ErrorCode2.ERROR_NOPERMISSION;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("update group profile error.", e);
		}
		logger.info("api.group.updateProfile result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * 添加群成员，支持群成员拉取好友进群，因此无群主权限限制<br>
	 * 无管理员权限限制
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse addMember(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupAddMemberProto.ApiGroupAddMemberRequest request = ApiGroupAddMemberProto.ApiGroupAddMemberRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			ProtocolStringList addMemberList = request.getUserListList();
			logger.info("api.group.addMember comamnd={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(siteUserId) && StringUtils.isNotBlank(groupId) && addMemberList != null) {
				int currentSize = UserGroupDao.getInstance().getGroupMemberCount(groupId);
				int maxSize = SiteConfig.getMaxGroupMemberSize();
				if (currentSize + addMemberList.size() <= maxSize) {
					if (UserGroupDao.getInstance().addGroupMember(siteUserId, groupId, addMemberList)) {
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					errCode = ErrorCode2.ERROR_GROUP_MAXMEMBERCOUNT;
				}
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("add group member error.", e);
		}
		logger.info("api.group.addMember result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupRemoveMemberProto.ApiGroupRemoveMemberRequest request = ApiGroupRemoveMemberProto.ApiGroupRemoveMemberRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			ProtocolStringList deleteMemberIds = request.getSiteUserIdList();
			logger.info("api.group.deleteMember command={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(siteUserId) && StringUtils.isNoneBlank(groupId) && deleteMemberIds != null) {
				String groupMasterId = UserGroupDao.getInstance().getGroupMaster(groupId);

				if (siteUserId.equals(groupMasterId)) {
					if (UserGroupDao.getInstance().deleteGroupMember(groupId, deleteMemberIds)) {
						errCode = ErrorCode2.SUCCESS;
					}
				} else {
					errCode = ErrorCode2.ERROR_NOPERMISSION;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("delete group member error.", e);
		}
		logger.info("api.group.removeMember", errCode.toString());
		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupQuitProto.ApiGroupQuitRequest request = ApiGroupQuitProto.ApiGroupQuitRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String groupId = request.getGroupId();
			logger.info("api.group.quit cmd={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(siteUserId) && StringUtils.isNotBlank(groupId)) {
				if (UserGroupDao.getInstance().quitGroup(groupId, siteUserId)) {
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("quite group error.", e);
		}
		logger.info("api.group.quit result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
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
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiGroupMembersProto.ApiGroupMembersRequest request = ApiGroupMembersProto.ApiGroupMembersRequest
					.parseFrom(command.getParams());
			String groupId = request.getGroupId();
			int pageNum = 1;
			int pageSize = GroupConfig.GROUP_MAX_MEMBER_COUNT;
			logger.info("api.group.membners cmd={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(groupId)) {
				List<GroupMemberBean> memberList = UserGroupDao.getInstance().getGroupMemberList(groupId, pageNum,
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
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("get group members error.", e);
		}
		logger.info("api.group.members result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * 获取用户群组中，不存在的好友用户
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse nonMembers(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
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
			logger.error("api.group.nonMembers exception", e);
		}
		logger.info("api.group.nonMembers result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

}
