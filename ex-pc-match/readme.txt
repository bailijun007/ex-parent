内存数据库，双机热备

强平，破产价 即成剩撤

自动添加交易品种

定时检查撮合是否存活，是否冲突，通过group的概念

snapshot的offset 比 matched offset 要大的情况，即撮合很快，matched消息发送很慢，在这中间时刻，做了snapshot，则此间数据将不再重发，bug

启动时要验证数据库可用