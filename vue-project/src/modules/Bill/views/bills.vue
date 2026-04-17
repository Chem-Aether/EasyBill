<template>
  <div class="bill-page">
    <el-card class="bill-card">
      <div class="page-title">记账系统</div>
      <el-tabs v-model="activeTab" type="border-card" class="bill-tabs">
        <el-tab-pane label="账单查询" name="query">
          <div class="section-panel">
            <el-form :model="queryForm" inline class="query-form" label-width="90px">
              <el-form-item label="账单类型">
                <el-select v-model="queryForm.payType" placeholder="全部" clearable style="width: 150px;">
                  <el-option label="支出" :value="1" />
                  <el-option label="收入" :value="2" />
                  <el-option label="互转" :value="3" />
                </el-select>
              </el-form-item>
              <el-form-item label="账户">
                <el-select v-model="queryForm.accountId" placeholder="全部" clearable style="width: 180px;">
                  <el-option v-for="account in accounts" :key="account.id" :label="account.account_name" :value="account.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="分类">
                <el-cascader
                    v-model="queryForm.cateId"
                    :options="categories"
                    placeholder="请选择分类"
                    clearable
                    style="width: 280px"
                />
              </el-form-item>
              <el-form-item label="时间范围">
                <el-date-picker
                    v-model="queryForm.dateRange"
                    type="datetimerange"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    range-separator="至"
                    unlink-panels
                    style="width: 360px;"
                />
              </el-form-item>
              <el-form-item label="关键词">
                <el-input v-model="queryForm.keyword" placeholder="对方/商品/备注" style="width: 220px;" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="searchRecords">查询</el-button>
                <el-button @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="paginatedRecords" stripe style="width: 100%;" v-loading="loadingRecords">
            <el-table-column prop="bill_time" label="时间" width="180" />
            <el-table-column prop="pay_type" label="类型" width="100">
              <template #default="{ row }">
                <span>{{ typeLabel(row.pay_type) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="out_account_name" label="转出账户" width="160" />
            <el-table-column prop="in_account_name" label="转入账户" width="160" />
            <el-table-column prop="category_name" label="分类" width="140" />
            <el-table-column prop="amount" label="金额" width="120" />
            <el-table-column prop="counterparty_name" label="对方" width="160" />
            <el-table-column prop="commodity" label="商品/摘要" />
          </el-table>

          <div class="pagination-wrapper" v-if="records.length">
            <el-pagination
                background
                layout="prev, pager, next"
                :page-size="pageSize"
                :current-page.sync="currentPage"
                :total="records.length"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="账单记录" name="record">
          <div class="section-panel">
            <el-form :model="recordForm" label-width="110px" class="record-form">
              <el-form-item label="账单类型">
                <el-select v-model="recordForm.pay_type" placeholder="请选择" style="width: 180px;">
                  <el-option label="支出" :value="1" />
                  <el-option label="收入" :value="2" />
                  <el-option label="互转" :value="3" />
                </el-select>
              </el-form-item>
              <el-form-item label="转出账户">
                <el-select v-model="recordForm.out_account_id" placeholder="请选择" style="width: 320px;">
                  <el-option v-for="account in accounts" :key="account.id" :label="account.account_name" :value="account.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="转入账户">
                <el-select v-model="recordForm.in_account_id" placeholder="请选择" style="width: 320px;">
                  <el-option v-for="account in accounts" :key="account.id" :label="account.account_name" :value="account.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="分类">
                <el-cascader
                    v-model="recordForm.cate_id"
                    :options="categories"
                    placeholder="请选择分类"
                    style="width: 320px;"
                />
              </el-form-item>
              <el-form-item label="金额">
                <el-input-number v-model="recordForm.amount" :min="0.01" :step="0.01" controls-position="right" style="width: 180px;" />
              </el-form-item>
              <el-form-item label="交易时间">
                <el-date-picker
                    v-model="recordForm.bill_time"
                    type="datetime"
                    placeholder="选择时间"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    style="width: 320px;"
                />
              </el-form-item>
              <el-form-item label="对方/名称">
                <el-input v-model="recordForm.counterparty_name" placeholder="请输入对方或商家名称" style="width: 320px;" />
              </el-form-item>
              <el-form-item label="商品/摘要">
                <el-input v-model="recordForm.commodity" placeholder="请输入商品或摘要" style="width: 320px;" />
              </el-form-item>
              <el-form-item label="备注">
                <el-input v-model="recordForm.remark" type="textarea" placeholder="可选" style="width: 320px;" rows="3" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitRecord">保存账单</el-button>
                <el-button @click="resetRecordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane label="分类统计" name="stats">
          <div class="section-panel">
            <el-form :model="statsForm" inline class="query-form" label-width="100px">
              <el-form-item label="账单类型">
                <el-select v-model="statsForm.payType" placeholder="全部" clearable style="width: 150px;">
                  <el-option label="支出" :value="1" />
                  <el-option label="收入" :value="2" />
                  <el-option label="互转" :value="3" />
                </el-select>
              </el-form-item>
              <el-form-item label="时间范围">
                <el-date-picker
                    v-model="statsForm.dateRange"
                    type="datetimerange"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    range-separator="至"
                    unlink-panels
                    style="width: 360px;"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadStatistics">加载统计</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="statistics" stripe style="width: 100%;">
            <el-table-column prop="category_name" label="分类" width="180" />
            <el-table-column prop="category_type" label="类型" width="100">
              <template #default="{ row }">
                <span>{{ typeLabel(row.category_type) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="total_amount" label="总金额" width="140" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="分类管理" name="category">
          <div class="section-panel">
            <el-button type="primary" @click="openCategoryDialog('add')">新增分类</el-button>
          </div>
          <el-table :data="categoryList" stripe style="width: 100%;">
            <el-table-column prop="cateId" label="分类编码" width="140" />
            <el-table-column prop="className" label="分类名称" width="160" />
            <el-table-column prop="parentCate" label="父分类" width="120" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <span>{{ typeLabel(row.type) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="icon" label="图标" width="120" />
            <el-table-column prop="level" label="层级" width="100" />
            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="openCategoryDialog('edit', row)">编辑</el-button>
                <el-button type="danger" size="small" @click="deleteCategory(row.cateId)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog title="分类管理" v-model="categoryDialogVisible" width="480px">
      <el-form :model="categoryForm" label-width="110px">
        <el-form-item label="分类编码">
          <el-input v-model="categoryForm.cateId" :disabled="categoryDialogMode === 'edit'" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.className" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="categoryForm.parentCate" placeholder="请选择父分类" clearable>
            <el-option v-for="parent in parentCategories" :key="parent.cateId" :label="parent.className" :value="parent.cateId" />
          </el-select>
        </el-form-item>
        <el-form-item label="层级">
          <el-select v-model="categoryForm.level" placeholder="请选择">
            <el-option label="一级" :value="1" />
            <el-option label="二级" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="categoryForm.type" placeholder="请选择">
            <el-option label="支出" :value="1" />
            <el-option label="收入" :value="2" />
            <el-option label="互转" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="categoryForm.icon" placeholder="可填写 emoji 或字符" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  getBillRecords,
  createBillRecord,
  getCategories,
  getCategoriesTree,
  getAccounts,
  getCategoryStatistics,
  addCategory,
  updateCategory,
  removeCategory,
} from '@/modules/Bill/apis/bill.js';

const activeTab = ref('query');
const records = ref([]);
const statistics = ref([]);
const categories = ref([]);
const categoryList = ref([]);
const accounts = ref([]);
const loadingRecords = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);

const queryForm = reactive({
  payType: null,
  accountId: null,
  cateId: null,
  keyword: '',
  dateRange: [],
});

const recordForm = reactive({
  pay_type: null,
  out_account_id: null,
  in_account_id: null,
  cate_id: null,
  amount: null,
  bill_time: '',
  counterparty_name: '',
  commodity: '',
  remark: '',
});

const statsForm = reactive({
  payType: null,
  dateRange: [],
});

const categoryDialogVisible = ref(false);
const categoryDialogMode = ref('add');
const categoryForm = reactive({
  cateId: '',
  className: '',
  parentCate: '',
  level: 2,
  icon: '',
  type: 1,
});

const parentCategories = computed(() => categoryList.value.filter((item) => item.level === 1));

const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return records.value.slice(start, start + pageSize.value);
});

const typeLabel = (type) => {
  switch (type) {
    case 1: return '支出';
    case 2: return '收入';
    case 3: return '互转';
    default: return '未知';
  }
};

const loadBaseData = async () => {
  try {
    const [treeRes, accountRes, listRes] = await Promise.all([
      getCategoriesTree(),
      getAccounts(1),
      getCategories()
    ]);
    categories.value = treeRes.data || [];
    accounts.value = accountRes.data || [];
    categoryList.value = listRes.data || [];
  } catch (error) {
    ElMessage.error('加载数据失败');
  }
};

const searchRecords = async () => {
  loadingRecords.value = true;
  currentPage.value = 1;
  try {
    const params = {
      payType: queryForm.payType,
      accountId: queryForm.accountId,
      cateId: queryForm.cateId?.join('/'),
      keyword: queryForm.keyword,
    };
    if (queryForm.dateRange?.length === 2) {
      params.startTime = queryForm.dateRange[0];
      params.endTime = queryForm.dateRange[1];
    }
    const res = await getBillRecords(params);
    records.value = res.data.data || [];
  } catch (error) {
    ElMessage.error('查询账单失败');
  } finally {
    loadingRecords.value = false;
  }
};

const resetQuery = () => {
  queryForm.payType = null;
  queryForm.accountId = null;
  queryForm.cateId = null;
  queryForm.keyword = '';
  queryForm.dateRange = [];
  searchRecords();
};

const resetRecordForm = () => {
  recordForm.pay_type = null;
  recordForm.out_account_id = null;
  recordForm.in_account_id = null;
  recordForm.cate_id = null;
  recordForm.amount = null;
  recordForm.bill_time = '';
  recordForm.counterparty_name = '';
  recordForm.commodity = '';
  recordForm.remark = '';
};

const submitRecord = async () => {
  if (!recordForm.pay_type) {
    ElMessage.warning('请选择账单类型');
    return;
  }
  if (!recordForm.out_account_id) {
    ElMessage.warning('请选择转出账户');
    return;
  }
  if (!recordForm.amount) {
    ElMessage.warning('请输入金额');
    return;
  }
  if (!recordForm.bill_time) {
    ElMessage.warning('请选择时间');
    return;
  }

  try {
    await createBillRecord({
      ...recordForm,
      user_id: 1,
      cate_id: recordForm.cate_id?.at(-1)
    });
    ElMessage.success('保存成功');
    resetRecordForm();
    searchRecords();
  } catch (error) {
    ElMessage.error('保存失败');
  }
};

const loadStatistics = async () => {
  try {
    const params = { payType: statsForm.payType };
    if (statsForm.dateRange?.length === 2) {
      params.startTime = statsForm.dateRange[0];
      params.endTime = statsForm.dateRange[1];
    }
    const res = await getCategoryStatistics(params);
    statistics.value = res.data.data || [];
  } catch (error) {
    ElMessage.error('统计加载失败');
  }
};

const openCategoryDialog = (mode, row = null) => {
  categoryDialogMode.value = mode;
  if (mode === 'edit' && row) {
    categoryForm.cateId = row.cateId;
    categoryForm.className = row.className;
    categoryForm.parentCate = row.parentCate;
    categoryForm.level = row.level;
    categoryForm.icon = row.icon;
    categoryForm.type = row.type;
  } else {
    categoryForm.cateId = '';
    categoryForm.className = '';
    categoryForm.parentCate = '';
    categoryForm.level = 2;
    categoryForm.icon = '';
    categoryForm.type = 1;
  }
  categoryDialogVisible.value = true;
};

const submitCategory = async () => {
  if (!categoryForm.cateId || !categoryForm.className) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  try {
    if (categoryDialogMode.value === 'edit') {
      await updateCategory({ ...categoryForm });
      ElMessage.success('修改成功');
    } else {
      await addCategory({ ...categoryForm });
      ElMessage.success('新增成功');
    }
    categoryDialogVisible.value = false;
    await loadBaseData();
  } catch (error) {
    ElMessage.error('保存失败');
  }
};

const deleteCategory = async (cateId) => {
  try {
    await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' });
    await removeCategory(cateId);
    ElMessage.success('删除成功');
    await loadBaseData();
  } catch (error) {
    ElMessage.error('删除失败');
  }
};

onMounted(async () => {
  await loadBaseData();
  await searchRecords();
});
</script>

<style scoped>
.bill-page {
  padding: 20px;
}
.bill-card {
  min-height: 820px;
}
.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 18px;
}
.section-panel {
  margin-bottom: 20px;
}
.query-form {
  flex-wrap: wrap;
  gap: 16px;
}
.record-form {
  max-width: 760px;
}
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>