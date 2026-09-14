<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="query.name"
          placeholder="按名称搜索"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="query.categoryId"
          placeholder="按分类筛选"
          clearable
          class="search-select"
        >
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="按上架筛选" clearable class="search-select">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-select v-model="query.auditStatus" placeholder="按审核筛选" clearable class="search-select">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button type="success" :icon="Plus" @click="openAdd">发布农产品</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column label="分类" width="90">
          <template #default="{ row }">{{ categoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="origin" label="产地" min-width="150" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">{{ row.price ?? '-' }} 元/{{ row.unit || '件' }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核" width="90">
          <template #default="{ row }">
            <el-tag :type="auditType(row.auditStatus)">
              {{ auditText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.auditStatus === 0" link type="success" @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.auditStatus === 0" link type="warning" @click="handleAudit(row, 2)">驳回</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @current-change="loadData"
        @size-change="handleSearch"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑农产品' : '发布农产品'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="如：赣南脐橙" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="产地">
          <el-input v-model="form.origin" placeholder="如：江西省赣州市安远县" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="form.price" placeholder="如：12.8" style="width: 200px" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="斤 / 袋 / 盒" style="width: 200px" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面图">
          <el-upload :show-file-list="false" :http-request="handleUpload" accept="image/*">
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
            <div v-else class="upload-placeholder">
              <el-icon><Plus /></el-icon>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="经度">
          <el-input v-model="form.longitude" placeholder="地图选点后自动填入" style="width: 200px" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input v-model="form.latitude" placeholder="地图选点后自动填入" style="width: 200px" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="产品介绍" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import {
  pageAdminProduct,
  addProduct,
  updateProduct,
  deleteProduct,
  uploadImage,
  auditProduct
} from '@/api/product'
import { listCategory } from '@/api/category'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])
const query = reactive({ name: '', categoryId: null, status: null, auditStatus: null, page: 1, pageSize: 10 })

function auditText(s) {
  if (s === 1) return '已通过'
  if (s === 2) return '已驳回'
  return '待审核'
}

function auditType(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'danger'
  return 'warning'
}

async function handleAudit(row, auditStatus) {
  const action = auditStatus === 1 ? '通过' : '驳回'
  try {
    await ElMessageBox.confirm(`确定${action}「${row.name}」吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await auditProduct(row.id, auditStatus)
  ElMessage.success(`${action}成功`)
  loadData()
}

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  origin: '',
  description: '',
  price: null,
  unit: '',
  stock: 0,
  status: 1,
  coverUrl: '',
  longitude: null,
  latitude: null
})

function categoryName(id) {
  const c = categories.value.find((item) => item.id === id)
  return c ? c.name : '-'
}

function resetForm() {
  Object.assign(form, {
    id: null,
    name: '',
    categoryId: null,
    origin: '',
    description: '',
    price: null,
    unit: '',
    stock: 0,
    status: 1,
    coverUrl: '',
    longitude: null,
    latitude: null
  })
}

async function loadData() {
  loading.value = true
  try {
    const res = await pageAdminProduct(query)
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function openAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name,
    categoryId: row.categoryId,
    origin: row.origin,
    description: row.description,
    price: row.price,
    unit: row.unit,
    stock: row.stock,
    status: row.status,
    coverUrl: row.coverUrl,
    longitude: row.longitude,
    latitude: row.latitude
  })
  dialogVisible.value = true
}

async function handleUpload(options) {
  const res = await uploadImage(options.file)
  form.coverUrl = res.data
  ElMessage.success('上传成功')
}

async function submit() {
  if (!form.name) {
    ElMessage.warning('请输入产品名称')
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateProduct(form.id, form)
    } else {
      await addProduct(form)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteProduct(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  listCategory().then((res) => {
    categories.value = res.data
  })
  loadData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.search-input {
  width: 200px;
}
.search-select {
  width: 150px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
.cover-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}
.upload-placeholder {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
</style>
