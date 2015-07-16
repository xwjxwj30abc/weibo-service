package zx.soft.weibo.mapred.sina;

import zx.soft.utils.http.HttpClientDaoImpl;
import zx.soft.weibo.mapred.domain.UsersAndIds;
import zx.soft.weibo.mapred.utils.RequestLimitException;
import zx.soft.weibo.sina.api.SinaWeiboAPI;

public class UserFriendsDemo {

	public static void main(String[] args) throws RequestLimitException {

		SinaWeiboAPI api = new SinaWeiboAPI(new HttpClientDaoImpl());
		UsersAndIds usersAndIds = FriendshipsDetail.getFriendships(api, "2859258962", Boolean.FALSE);
		System.out.println(usersAndIds.getUsers().size());
		System.out.println(usersAndIds.getIds().size());

	}

}
