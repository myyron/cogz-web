/*
 * Copyright 2018 Myyron Latorilla
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
import java.io.IOException;
import org.cogz.web.dto.FeeDto;
import org.cogz.web.service.FeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The fees controller.
 *
 * @author Myyron Latorilla
 */
@Controller
@RequestMapping("/settings/fees")
public class FeesController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FeeService feeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showFeesPage() {
        return "settings/fees";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getFeeList() throws JsonProcessingException {
        String result = new ObjectMapper().writeValueAsString(feeService.getAllFees());
        logger.debug("fee list: {}", result);
        return result;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Long createFee(@RequestParam String feeDto) throws IOException {
        logger.debug("create fee: {}", feeDto);
        return feeService.createFee(new ObjectMapper().readValue(feeDto, FeeDto.class));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Long deleteFee(@RequestParam String itemName) {
        logger.debug("delete fee: {}", itemName);
        return feeService.deleteFee(itemName);
    }
}
