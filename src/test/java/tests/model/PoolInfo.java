package tests.model;

import java.util.Objects;

public class PoolInfo {
    private Integer inPool;
    private Integer inUse;

    public PoolInfo(Integer inPool, Integer inUse) {
        this.inPool = inPool;
        this.inUse = inUse;
    }

    public PoolInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PoolInfo)) {
            return false;
        }

        PoolInfo poolInfo = (PoolInfo) o;

        return Objects.equals(inPool, poolInfo.inPool)
                && Objects.equals(inUse, poolInfo.inUse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inPool, inUse);
    }

    public Integer getInPool() {
        return inPool;
    }

    public void setInPool(Integer inPool) {
        this.inPool = inPool;
    }

    public Integer getInUse() {
        return inUse;
    }

    public void setInUse(Integer inUse) {
        this.inUse = inUse;
    }

    @Override
    public String toString() {
        return "PoolInfo{" +
                "inPool=" + inPool +
                ", inUse=" + inUse +
                '}';
    }
}
