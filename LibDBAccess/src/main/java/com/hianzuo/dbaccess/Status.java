package com.hianzuo.dbaccess;

/**
 * User: Ryan
 * Date: 14-4-29
 * Time: 下午2:54
 */
public enum Status {
    DEACTIVATE("未启用"),
    ACTIVATED("已启用"),
    DELETED("已删除"),
    NEW("新数据"),
    AUDITED("已审核");

    public static String SQL_AVAILABLE = " status in(?,?,?) ";
    public static String[] SQL_AVAILABLE_VAL = new String[]{ACTIVATED.name(), NEW.name(), AUDITED.name()};
    public static String[] DISABLE = new String[]{DELETED.name(), DEACTIVATE.name()};
    private String label;

    private Status(String label) {
        this.label = label;
    }

    public String getValue() {
        return name();
    }

    public String getLabel() {
        return label;
    }

    public static boolean isAvailable(Status status) {
        return
                null != status && (
                        Status.AUDITED == status ||
                                Status.ACTIVATED == status ||
                                Status.NEW == status
                );
    }
    public static boolean isAvailable(String status) {
        return
                null != status && (
                        Status.AUDITED.name().equals(status) ||
                                Status.ACTIVATED.name().equals(status) ||
                                Status.NEW.name().equals(status)
                );
    }
}
