package com.internshipproject.patientregistration.config.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        registry.setMessageSizeLimit(100 * 1024 * 1024)
        registry.setSendBufferSizeLimit(100 * 1024 * 1024)
        registry.setSendTimeLimit(60000)
    }
    @Bean
    fun createServletServerContainerFactoryBean(): ServletServerContainerFactoryBean {
        val factoryBean = ServletServerContainerFactoryBean()
        factoryBean.setMaxTextMessageBufferSize( 100 * 1024 * 1024)
        factoryBean.setMaxBinaryMessageBufferSize (100 * 1024 * 1024)
        factoryBean.setMaxSessionIdleTimeout(1800000)
        factoryBean.setAsyncSendTimeout(30000)
        return factoryBean
    }
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.setApplicationDestinationPrefixes("/app")
        config.enableSimpleBroker("/chatroom","/user")
        config.setUserDestinationPrefix("/user")

    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
    }
}