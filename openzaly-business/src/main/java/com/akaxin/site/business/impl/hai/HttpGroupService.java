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
package com.akaxin.site.business.impl.hai;

import java.util.List;

import com.akaxin.site.business.impl.site.SiteConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.exceptions.ZalyException2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.GroupProto;
import com.akaxin.proto.core.UserProto;
import com.akaxin.proto.plugin.HaiGroupAddMemberProto;
import com.akaxin.proto.plugin.HaiGroupCheckMemberProto;
import com.akaxin.proto.plugin.HaiGroupDeleteProto;
import com.akaxin.proto.plugin.HaiGroupListProto;
import com.akaxin.proto.plugin.HaiGroupMembersProto;
import com.akaxin.proto.plugin.HaiGroupNonmembersProto;
import com.akaxin.proto.plugin.HaiGroupProfileProto;
import com.akaxin.proto.plugin.HaiGroupRemoveMemberProto;
import com.akaxin.proto.plugin.HaiGroupUpdateProto;
import com.akaxin.site.business.constant.GroupConfig;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.google.protobuf.ProtocolStringList;

/**
 * <pre>
 * 	群组相关的扩展功能实现
 * 		/ hai / group / profile 
 * 		/ hai / group / list 
 * 		/ hai / group / update 
 * 		/ hai / group / delete 
 * 		/ hai / group / addMember 
 * 		/ hai / group / checkMember 	
 * 		/ hai / group / removeMember 
 * 		/ hai / group / members 
 * 		/ hai / group / nonmembers
 * </pre>
 */
/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-13 16:46:01
 */
