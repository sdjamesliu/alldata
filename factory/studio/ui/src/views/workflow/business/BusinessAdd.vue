<template>
  <el-card class="box-card" shadow="always">
    <div slot="header" class="clearfix">
      <span>{{ title }}</span>
      <el-button-group style="float: right;">
        <el-button v-hasPerm="['workflow:business:add']" size="mini" icon="el-icon-plus" round :loading="loadingOptions.loading" :disabled="loadingOptions.isDisabled" @click="submitForm">{{ loadingOptions.loadingText }}</el-button>
        <el-button size="mini" icon="el-icon-back" round @click="showCard">返回</el-button>
      </el-button-group>
    </div>
    <div class="body-wrapper">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="业务编码" prop="businessCode">
          <el-select v-model="form.businessCode" clearable filterable placeholder="请输入业务编码" @change="changeValue($event)">
            <el-option
              v-for="item in menuOptions"
              :key="item.menuCode"
              :label="item.menuCode"
              :value="item.menuCode"
            >
              <span style="float: left">{{ '业务名称:' + item.menuName + '-业务编码:' + item.menuCode }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="业务审核用户组">
          <el-select v-model="form.businessAuditGroup" placeholder="请选择审核用户组">
            <el-option
              v-for="item in roleOptions"
              :key="item.id"
              :label="item.roleName"
              :value="item.id"
              :disabled="item.status === '0'"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="流程定义ID" prop="processDefinitionId">
          <el-input v-model="form.processDefinitionId" placeholder="请输入流程定义ID" />
        </el-form-item>
        <el-form-item label="消息模板" prop="businessTempalte">
          <el-input v-model="form.businessTempalte" type="textarea" placeholder="请输入消息模板,模板字段{nickname、datetime、businessName、businessKey}" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusOptions"
              :key="dict.id"
              :label="dict.itemText"
            >{{ dict.itemValue }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script>
import { addBusiness } from '@/api/workflow/business'
import { listMenuForFlow } from '@/api/system/menu'
import { listRole } from '@/api/system/role'

export default {
  name: 'BusinessAdd',
  props: {
    data: {
      type: Object,
      default: function() {
        return {}
      }
    }
  },
  data() {
    return {
      title: '业务流程配置新增',
      // 展示切换
      showOptions: {
        data: {},
        showList: true,
        showAdd: false,
        showEdit: false,
        showDetail: false
      },
      // 保存按钮
      loadingOptions: {
        loading: false,
        loadingText: '保存',
        isDisabled: false
      },
      // 表单参数
      form: {
        status: '1'
      },
      // 表单校验
      rules: {
        businessCode: [
          { required: true, message: '业务编码不能为空', trigger: 'blur' }
        ],
        processDefinitionId: [
          { required: true, message: '流程定义ID不能为空', trigger: 'blur' }
        ]
      },
      // 状态数据字典
      statusOptions: [],
      menuOptions: [],
      roleOptions: []
    }
  },
  created() {
    this.getDicts('sys_common_status').then(response => {
      if (response.success) {
        this.statusOptions = response.data
      }
    })
    this.getMenuOptions()
    this.getRoleList()
  },
  methods: {
    showCard() {
      this.$emit('showCard', this.showOptions)
    },
    getMenuOptions() {
      listMenuForFlow().then(response => {
        if (response.success) {
          this.menuOptions = response.data
        }
      })
    },
    getRoleList() {
      listRole().then(response => {
        if (response.success) {
          this.roleOptions = response.data
        }
      })
    },
    changeValue(value) {
      const obj = this.menuOptions.find(function(item) {
        return item.menuCode === value
      })
      this.form.businessName = obj.menuName
      this.form.businessComponent = obj.menuComponent
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.loadingOptions.loading = true
          this.loadingOptions.loadingText = '保存中...'
          this.loadingOptions.isDisabled = true
          addBusiness(this.form).then(response => {
            if (response.success) {
              this.$message.success('保存成功')
              setTimeout(() => {
                // 2秒后跳转列表页
                this.$emit('showCard', this.showOptions)
              }, 2000)
            } else {
              this.$message.error('保存失败')
              this.loadingOptions.loading = false
              this.loadingOptions.loadingText = '保存'
              this.loadingOptions.isDisabled = false
            }
          }).catch(() => {
            this.loadingOptions.loading = false
            this.loadingOptions.loadingText = '保存'
            this.loadingOptions.isDisabled = false
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.el-card ::v-deep .el-card__body {
  height: calc(100vh - 230px);
  overflow-y: auto;
}
</style>
