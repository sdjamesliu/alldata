package com.alibaba.tesla.appmanager.server.repository.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductReleaseAppRelDOExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ProductReleaseAppRelDOExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andGmtCreateIsNull() {
            addCriterion("gmt_create is null");
            return (Criteria) this;
        }

        public Criteria andGmtCreateIsNotNull() {
            addCriterion("gmt_create is not null");
            return (Criteria) this;
        }

        public Criteria andGmtCreateEqualTo(Date value) {
            addCriterion("gmt_create =", value, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateNotEqualTo(Date value) {
            addCriterion("gmt_create <>", value, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateGreaterThan(Date value) {
            addCriterion("gmt_create >", value, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
            addCriterion("gmt_create >=", value, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateLessThan(Date value) {
            addCriterion("gmt_create <", value, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
            addCriterion("gmt_create <=", value, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateIn(List<Date> values) {
            addCriterion("gmt_create in", values, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateNotIn(List<Date> values) {
            addCriterion("gmt_create not in", values, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateBetween(Date value1, Date value2) {
            addCriterion("gmt_create between", value1, value2, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtCreateNotBetween(Date value1, Date value2) {
            addCriterion("gmt_create not between", value1, value2, "gmtCreate");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedIsNull() {
            addCriterion("gmt_modified is null");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedIsNotNull() {
            addCriterion("gmt_modified is not null");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedEqualTo(Date value) {
            addCriterion("gmt_modified =", value, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedNotEqualTo(Date value) {
            addCriterion("gmt_modified <>", value, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedGreaterThan(Date value) {
            addCriterion("gmt_modified >", value, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
            addCriterion("gmt_modified >=", value, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedLessThan(Date value) {
            addCriterion("gmt_modified <", value, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
            addCriterion("gmt_modified <=", value, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedIn(List<Date> values) {
            addCriterion("gmt_modified in", values, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedNotIn(List<Date> values) {
            addCriterion("gmt_modified not in", values, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedBetween(Date value1, Date value2) {
            addCriterion("gmt_modified between", value1, value2, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andGmtModifiedNotBetween(Date value1, Date value2) {
            addCriterion("gmt_modified not between", value1, value2, "gmtModified");
            return (Criteria) this;
        }

        public Criteria andProductIdIsNull() {
            addCriterion("product_id is null");
            return (Criteria) this;
        }

        public Criteria andProductIdIsNotNull() {
            addCriterion("product_id is not null");
            return (Criteria) this;
        }

        public Criteria andProductIdEqualTo(String value) {
            addCriterion("product_id =", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotEqualTo(String value) {
            addCriterion("product_id <>", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThan(String value) {
            addCriterion("product_id >", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdGreaterThanOrEqualTo(String value) {
            addCriterion("product_id >=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThan(String value) {
            addCriterion("product_id <", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLessThanOrEqualTo(String value) {
            addCriterion("product_id <=", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdLike(String value) {
            addCriterion("product_id like", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotLike(String value) {
            addCriterion("product_id not like", value, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdIn(List<String> values) {
            addCriterion("product_id in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotIn(List<String> values) {
            addCriterion("product_id not in", values, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdBetween(String value1, String value2) {
            addCriterion("product_id between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andProductIdNotBetween(String value1, String value2) {
            addCriterion("product_id not between", value1, value2, "productId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdIsNull() {
            addCriterion("release_id is null");
            return (Criteria) this;
        }

        public Criteria andReleaseIdIsNotNull() {
            addCriterion("release_id is not null");
            return (Criteria) this;
        }

        public Criteria andReleaseIdEqualTo(String value) {
            addCriterion("release_id =", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdNotEqualTo(String value) {
            addCriterion("release_id <>", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdGreaterThan(String value) {
            addCriterion("release_id >", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdGreaterThanOrEqualTo(String value) {
            addCriterion("release_id >=", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdLessThan(String value) {
            addCriterion("release_id <", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdLessThanOrEqualTo(String value) {
            addCriterion("release_id <=", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdLike(String value) {
            addCriterion("release_id like", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdNotLike(String value) {
            addCriterion("release_id not like", value, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdIn(List<String> values) {
            addCriterion("release_id in", values, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdNotIn(List<String> values) {
            addCriterion("release_id not in", values, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdBetween(String value1, String value2) {
            addCriterion("release_id between", value1, value2, "releaseId");
            return (Criteria) this;
        }

        public Criteria andReleaseIdNotBetween(String value1, String value2) {
            addCriterion("release_id not between", value1, value2, "releaseId");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNull() {
            addCriterion("app_id is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("app_id is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(String value) {
            addCriterion("app_id =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(String value) {
            addCriterion("app_id <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(String value) {
            addCriterion("app_id >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(String value) {
            addCriterion("app_id >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(String value) {
            addCriterion("app_id <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(String value) {
            addCriterion("app_id <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLike(String value) {
            addCriterion("app_id like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotLike(String value) {
            addCriterion("app_id not like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<String> values) {
            addCriterion("app_id in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<String> values) {
            addCriterion("app_id not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(String value1, String value2) {
            addCriterion("app_id between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(String value1, String value2) {
            addCriterion("app_id not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andTagIsNull() {
            addCriterion("tag is null");
            return (Criteria) this;
        }

        public Criteria andTagIsNotNull() {
            addCriterion("tag is not null");
            return (Criteria) this;
        }

        public Criteria andTagEqualTo(String value) {
            addCriterion("tag =", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagNotEqualTo(String value) {
            addCriterion("tag <>", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagGreaterThan(String value) {
            addCriterion("tag >", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagGreaterThanOrEqualTo(String value) {
            addCriterion("tag >=", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagLessThan(String value) {
            addCriterion("tag <", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagLessThanOrEqualTo(String value) {
            addCriterion("tag <=", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagLike(String value) {
            addCriterion("tag like", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagNotLike(String value) {
            addCriterion("tag not like", value, "tag");
            return (Criteria) this;
        }

        public Criteria andTagIn(List<String> values) {
            addCriterion("tag in", values, "tag");
            return (Criteria) this;
        }

        public Criteria andTagNotIn(List<String> values) {
            addCriterion("tag not in", values, "tag");
            return (Criteria) this;
        }

        public Criteria andTagBetween(String value1, String value2) {
            addCriterion("tag between", value1, value2, "tag");
            return (Criteria) this;
        }

        public Criteria andTagNotBetween(String value1, String value2) {
            addCriterion("tag not between", value1, value2, "tag");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchIsNull() {
            addCriterion("baseline_git_branch is null");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchIsNotNull() {
            addCriterion("baseline_git_branch is not null");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchEqualTo(String value) {
            addCriterion("baseline_git_branch =", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchNotEqualTo(String value) {
            addCriterion("baseline_git_branch <>", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchGreaterThan(String value) {
            addCriterion("baseline_git_branch >", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchGreaterThanOrEqualTo(String value) {
            addCriterion("baseline_git_branch >=", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchLessThan(String value) {
            addCriterion("baseline_git_branch <", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchLessThanOrEqualTo(String value) {
            addCriterion("baseline_git_branch <=", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchLike(String value) {
            addCriterion("baseline_git_branch like", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchNotLike(String value) {
            addCriterion("baseline_git_branch not like", value, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchIn(List<String> values) {
            addCriterion("baseline_git_branch in", values, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchNotIn(List<String> values) {
            addCriterion("baseline_git_branch not in", values, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchBetween(String value1, String value2) {
            addCriterion("baseline_git_branch between", value1, value2, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineGitBranchNotBetween(String value1, String value2) {
            addCriterion("baseline_git_branch not between", value1, value2, "baselineGitBranch");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathIsNull() {
            addCriterion("baseline_build_path is null");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathIsNotNull() {
            addCriterion("baseline_build_path is not null");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathEqualTo(String value) {
            addCriterion("baseline_build_path =", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathNotEqualTo(String value) {
            addCriterion("baseline_build_path <>", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathGreaterThan(String value) {
            addCriterion("baseline_build_path >", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathGreaterThanOrEqualTo(String value) {
            addCriterion("baseline_build_path >=", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathLessThan(String value) {
            addCriterion("baseline_build_path <", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathLessThanOrEqualTo(String value) {
            addCriterion("baseline_build_path <=", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathLike(String value) {
            addCriterion("baseline_build_path like", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathNotLike(String value) {
            addCriterion("baseline_build_path not like", value, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathIn(List<String> values) {
            addCriterion("baseline_build_path in", values, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathNotIn(List<String> values) {
            addCriterion("baseline_build_path not in", values, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathBetween(String value1, String value2) {
            addCriterion("baseline_build_path between", value1, value2, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineBuildPathNotBetween(String value1, String value2) {
            addCriterion("baseline_build_path not between", value1, value2, "baselineBuildPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathIsNull() {
            addCriterion("baseline_launch_path is null");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathIsNotNull() {
            addCriterion("baseline_launch_path is not null");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathEqualTo(String value) {
            addCriterion("baseline_launch_path =", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathNotEqualTo(String value) {
            addCriterion("baseline_launch_path <>", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathGreaterThan(String value) {
            addCriterion("baseline_launch_path >", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathGreaterThanOrEqualTo(String value) {
            addCriterion("baseline_launch_path >=", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathLessThan(String value) {
            addCriterion("baseline_launch_path <", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathLessThanOrEqualTo(String value) {
            addCriterion("baseline_launch_path <=", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathLike(String value) {
            addCriterion("baseline_launch_path like", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathNotLike(String value) {
            addCriterion("baseline_launch_path not like", value, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathIn(List<String> values) {
            addCriterion("baseline_launch_path in", values, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathNotIn(List<String> values) {
            addCriterion("baseline_launch_path not in", values, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathBetween(String value1, String value2) {
            addCriterion("baseline_launch_path between", value1, value2, "baselineLaunchPath");
            return (Criteria) this;
        }

        public Criteria andBaselineLaunchPathNotBetween(String value1, String value2) {
            addCriterion("baseline_launch_path not between", value1, value2, "baselineLaunchPath");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}