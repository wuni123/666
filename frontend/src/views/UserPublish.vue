<template>
  <div>
    <el-card class="publish-card">
      <template #header>
        <span>发布我的农产品</span>
      </template>
      <el-form :model="form" label-width="90px" style="max-width: 640px">
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
        <el-form-item label="封面图">
          <el-upload :show-file-list="false" :http-request="handleUpload" accept="image/*">
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
            <div v-else class="upload-placeholder">
              <el-icon><Plus /></el-icon>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="经度">
          <el-input v-model="form.longitude" placeholder="地图选点后自动填入，可留空" style="width: 200px" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input v-model="form.latitude" placeholder="地图选点后自动填入，可留空" style="width: 200px" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="产品介绍" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">提交审核</el-button>
          <span class="tip">提交后由村委会管理员审核，通过后才会在首页展示。</span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="my-card">
      <template #header>
        <span>我的发布</span>
      </template>
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">{{ row.price ?? '-' }} 元/{{ row.unit || '件' }}</template>
        </el-table-column>
        <el-table-column label="上架状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="110">
          <template #default="{ row }">
            <el-tag :type="auditType(row.auditStatus)">
              {{ auditText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" />
      </el-table>
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        class="pagination"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { submitProduct, myProducts, uploadImageUser } from '@/api/product'
import { listCategory } from '@/api/category'

const categories = ref([])
const submitting = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, pageSize: 10 })

const form = reactive({
  name: '',
  categoryId: null,
  origin: '',
  price: null,
  unit: '',
  stock: 0,
  coverUrl: '',
  longitude: null,
  latitude: null,
  description: ''
})

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

async function handleUpload(options) {
  const res = await uploadImageUser(options.file)
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
    await submitProduct(form)
    ElMessage.success('已提交，等待管理员审核')
    Object.assign(form, {
      name: '',
      categoryId: null,
      origin: '',
      price: null,
      unit: '',
      stock: 0,
      coverUrl: '',
      longitude: null,
      latitude: null,
      description: ''
    })
    loadData()
  } finally {
    submitting.value = false
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await myProducts(query)
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  listCategory().then((res) => {
    categories.value = res.data
  })
  loadData()
})
</script>

<style scoped>
.publish-card,
.my-card {
  margin-bottom: 16px;
}
.tip {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
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
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
