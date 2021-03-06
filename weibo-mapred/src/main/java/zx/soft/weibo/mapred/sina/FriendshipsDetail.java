package zx.soft.weibo.mapred.sina;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.weibo.mapred.domain.User;
import zx.soft.weibo.mapred.domain.UsersAndIds;
import zx.soft.weibo.mapred.source.SourceId;
import zx.soft.weibo.mapred.utils.SinaDomainUtils;
import zx.soft.weibo.sina.api.SinaWeiboAPI;
import zx.soft.weibo.sina.domain.SinaDomain;

/**
 * 用户关系详细数据：关注和粉丝信息
 *
 * @author wanggang
 *
 */
public class FriendshipsDetail {

	private static Logger logger = LoggerFactory.getLogger(FriendshipsDetail.class);

	// 每页最大返回数
	private static final int PAGE_COUNT = 200;

	// 最大返回数
	private static final int MAX_COUNT = 2000;

	// IP请求频次超过上限
	public static final String IP_REQUEST_LIMIT = "10022";

	public static UsersAndIds getFriendships(SinaWeiboAPI api, String uid, boolean isFollower) {
		SinaDomain sinaDomain = null;
		if (isFollower) {
			sinaDomain = api.friendshipsFollowers(uid, PAGE_COUNT, 0, 1);
		} else {
			sinaDomain = api.friendshipsFriends(uid, PAGE_COUNT, 0, 1);
		}
		if (sinaDomain.getFieldValue("error_code") != null) {
			logger.info("Request error_code:{},error:{}", sinaDomain.getFieldValue("error_code").toString(), sinaDomain
					.getFieldValue("error").toString());
			return null;
		}
		List<User> users = new ArrayList<>();
		List<String> ids = new ArrayList<>();
		UsersAndIds usersAndIds = SinaDomainUtils.getUsersAndIds(sinaDomain);
		if (usersAndIds.getUsers().size() > 0) {
			users.addAll(usersAndIds.getUsers());
			ids.addAll(usersAndIds.getIds());
		}
		// 循环执行抓取
		long totalNumber = (long) sinaDomain.getFieldValue("total_number");
		//cursor的取值范围为[-1,2000],超过该范围会出现如下错误;应当修改MAX_COUNT=2000;
		// Request error_code:10017,error:parameter (cursor)'s value invalid,expect (int[-1~2000]), but get (2200), see doc for more info.
		long page = totalNumber > MAX_COUNT ? MAX_COUNT / PAGE_COUNT : totalNumber / PAGE_COUNT + 1;
		for (int i = 1; i < page; i++) {
			if (isFollower) {
				sinaDomain = api.friendshipsFollowers(uid, PAGE_COUNT, i * PAGE_COUNT, 1);
			} else {
				sinaDomain = api.friendshipsFriends(uid, PAGE_COUNT, i * PAGE_COUNT, 1);
			}
			if (sinaDomain.getFieldValue("error_code") != null) {
				logger.info("Request error_code:{},error:{}", sinaDomain.getFieldValue("error_code").toString(),
						sinaDomain.getFieldValue("error").toString());
				break;
			}
			usersAndIds = SinaDomainUtils.getUsersAndIds(sinaDomain);
			if (usersAndIds.getUsers().size() > 0) {
				users.addAll(usersAndIds.getUsers());
				ids.addAll(usersAndIds.getIds());
			}
		}
		return new UsersAndIds(users, ids);
	}

	public static UsersAndIds getFriendships(SinaWeiboAPI api, String uid, boolean isFollower, String source) {
		SinaDomain sinaDomain = null;
		if (isFollower) {
			sinaDomain = api.friendshipsFollowers(uid, PAGE_COUNT, 0, 1, source);
		} else {
			sinaDomain = api.friendshipsFriends(uid, PAGE_COUNT, 0, 1, source);
		}
		if (sinaDomain.getFieldValue("error_code") != null) {
			logger.info("Request error_code:{},error:{}", sinaDomain.getFieldValue("error_code").toString(), sinaDomain
					.getFieldValue("error").toString());
			if (sinaDomain.getFieldValue("error_code").toString().equals("10017")) {
				logger.error("Parameter value invalid");
			}
			if (sinaDomain.getFieldValue("error_code").toString().equals("10022")) {
				logger.error("IP requests out of rate limit");
				SourceId.addIdUselesses(source);
				SourceId.removeIdUseful(source);
			}
			if (sinaDomain.getFieldValue("error_code").toString().equals("10011")) {
				logger.error("RPC ERROR");
			}
			return null;
		}
		List<User> users = new ArrayList<>();
		List<String> ids = new ArrayList<>();
		UsersAndIds usersAndIds = SinaDomainUtils.getUsersAndIds(sinaDomain);
		if (usersAndIds.getUsers().size() > 0) {
			users.addAll(usersAndIds.getUsers());
			ids.addAll(usersAndIds.getIds());
		}
		// 循环执行抓取
		long totalNumber = (long) sinaDomain.getFieldValue("total_number");
		long page = totalNumber > MAX_COUNT ? MAX_COUNT / PAGE_COUNT : totalNumber / PAGE_COUNT + 1;
		for (int i = 1; i < page; i++) {
			if (isFollower) {
				sinaDomain = api.friendshipsFollowers(uid, PAGE_COUNT, i * PAGE_COUNT, 1, source);
			} else {
				sinaDomain = api.friendshipsFriends(uid, PAGE_COUNT, i * PAGE_COUNT, 1, source);
			}
			if (sinaDomain.getFieldValue("error_code") != null) {
				logger.info("Request error_code:{},error:{}", sinaDomain.getFieldValue("error_code").toString(),
						sinaDomain.getFieldValue("error").toString());
				break;
			}
			usersAndIds = SinaDomainUtils.getUsersAndIds(sinaDomain);
			if (usersAndIds.getUsers().size() > 0) {
				users.addAll(usersAndIds.getUsers());
				ids.addAll(usersAndIds.getIds());
			}
		}
		return new UsersAndIds(users, ids);
	}
}
