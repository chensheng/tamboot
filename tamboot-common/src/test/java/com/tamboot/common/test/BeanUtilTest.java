package com.tamboot.common.test;

import com.tamboot.common.tools.base.BeanUtil;
import org.junit.Assert;
import org.junit.Test;

public class BeanUtilTest {

    @Test
    public void testCopyNotNullProperties() {
        TargetBean targetBean = new TargetBean();
        targetBean.setField1("value1");
        targetBean.setField2(10);
        targetBean.setTargetField("targetField");

        DestinationBean destinationBean = new DestinationBean();
        destinationBean.setField3(9);

        BeanUtil.copyNotNullProperties(destinationBean, targetBean);

        Assert.assertEquals(targetBean.getField1(), destinationBean.getField1());
        Assert.assertEquals(targetBean.getField2(), destinationBean.getField2());
        Assert.assertEquals(Integer.valueOf(9), destinationBean.getField3());
    }

    public static class TargetBean {
        private String field1;

        private int field2;

        private Integer field3;

        private String targetField;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public int getField2() {
            return field2;
        }

        public void setField2(int field2) {
            this.field2 = field2;
        }

        public Integer getField3() {
            return field3;
        }

        public void setField3(Integer field3) {
            this.field3 = field3;
        }

        public String getTargetField() {
            return targetField;
        }

        public void setTargetField(String targetField) {
            this.targetField = targetField;
        }
    }

    public static class DestinationBean {
        private String field1;

        private int field2;

        private Integer field3;

        private String destinationField;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public int getField2() {
            return field2;
        }

        public void setField2(int field2) {
            this.field2 = field2;
        }

        public Integer getField3() {
            return field3;
        }

        public void setField3(Integer field3) {
            this.field3 = field3;
        }

        public String getDestinationField() {
            return destinationField;
        }

        public void setDestinationField(String destinationField) {
            this.destinationField = destinationField;
        }
    }
}
