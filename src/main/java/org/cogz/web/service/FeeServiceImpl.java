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
package org.cogz.web.service;

import java.util.ArrayList;
import java.util.List;
import org.cogz.web.dto.FeeDto;
import org.cogz.web.entity.Fee;
import org.cogz.web.repo.FeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The {@link FeeService} implementation.
 *
 * @author Myyron Latorilla
 */
@Service
public class FeeServiceImpl implements FeeService {

    @Autowired
    private FeeRepository feeRepository;

    @Override
    public List<FeeDto> getAllFees() {
        List<FeeDto> result = new ArrayList<>();
        for (Fee fee : feeRepository.findAllByEnabled(1)) {
            FeeDto feeDto = new FeeDto();
            BeanUtils.copyProperties(fee, feeDto);
            result.add(feeDto);
        }
        return result;
    }

    @Override
    @Transactional
    public Long createFee(FeeDto feeDto) {
        Fee fee = new Fee();
        BeanUtils.copyProperties(feeDto, fee);
        return feeRepository.save(fee).getId();
    }

    @Override
    @Transactional
    public Long deleteFee(String itemName) {
        Fee fee = feeRepository.findByItemNameAndEnabled(itemName, 1);
        fee.setEnabled(0);
        return fee.getId();
    }
}
