package zx.soft.weibo.mapred.activeweibo;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;

import zx.soft.utils.config.ConfigUtil;
import zx.soft.utils.http.ClientDao;
import zx.soft.utils.http.HttpClientDaoImpl;
import zx.soft.weibo.mapred.source.SourceId;
import zx.soft.weibo.mapred.utils.Constant;
import zx.soft.weibo.mapred.utils.ThreadPoolExecutorUtils;

import com.google.protobuf.ServiceException;

public class ActiveThreadPoolExector {
	static {
		Properties props = ConfigUtil.getProps("super.properties");
		for (String id : props.getProperty("super_user_timeline_active").split(",")) {
			SourceId.addIdUseful(id);
		}
	}

	public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException,
			ServiceException, InterruptedException {
		ClientDao clientDao = new HttpClientDaoImpl();
		String sinceId = "3872946600794464";
		int cpuNums = 64;
		ThreadPoolExecutor pool = ThreadPoolExecutorUtils.createExecutor(cpuNums);
		while (!pool.isShutdown()) {
			if (clientDao.doGet(Constant.MAX_WEIBO_ID_GET) != null) {
				//更新since id,循环获取活跃用户微博,传入url查询USERS_LASTEST_WEIBOS表中最大微博id
				sinceId = clientDao.doGet(Constant.MAX_WEIBO_ID_GET);
			}
			for (int i = 0; i < 10000; i++) {
				pool.execute(new ActiveThread(i * 1000, sinceId, clientDao));
			}
			pool.shutdown();
			while (!pool.isTerminated()) {
				//等待子线程全部运行结束
				Thread.sleep(60_000);
			}
			pool = ThreadPoolExecutorUtils.createExecutor(cpuNums);
		}
	}

}