public class HttpGroupService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpGroupService.class);

	/**
	 * /hai/group/profile : 获取群组资料
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiGroupProfileProto.HaiGroupProfileRequest request = HaiGroupProfileProto.HaiGroupProfileRequest
					.parseFrom(command.getParams());
			String groupId = request.getGroupId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			GroupProfileBean groupBean = UserGroupDao.getInstance().getGroupProfile(groupId);

			if (groupBean != null && StringUtils.isNotBlank(groupBean.getGroupId())) {
				GroupProto.GroupProfile groupProfile = GroupProto.GroupProfile.newBuilder()
						.setId(groupBean.getGroupId()).setName(String.valueOf(groupBean.getGroupName()))
						.setIcon(String.valueOf(groupBean.getGroupPhoto())).build();
				HaiGroupProfileProto.HaiGroupProfileResponse.Builder responseBuilder = HaiGroupProfileProto.HaiGroupProfileResponse
						.newBuilder();
				responseBuilder.setProfile(groupProfile);
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * /hai/group/list
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiGroupListProto.HaiGroupListRequest request = HaiGroupListProto.HaiGroupListRequest
					.parseFrom(command.getParams());
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			LogUtils.requestDebugLog(logger, command, request.toString());

			int totalNum = UserGroupDao.getInstance().getTotalGroupNum();
			List<SimpleGroupBean> groupList = UserGroupDao.getInstance().getGroupList(pageNum, pageSize);

			HaiGroupListProto.HaiGroupListResponse.Builder responseBuilder = HaiGroupListProto.HaiGroupListResponse
					.newBuilder();
			responseBuilder.setPageTotalNum(totalNum);
			if (groupList != null) {
				for (SimpleGroupBean bean : groupList) {
					responseBuilder.addGroupProfile(getSimpleGroupProfile(bean));
				}
			}
			commandResponse.setParams(responseBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * /hai/group/update : 更新群资料
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse update(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiGroupUpdateProto.HaiGroupUpdateRequest request = HaiGroupUpdateProto.HaiGroupUpdateRequest
					.parseFrom(command.getParams());
			String groupId = request.getProfile().getId();
			String photoId = request.getProfile().getIcon();
			String groupName = request.getProfile().getName();
			String groupNotice = request.getProfile().getGroupNotice();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isEmpty(groupId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			GroupProfileBean gprofileBean = new GroupProfileBean();
			gprofileBean.setGroupId(groupId);
			gprofileBean.setGroupName(groupName);
			gprofileBean.setGroupPhoto(photoId);
			gprofileBean.setGroupNotice(groupNotice);
			if (UserGroupDao.getInstance().updateGroupProfile(gprofileBean)) {
				errCode = ErrorCode2.SUCCESS;
			} else {
				errCode = ErrorCode2.ERROR_DATABASE_EXECUTE;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * /hai/group/delete : 删除群组
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse delete(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiGroupDeleteProto.HaiGroupDeleteRequest request = HaiGroupDeleteProto.HaiGroupDeleteRequest
					.parseFrom(command.getParams());
			String siteGroupId = request.getGroupId();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isEmpty(siteGroupId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			if (UserGroupDao.getInstance().deleteGroup(siteGroupId)) {
				errCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}

		return commandResponse.setErrCode(errCode);
	}

	/**
	 * /hai/group/addMember : 添加群成员
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse addMember(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiGroupAddMemberProto.HaiGroupAddMemberRequest request = HaiGroupAddMemberProto.HaiGroupAddMemberRequest
					.parseFrom(command.getParams());
			String siteGroupId = request.getGroupId();
			ProtocolStringList memberUserList = request.getMemberSiteUserIdList();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (memberUserList == null) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			if (!checkGroupIdIegal(siteGroupId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			for (String memberId : memberUserList) {
				if (!checkUserIdIegal(memberId)) {
					throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
				}
			}

			if (UserGroupDao.getInstance().addGroupMember(null, siteGroupId, memberUserList)) {
				errCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * /hai/group/removeMember : 删除群成员
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse removeMember(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiGroupRemoveMemberProto.HaiGroupRemoveMemberRequest request = HaiGroupRemoveMemberProto.HaiGroupRemoveMemberRequest
					.parseFrom(command.getParams());
			String groupId = request.getGroupId();
			ProtocolStringList deleteMemberIds = request.getGroupMemberList();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(groupId)) {
				//无法删除admin
				for (String deleteMemberId : deleteMemberIds) {
					if (SiteConfig.isSiteSuperAdmin(deleteMemberId)) {
						throw new ZalyException2(ErrorCode2.ERROR_NOPERMISSION);
					}
				}
				if (UserGroupDao.getInstance().deleteGroupMember(groupId, deleteMemberIds)) {
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 zalyException2) {
			errCode = (ErrorCode2) zalyException2.getErrCode();
			LogUtils.requestErrorLog(logger, command, zalyException2);
		}
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * /hai/group/checkMember : 检测用户是否在群聊中
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse checkMember(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			HaiGroupCheckMemberProto.HaiGroupCheckMemberRequest request = HaiGroupCheckMemberProto.HaiGroupCheckMemberRequest
					.parseFrom(command.getParams());
			String groupId = request.getGroupId();
			ProtocolStringList checkUserIds = request.getSiteUserIdList();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (StringUtils.isNotBlank(groupId) || checkUserIds == null) {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}

			List<String> checkedUserList = UserGroupDao.getInstance().checkGroupMember(groupId, checkUserIds);
			if (checkedUserList != null) {
				HaiGroupCheckMemberProto.HaiGroupCheckMemberResponse response = HaiGroupCheckMemberProto.HaiGroupCheckMemberResponse
						.newBuilder().addAllMembersSiteUserId(checkedUserList).build();
				commandResponse.setParams(response.toByteArray());
			}

			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * 获取群成员列表
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse members(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiGroupMembersProto.HaiGroupMembersRequest request = HaiGroupMembersProto.HaiGroupMembersRequest
					.parseFrom(command.getParams());
			String groupId = request.getGroupId();
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			if (pageNum == 0 && pageSize == 0) {
				pageNum = 1;
				pageSize = GroupConfig.GROUP_MAX_MEMBER_COUNT;
			}
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (!checkGroupIdIegal(groupId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			int memberSize = UserGroupDao.getInstance().getGroupMemberCount(groupId);
			List<GroupMemberBean> memberList = UserGroupDao.getInstance().getGroupMemberList(groupId, pageNum,
					pageSize);

			HaiGroupMembersProto.HaiGroupMembersResponse.Builder responseBuilder = HaiGroupMembersProto.HaiGroupMembersResponse
					.newBuilder();
			responseBuilder.setPageTotalNum(memberSize);
			if (memberList != null) {
				for (GroupMemberBean member : memberList) {
					GroupProto.GroupMemberRole memberRole = GroupProto.GroupMemberRole.forNumber(member.getUserRole());
					UserProto.UserProfile memberProfile = UserProto.UserProfile.newBuilder()
							.setSiteUserId(member.getUserId()).setUserName(String.valueOf(member.getUserName()))
							.setUserPhoto(String.valueOf(member.getUserPhoto())).build();
					GroupProto.GroupMemberProfile groupMember = GroupProto.GroupMemberProfile.newBuilder()
							.setRole(memberRole).setProfile(memberProfile).build();
					responseBuilder.addGroupMember(groupMember);
				}
			}
			commandResponse.setParams(responseBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * /hai/group/nonmembers
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse nonmembers(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			HaiGroupNonmembersProto.HaiGroupNonmembersRequest request = HaiGroupNonmembersProto.HaiGroupNonmembersRequest
					.parseFrom(command.getParams());
			String siteUserId = request.getSiteUserId();
			String groupId = request.getGroupId();
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			LogUtils.requestDebugLog(logger, command, request.toString());

			if (!checkUserIdIegal(siteUserId) || !checkGroupIdIegal(groupId)) {
				throw new ZalyException2(ErrorCode2.ERROR_PARAMETER);
			}

			int totalNonMembersSize = UserGroupDao.getInstance().getUserFriendNonGroupMemberNum(siteUserId, groupId);
			List<GroupMemberBean> memberList = UserGroupDao.getInstance().getNonGroupMemberList(groupId, pageNum,
					pageSize);
			HaiGroupNonmembersProto.HaiGroupNonmembersResponse.Builder responseBuilder = HaiGroupNonmembersProto.HaiGroupNonmembersResponse
					.newBuilder();
			responseBuilder.setPageTotalNum(totalNonMembersSize);
			if (memberList != null) {
				for (GroupMemberBean member : memberList) {
					GroupProto.GroupMemberRole memberRole = GroupProto.GroupMemberRole.NONMEMBER;
					UserProto.UserProfile memberProfile = UserProto.UserProfile.newBuilder()
							.setSiteUserId(member.getUserId()).setUserName(String.valueOf(member.getUserName()))
							.setUserPhoto(String.valueOf(member.getUserPhoto())).build();
					GroupProto.GroupMemberProfile groupMember = GroupProto.GroupMemberProfile.newBuilder()
							.setRole(memberRole).setProfile(memberProfile).build();
					responseBuilder.addGroupMember(groupMember);
				}
			}
			commandResponse.setParams(responseBuilder.build().toByteArray());
			errCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	private GroupProto.SimpleGroupProfile getSimpleGroupProfile(SimpleGroupBean bean) {
		GroupProto.SimpleGroupProfile.Builder sgpBuilder = GroupProto.SimpleGroupProfile.newBuilder();

		if (StringUtils.isNotBlank(bean.getGroupId())) {
			sgpBuilder.setGroupId(bean.getGroupId());
		}

		if (StringUtils.isNotBlank(bean.getGroupName())) {
			sgpBuilder.setGroupName(bean.getGroupName());
		}

		if (StringUtils.isNotBlank(bean.getGroupPhoto())) {
			sgpBuilder.setGroupIcon(bean.getGroupPhoto());
		}

		return sgpBuilder.build();
	}

}