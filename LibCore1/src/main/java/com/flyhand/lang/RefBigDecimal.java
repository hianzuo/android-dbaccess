package com.flyhand.lang;

import java.math.BigDecimal;

/**
 * Created by Ryan
 * On 2016/11/16.
 */

public class RefBigDecimal {
    private BigDecimal value;

    public RefBigDecimal(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
