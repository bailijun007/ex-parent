/**
 * 
 */
package com.hp.sh.expv3.config.mq;

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
public class RabbitConfig2 {
	
	public static final String EXCHANGE_CIRCLE = "exchange_im_circle";

	public static final String QUEUE_TREND_ADD = "queue_trend_add";
	public static final String QUEUE_TREND_DEL = "queue_trend_del";
	
	public static final String QUEUE_COMMENT_ADD = "queue_comment_add";
	public static final String QUEUE_COMMENT_DEL = "queue_comment_del";
    
    /**
     * 分发路由器
     * @return
     */
    @Bean(EXCHANGE_CIRCLE)
    public Exchange distributeExchange(){
        return ExchangeBuilder.directExchange(EXCHANGE_CIRCLE).build();
    }

    /**
     * 动态添加队列
     * @return
     */
    @Bean(QUEUE_TREND_ADD)
    public Queue trendAddQueue(){
        return new Queue(QUEUE_TREND_ADD, false);
    }

    /**
     * 动态删除队列
     * @return
     */
    @Bean(QUEUE_TREND_DEL)
    public Queue trendDelQueue(){
        return new Queue(QUEUE_TREND_DEL, false);
    }

    /**
     * 评论添加队列
     * @return
     */
    @Bean(QUEUE_COMMENT_ADD)
    public Queue commentAddQueue(){
        return new Queue(QUEUE_COMMENT_ADD, false);
    }

    /**
     * 评论删除队列
     * @return
     */
    @Bean(QUEUE_COMMENT_DEL)
    public Queue commentDelQueue(){
        return new Queue(QUEUE_COMMENT_DEL, false);
    }
    

	/**
	 * 绑定队列到路由器
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean
	public Binding queueBinding1(
			@Qualifier(EXCHANGE_CIRCLE) Exchange exchange,
			@Qualifier(QUEUE_TREND_ADD) Queue queue
	){
		return BindingBuilder.bind(queue).to(exchange).with(QUEUE_TREND_ADD).noargs();
	}

	@Bean
	public Binding queueBinding2(
			@Qualifier(EXCHANGE_CIRCLE) Exchange exchange,
			@Qualifier(QUEUE_TREND_DEL) Queue queue
	){
		return BindingBuilder.bind(queue).to(exchange).with(QUEUE_TREND_DEL).noargs();
	}

	@Bean
	public Binding queueBinding3(
			@Qualifier(EXCHANGE_CIRCLE) Exchange exchange,
			@Qualifier(QUEUE_COMMENT_ADD) Queue queue
	){
		return BindingBuilder.bind(queue).to(exchange).with(QUEUE_COMMENT_ADD).noargs();
	}

	@Bean
	public Binding queueBinding4(
			@Qualifier(EXCHANGE_CIRCLE) Exchange exchange,
			@Qualifier(QUEUE_COMMENT_DEL) Queue queue
	){
		return BindingBuilder.bind(queue).to(exchange).with(QUEUE_COMMENT_DEL).noargs();
	}
	
}
