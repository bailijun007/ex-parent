/**
 * 
 */
package com.hp.sh.expv3.pc.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangjg
 */
@Configuration
public class RabbitConfig {
	
	public static final String EXCHANGE_FRIEND = "exchange_im_chat";
	
	public static final String QUEUE_FRIEND_DEL = "queue_friend_del";
    
    private static final String ROUTINGKEY_FRIEND = "friend.#";

    /**
     * 声明路由器
     * @return
     */
    @Bean(EXCHANGE_FRIEND)
    public Exchange friendExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_FRIEND).build();
    }

    /**
     * 声明队列
     * @return
     */
    @Bean(QUEUE_FRIEND_DEL)
    public Queue friendDelQueue(){
        return new Queue(QUEUE_FRIEND_DEL);
    }

	/**
	 * 绑定队列到路由器
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean
	public Binding friendDelQueueBinding(
			@Qualifier(QUEUE_FRIEND_DEL) Queue queue,
			@Qualifier(EXCHANGE_FRIEND) Exchange exchange
	){
		return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_FRIEND).noargs();
	}

}
