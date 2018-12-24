package org.cogz.web.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author mlatorilla
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Column(nullable = false)
    protected boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
