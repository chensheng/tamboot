package com.tamboot.mybatis.id;

public abstract class MachineIdStrategy {
    public static final long MIN_MACHINE_ID = 0;

    public static final long MAX_MACHINE_ID = 1023;

    private long machineId;

    private volatile boolean initOK;

    public long getMachineId() {
        return machineId;
    }

    public final void init() {
        if (initOK) {
            return;
        }

        machineId = generateMachineId();
        if (machineId < MIN_MACHINE_ID || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("machineId should be from 0 to 1023");
        }
        initOK = true;
    }

    /**
     * Generate machine id whose value is form {@code 0} to {@code 1023}
     * @return machine id
     */
    protected abstract long generateMachineId();
}
