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
package org.cogz.web.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import org.cogz.web.converter.FeeTypeConverter;
import org.cogz.web.enums.FeeType;

/**
 * The fee entity class.
 *
 * @author Myyron Latorilla
 */
@Entity
public class Fee extends BaseEntity {

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    @Convert(converter = FeeTypeConverter.class)
    private FeeType feeType;

    @Column(nullable = false)
    private Float amount;

    private Integer refundable = 0;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getRefundable() {
        return refundable;
    }

    public void setRefundable(Integer refundable) {
        this.refundable = refundable;
    }
}
