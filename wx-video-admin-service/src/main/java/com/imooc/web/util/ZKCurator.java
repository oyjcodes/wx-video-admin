package com.imooc.web.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {

	// zk客户端
	private CuratorFramework client = null;	
	final static Logger log = LoggerFactory.getLogger(ZKCurator.class);
	
	public ZKCurator(CuratorFramework client) {
		this.client = client;
	}
	
	public void init() {
		//同一个项目放在同一个命名空间下便于管理，zookeeper上如果不存在这个空间的话会自动创建
		client = client.usingNamespace("admin");
		
		try {
			// 判断在admin命名空间下是否有bgm节点  /admin/bmg
			if (client.checkExists().forPath("/bgm") == null) {
				/**
				 * 对于zk来讲，有两种类型的节点:
				 * 持久节点: 当你创建一个节点的时候，这个节点就永远存在，除非你手动删除
				 * 临时节点: 你创建一个节点之后，会话断开，会自动删除，当然也可以手动删除
				 */
				client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT)		// 节点类型：持久节点
					.withACL(Ids.OPEN_ACL_UNSAFE)			// acl：匿名权限
					.forPath("/bgm");
				log.info("zookeeper初始化成功...");
				
				log.info("zookeeper服务器状态：{0}", client.isStarted());
			}
		} catch (Exception e) {
			log.error("zookeeper客户端连接、初始化错误...");
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description: 增加或者删除bgm，向zk-server创建子节点，供小程序后端监听
	 * operType 来自BGMOperatorTypeEnum 类型 1：添加bgm  2:删除bgm
	 */
	public void sendBgmOperator(String bgmId, String operType) {
		try {
			
			client.create().creatingParentsIfNeeded()
				.withMode(CreateMode.PERSISTENT)		// 节点类型：持久节点
				.withACL(Ids.OPEN_ACL_UNSAFE)			// acl：匿名权限
				.forPath("/bgm/" + bgmId, operType.getBytes()); // 在bgm/'bgmId'节点下添加 {operType："***"，path："***"}
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
}
