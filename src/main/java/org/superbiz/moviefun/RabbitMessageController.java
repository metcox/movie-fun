package org.superbiz.moviefun;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitMessageController {

    private RabbitTemplate rabbitTemplate;
    private String queue;

    public RabbitMessageController(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue}") String queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    @PostMapping("/rabbit")
    public String triggerUpdate() {



        rabbitTemplate.convertAndSend(queue,"test");

        return "Triggered !!!";
    }

}
