package org.cogz.web.model;

import jakarta.persistence.MappedSuperclass;

/**
 *
 * @author altrax
 */
@MappedSuperclass
public class BaseEntity {

    private Integer enabled = 1;

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
