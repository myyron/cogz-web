/*
 * Copyright 2025 Contractors of Ground Zero (CoGZ)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogz.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cogz.web.dto.WebSocketActionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author altrax
 */
@RestController
public class WebSocketController {

    Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/admin-action")
    @SendTo("/topic/client-update")
    public WebSocketActionDto adminAction(WebSocketActionDto adminAction) throws JsonProcessingException {
        logger.info("admin-action - {}", new ObjectMapper().writeValueAsString(adminAction));
        return adminAction;
    }

    @MessageMapping("/client-action")
    @SendTo("/topic/admin-update")
    public WebSocketActionDto clientAction(WebSocketActionDto clientAction) throws JsonProcessingException {
        logger.info("client-action - {}", new ObjectMapper().writeValueAsString(clientAction));
        return clientAction;
    }
}
